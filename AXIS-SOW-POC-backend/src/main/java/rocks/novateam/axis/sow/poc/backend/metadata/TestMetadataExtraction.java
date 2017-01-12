package rocks.novateam.axis.sow.poc.backend.metadata;

import java.io.File;

public class TestMetadataExtraction {

    private static final String FILE_PATH = "/Users/Geoffrey/Desktop/video.mp4";

    public static void main(String[] args) {
        MetadataHandler.extractAndStoreMetadata(new File(FILE_PATH));
    }

}
