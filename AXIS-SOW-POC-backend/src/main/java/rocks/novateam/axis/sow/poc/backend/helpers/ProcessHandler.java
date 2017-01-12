package rocks.novateam.axis.sow.poc.backend.helpers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

public class ProcessHandler {

    public static IOStream startProcess(List<String> args)
            throws RuntimeException {
        Process proc = null;
        IOStream streams = null;
        
        System.out.println("Will execute the following command : " 
                + args.toString().replace(",",""));

        try {
            proc = new ProcessBuilder(args).start();
            proc.waitFor(); // Wait until the process is completed
        } catch (Exception e) {
            String message = "Unable to start external process";
            System.out.println(message);
            throw new RuntimeException(message, e);
        }

        // Setup read/write streams to the external process.
        streams = new IOStream(new BufferedReader(new InputStreamReader(
                proc.getInputStream())), new OutputStreamWriter(
                proc.getOutputStream()));

        return streams;
    }

    public static class IOStream {

        BufferedReader reader;
        OutputStreamWriter writer;

        public IOStream(BufferedReader reader, OutputStreamWriter writer) {
            this.reader = reader;
            this.writer = writer;
        }

        public void close() {
            try { // Read Stream
                reader.close();
            } catch (Exception e) {
                // no-op, just try to close it.
            }

            try { // Write Stream
                writer.close();
            } catch (Exception e) {
                // no-op, just try to close it.
            }

            // Null the streams references.
            reader = null;
            writer = null;
        }
    }

}
