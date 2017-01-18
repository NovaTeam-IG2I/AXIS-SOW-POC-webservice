package rocks.novateam.axis.sow.poc.backend.metadata;

import java.io.File;

public class TestMetadataQuery {

    private static final String MP4_FILE_PATH = System.getProperty("user.home") + "/Desktop/selma.mp4";
    private static final String RDF_FILE_PATH = System.getProperty("user.home") + "/Desktop/selma.rdf";

    public static void main(String[] args) {
        /**
         * Uncomment if you haven't already generated a RDF file
         *
         * - Please modify MP4_FILE_PATH to match the file you want to have
         * metadata extracted from - Make sure exiftool is installed on your
         * system
         */
        // MetadataHandler.extractAndStoreMetadata(new File(MP4_FILE_PATH));
        /**
         * - Please modify RDF_FILE_PATH to match the rdf file you want to query
         * the metadata from - Make sure exiftool is installed on your system
         *
         * Will generate xmp file containing extracted metadata, then generate a
         * rdf file from it, and finally store the rdf/xml content in the TDB
         */
        MetadataQuery meta = new MetadataQuery(new File(RDF_FILE_PATH));
        System.out.println(meta.getVideoFrameRate().toString());
        System.out.println(meta.getComments().toString());
    }

}
