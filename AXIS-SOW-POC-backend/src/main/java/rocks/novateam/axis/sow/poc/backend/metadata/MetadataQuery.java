package rocks.novateam.axis.sow.poc.backend.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Bag;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Seq;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import rocks.novateam.axis.sow.poc.backend.R;

public class MetadataQuery {

    /**
     * For more informations about XMP structure, see Adobe XMP Specifications
     * https://www.adobe.com/content/dam/Adobe/en/devnet/xmp/pdfs/XMPSpecificationPart1.pdf
     * https://www.adobe.com/content/dam/Adobe/en/devnet/xmp/pdfs/XMPSpecificationPart2.pdf
     * https://www.adobe.com/content/dam/Adobe/en/devnet/xmp/pdfs/XMPSpecificationPart3.pdf
     */
    private final OntModel metadata = ModelFactory.createOntologyModel();

    public MetadataQuery(File inputFile) {
        metadata.read(inputFile.getAbsolutePath());
        for(StmtIterator i = metadata.listStatements(); i.hasNext() ;) {
            System.out.println(i.nextStatement().toString());
        }
    }

    public HashMap getComments() { // Commentaires
        final String expectedTrackTypeValue = "Comment";
        HashMap comments = new HashMap();
        ArrayList tracksData = new ArrayList();

        Bag tracks = metadata.getBag(R.XMPDM.TRACKS_TAG);
        NodeIterator trackIter = tracks.iterator();
        while (trackIter.hasNext()) {
            RDFNode track = trackIter.nextNode();
            

        }

        /*
        ResIterator trackIter = tracks.getModel().listResourcesWithProperty(R.XMPDM.TRACKTYPE, expectedTrackTypeValue);
        while (trackIter.hasNext()) {
            Resource track = trackIter.nextResource();
            HashMap trackData = new HashMap();
            
            track.getModel().write(System.out);
            
            /*

            // Can get Properties frameRate and trackType right here
            Seq markers = track.getModel().getSeq(R.XMPDM.MARKERS_TAG);
            // markers.getLocalName(); // returns "markers"
            if (markers != null) {
                ResIterator markerIter = markers.getModel().listSubjects();
                while (markerIter.hasNext()) {
                    Resource marker = markerIter.nextResource();

                    marker.listProperties().toList().toString();
                    System.out.println("");


                    add(data, marker, R.XMPDM.STARTTIME);
                        add(data, marker, R.XMPDM.DURATION);
                        add(data, marker, R.XMPDM.COMMENT);
                        add(data, marker, R.XMPDM.NAME);
                }
            }          
            
            
            
            tracksData.add(trackData);
        }*/
        comments.put(tracks.getLocalName(), tracksData);
        return comments;
    }

    public ArrayList getInOuts() { // Sous-éléments
        ArrayList inOuts = new ArrayList();
        return inOuts;
    }

    public HashMap getVideoFrameRate() {
//        ArrayList videoFrameRates = new ArrayList();
//        ResIterator resIter = metadata.listSubjectsWithProperty(R.XMPDM.VIDEOFRAMERATE);
//        while (resIter.hasNext()) {
//            Resource r = resIter.nextResource();
//            HashMap data = new HashMap();
//
//            add(data, r, R.XMPDM.VIDEOFRAMERATE);
//
//            videoFrameRates.add(data);
//        }
//        return (videoFrameRates.size() > 0) ? (HashMap) videoFrameRates.get(0) : null;

        ArrayList videoFrameRates = new ArrayList();
        ResIterator resIter = metadata.listResourcesWithProperty(R.XMPDM.TRACKTYPE);
        while (resIter.hasNext()) {
            Resource r = resIter.nextResource();
            if (r.hasProperty(R.XMPDM.TRACKTYPE)
                    && r.getProperty(R.XMPDM.TRACKTYPE).getString().equals("Comment")) {
                System.out.println("");
            }
        }
        return null;
    }

    public ArrayList getDuration() {
        ArrayList duration = new ArrayList();
        ResIterator resIter = metadata.listSubjectsWithProperty(R.XMPDM.DURATION);
        while (resIter.hasNext()) {
            Resource r = resIter.nextResource();
            HashMap data = new HashMap();

            Statement stmt;
            if ((stmt = r.getProperty(R.XMPDM.DURATION)) != null) {
                if ((stmt = stmt.getProperty(R.XMPDM.VALUE)) != null) {
                    data.put("value", stmt.getString());
                }
                if ((stmt = stmt.getProperty(R.XMPDM.SCALE)) != null) {
                    data.put("scale", stmt.getString());
                }
                duration.add(data);
            }
        }
        return duration;
    }

    private void add(HashMap data, Resource r, Property prprt) {
        if (r.hasProperty(prprt)) {
            data.put(r.getProperty(prprt).getPredicate().getLocalName(),
                    r.getProperty(prprt).getString());
        }
    }

    private float formatFrameRate(String str) {
        int frameRate = Integer.parseInt(str.contains("s")
                ? str.substring(str.indexOf("f") + 1, str.indexOf("s"))
                : str.substring(str.indexOf("f") + 1));
        int rateBasis = str.contains("s")
                ? Integer.parseInt(str.substring(str.indexOf("s") + 1))
                : 1;
        return (float) frameRate / rateBasis;
    }

}
