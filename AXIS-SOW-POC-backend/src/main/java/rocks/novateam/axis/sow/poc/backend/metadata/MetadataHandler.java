package rocks.novateam.axis.sow.poc.backend.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import rocks.novateam.axis.sow.poc.backend.Configuration;

import rocks.novateam.axis.sow.poc.backend.helpers.ProcessHandler.IOStream;
import static rocks.novateam.axis.sow.poc.backend.helpers.ProcessHandler.startProcess;

public class MetadataHandler {

    private static final String EXIF_TOOL_PATH = Configuration.getInstance().getExiftoolPath();
    private static final String XMP_FILE_EXTENSION = ".xmp";
    private static final String RDF_FILE_EXTENSION = ".rdf";

    public static void extractAndStoreMetadata(File inputFile) {
        try {
            storeInTDB(extractRDF(extractXMP(inputFile)));
        } catch (Exception e) { // Should not happen
            System.out.println("An error occured during the metadata extraction process");
        }
    }

    /**
     * Returns XMP Sidecar file containing all the meta extracted
     *
     * @param inputFile any file handled by exiftool
     * @return XMP File
     */
    public static File extractXMP(File inputFile) {
        IOStream streams;
        List<String> args = new ArrayList<>();

        checkFile(inputFile);

        File xmpFile = new File(modifyExtension(inputFile, XMP_FILE_EXTENSION));

        if (xmpFile.isFile()) {
            xmpFile.delete();
        }

        args.add(EXIF_TOOL_PATH);
        args.add(inputFile.getAbsolutePath());
        args.add("-out"); // Set output inputFile or directory name
        args.add(xmpFile.getAbsolutePath());
        args.add("-xmp"); // Extract XMP data
        args.add("-b"); // Output metadata in binary format
        args.add("-xmlFormat"); // Use RDF/XML output format
        args.add("-quiet"); // Quiet processing

        streams = startProcess(args);
        streams.close();

        return xmpFile.isFile() ? xmpFile : null;
    }

    /**
     * Replaces the str of a f
     *
     * @param f
     * @param str
     * @return
     */
    private static String modifyExtension(File f, String str) {
        return f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf('.')).concat(str);
    }

    /**
     * Reads RDF/XML file using Jena Model
     *
     * @param rdfFile RDF File
     */
    public static void read(File rdfFile) {
        checkFile(rdfFile);
        Model model = ModelFactory.createDefaultModel(); // Create an empty model
        InputStream in = FileManager.get().open(rdfFile.getAbsolutePath());
        try {
            model.read(in, ""); // read the RDF/XML file
            model.write(System.out); // write it to standard out
        } catch (Exception e) { // throws an error if file doesn't contain only RDF
            throw new Error("Unvalid file content");
        } finally {
            model.close();
        }
    }

    /**
     *
     * @param rdfFile
     * @param model
     * @return
     * @throws FileNotFoundException
     */
    public static File write(File rdfFile, Model model) throws FileNotFoundException {
        OutputStream out = new FileOutputStream(rdfFile);
        model.write(out, "RDF/XML-ABBREV");
        return rdfFile.isFile() ? rdfFile : null;
    }

    /**
     * Extracts RDF content from a given file to a RDF file
     *
     * @param xmpFile File that contains the metadata
     * @return RDF File containing only RDF content
     */
    public static File extractRDF(File xmpFile) {
        checkFile(xmpFile);
        File rdfFile = new File(modifyExtension(xmpFile, RDF_FILE_EXTENSION));
        if (rdfFile.isFile()) {
            rdfFile.delete();
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(xmpFile));
            PrintWriter pw = new PrintWriter(new FileWriter(rdfFile));
            String line;
            Boolean inRDFTag = false;
            while ((line = br.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("<rdf:RDF")) {
                    inRDFTag = true;
                }
                if (inRDFTag) {
                    pw.println(line);
                    pw.flush();
                }
                if (trimmedLine.startsWith("</rdf:RDF")) {
                    break;
                }
            }
            pw.close();
            br.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return rdfFile;
    }

    /**
     * Stores RDF file in TDB
     *
     * @param rdfFile RDF File
     */
    public static void storeInTDB(File rdfFile) {
        // TODO : READ THE FILE UNTIL TDB IS READY
        checkFile(rdfFile);
        Model model = ModelFactory.createDefaultModel(); // Create an empty model
        InputStream in = FileManager.get().open(rdfFile.getAbsolutePath());
        try {
            model.read(in, ""); // read the RDF/XML file
            model.write(System.out); // write it to standard out
        } catch (Exception e) { // throws an error if file doesn't contain only RDF
            throw new Error("Unvalid file content");
        } finally {
            model.close();
        }
    }

    /**
     *
     * @param f
     */
    private static void checkFile(File f) {
        if (f == null || !f.isFile()) {
            throw new Error("Error with the file");
        }
    }

}
