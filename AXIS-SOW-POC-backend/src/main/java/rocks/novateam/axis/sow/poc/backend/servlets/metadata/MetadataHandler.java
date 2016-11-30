/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.servlets.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import org.xml.sax.SAXException;

public class MetadataHandler {

    public static void main(final String[] args) throws Exception {
        String fileLocation = "/Users/Geoffrey/Desktop/video.mp4";

        //create a file object and assume it is in your current directory
        File file = new File(fileLocation);

        Metadata metadata = getMetadata(file);

        printMetadata(metadata);
    }

    /**
     * Returns the metadata included in a given file
     * @param file
     * @return Metadata object
     * @throws IOException
     * @throws SAXException
     * @throws TikaException 
     */
    public static Metadata getMetadata(File file) throws IOException, SAXException, TikaException {
        // Parameters of parse() method
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(file);
        ParseContext context = new ParseContext();

        // Parsing the given file
        parser.parse(inputstream, handler, metadata, context);

        return metadata;
    }

    /**
     * Prints the metadata included in a given file
     * @param file
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    public static void printMetadata(File file) throws IOException, SAXException, TikaException {
        printMetadata(getMetadata(file));
    }

    /**
     * Prints the metadata included in a given metadata object
     * @param metadata
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    private static void printMetadata(Metadata metadata) throws IOException, SAXException, TikaException {
        String[] metadataNames = metadata.names(); // getting the list of all meta data elements 
        for (String name : metadataNames) {
            System.out.println(name + ": " + metadata.get(name));
        }
    }

}
