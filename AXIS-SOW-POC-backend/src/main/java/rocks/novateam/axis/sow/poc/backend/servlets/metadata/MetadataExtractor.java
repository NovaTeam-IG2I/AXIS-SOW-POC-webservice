package rocks.novateam.axis.sow.poc.backend.servlets.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rocks.novateam.axis.sow.poc.backend.servlets.metadata.ProcessHandler.IOStream;
import static rocks.novateam.axis.sow.poc.backend.servlets.metadata.ProcessHandler.startProcess;

public class MetadataExtractor {

    private static final String EXIF_TOOL_PATH = "/usr/local/bin/exiftool";

    public MetadataExtractor() {
    }

    public static void extractToXMPSidecarFile(File file) throws IOException {
        IOStream streams = null;
        List<String> args = new ArrayList<>();

        checkFile(file);

        args.add(EXIF_TOOL_PATH);
        args.add(file.getAbsolutePath());
        args.add("-out"); // Set output file or directory name
        args.add(modifyExtension(file.getAbsolutePath(), ".xmp"));
        args.add("-quiet"); // Quiet processing
        // args.add("-overwrite_original"); // Overwrite original by renaming tmp file

        streams = startProcess(args);

        streams.close();
    }

    /**
     * Checks if the file is valid and can be opened
     * @param file
     * @throws IllegalArgumentException
     * @throws SecurityException 
     */
    private static void checkFile(File file) throws IllegalArgumentException, SecurityException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null and must be a valid stream.");
        }

        if (!file.canRead()) {
            throw new SecurityException("Unable to read the given file ["
                    + file.getAbsolutePath()
                    + "], ensure that the file exists at the given path and that the executing Java process has permissions to read it.");
        }
    }

    /**
     * Returns filename with the extension replaced by the given one
     * @param filename
     * @param extension
     * @return 
     */
    private static String modifyExtension(String filename, String extension) {
        return filename.substring(0, filename.lastIndexOf('.'))
                .concat(extension);
    }

}
