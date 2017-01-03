package rocks.novateam.axis.sow.poc.backend.servlets.metadata;

import java.io.File;
import java.io.IOException;

public class TestMetadataExtraction {
    
    private static final String FILE_PATH = "/Users/Geoffrey/Desktop/video.mp4";

    public static void main(String[] args) throws IOException {
        File file = new File(FILE_PATH);
        MetadataExtractor.extractToXMPSidecarFile(file);
    }

}
