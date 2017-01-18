package rocks.novateam.axis.sow.poc.backend.metadata;

import java.io.File;

public class TestMetadataHandler {

    private static final String FILE_PATH = System.getProperty("user.home") + "/Desktop/video.mp4";

    public static void main(String[] args) {
        /**
         * - Please modify FILE_PATH to match the file you want to have metadata
         * extracted from - Make sure exiftool is installed on your system
         *
         * Will generate xmp file containing extracted metadata, then generate a
         * rdf file from it, and finally store the rdf/xml content in the TDB
         */
        MetadataHandler.extractAndStoreMetadata(new File(FILE_PATH));
    }

}
