package rocks.novateam.axis.sow.poc.backend.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import rocks.novateam.axis.sow.poc.backend.Configuration;

import rocks.novateam.axis.sow.poc.backend.helpers.ProcessHandler.IOStream;
import static rocks.novateam.axis.sow.poc.backend.helpers.ProcessHandler.startProcess;

public class MetadataExtractor {

    private static final String EXIF_TOOL_PATH = Configuration.getInstance().getExiftoolPath();

    public static void extractAndStoreMetadata(File inputFile) {
        storeInTDB(extractRDF(extractXMP(inputFile)));
    }

    /**
     * Returns XMP Sidecar file containing all the meta extracted
     *
     * @param inputFile
     * @return
     */
    private static File extractXMP(File inputFile) {
        IOStream streams = null;
        List<String> args = new ArrayList<>();

        checkFile(inputFile);

        File outputFile = new File(modifyExtension(inputFile, ".xmp"));

        if (outputFile.isFile()) {
            outputFile.delete();
        }

        args.add(EXIF_TOOL_PATH);
        args.add(inputFile.getAbsolutePath());
        args.add("-out"); // Set output inputFile or directory name
        args.add(outputFile.getAbsolutePath());
        args.add("-xmp"); // Extract XMP data
        args.add("-b"); // Output metadata in binary format
        args.add("-xmlFormat"); // Use RDF/XML output format
        args.add("-quiet"); // Quiet processing

        streams = startProcess(args);
        streams.close();
        
        return outputFile.isFile() ? outputFile : null;
    }

    /**
     * Replaces the extension of a file
     *
     * @param file
     * @param extension
     * @return
     */
    private static String modifyExtension(File file, String extension) {
        return file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf('.')).concat(extension);
    }

    /**
     * Reads RDF/XML file using Jena Model
     *
     * @param inputFile RDF File
     */
    private static void read(File inputFile) {
        checkFile(inputFile);
        Model model = ModelFactory.createDefaultModel(); // Create an empty model
        InputStream in = FileManager.get().open(inputFile.getAbsolutePath());
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
     * Extracts RDF content from a given file to a RDF file
     *
     * @param inputFile File that contains the metadata
     * @return RDF File containing only RDF content
     */
    private static File extractRDF(File inputFile) {
        checkFile(inputFile);
        File outputFile = new File(modifyExtension(inputFile, ".rdf"));
        if (outputFile.isFile()) {
            outputFile.delete();
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
            String line;
            Boolean inRDF = false;
            while ((line = br.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.startsWith("<rdf:RDF")) {
                    inRDF = true;
                }
                if (inRDF) {
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
        return outputFile;
    }

    /**
     * Stores RDF file in TDB
     *
     * @param inputFile RDF File
     */
    private static void storeInTDB(File inputFile) {
        read(inputFile);

//        Dataset dataset = TDBManager.getInstance().getDataset(); //
//        Model model = dataset.getDefaultModel(); //
//        // Model model = ModelFactory.createDefaultModel(); // Create an empty model
//        InputStream in = FileManager.get().open(inputFile.getAbsolutePath());
//        //if (in == null) {
//        //    throw new IllegalArgumentException("File not found");
//        //}
//        FileManager.get().readModel(model, inputFile.getAbsolutePath());
//        // model.read(in, ""); // read the RDF/XML file
//        model.write(System.out); // write it to standard out
//        dataset.commit();
//        model.close();
//        dataset.close();
    }
    
    private static void checkFile(File file){
        if (file == null || !file.isFile()) {
            throw new Error("Error with the file");
        }
    }

}
