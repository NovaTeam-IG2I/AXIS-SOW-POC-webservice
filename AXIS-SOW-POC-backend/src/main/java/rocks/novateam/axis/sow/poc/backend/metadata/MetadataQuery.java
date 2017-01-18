package rocks.novateam.axis.sow.poc.backend.metadata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import rocks.novateam.axis.sow.poc.backend.R;

public class MetadataQuery {

    /**
     * https://jena.apache.org/tutorials/rdf_api.html
     */
    private final Model metadata = ModelFactory.createDefaultModel();

    public MetadataQuery(File inputFile) {
        metadata.read(inputFile.getAbsolutePath());
    }

    public ArrayList getComments() { // Commentaires
        ArrayList comments = new ArrayList();
        ResIterator iter = metadata.listSubjectsWithProperty(R.XMPDM.NAME);
        while (iter.hasNext()) {
            Resource r = iter.nextResource();
            HashMap data = new HashMap();
            if (r.getProperty(R.XMPDM.STARTTIME) != null) {
                data.put("startTime", r.getProperty(R.XMPDM.STARTTIME).getString());
            }
            if (r.getProperty(R.XMPDM.DURATION) != null) {
                data.put("duration", r.getProperty(R.XMPDM.DURATION).getString());
            }
            if (r.getProperty(R.XMPDM.COMMENT) != null) {
                data.put("comment", r.getProperty(R.XMPDM.COMMENT).getString());
            }
            if (r.getProperty(R.XMPDM.NAME) != null) {
                data.put("name", r.getProperty(R.XMPDM.NAME).getString());
            }
            comments.add(data);
        }
        return comments;
    }

    public ArrayList getInOuts() { // Sous-éléments
        ArrayList inOuts = new ArrayList();
        return inOuts;
    }

    public ArrayList getVideoFrameRate() {
        ArrayList videoFrameRate = new ArrayList();
        ResIterator iter = metadata.listSubjectsWithProperty(R.XMPDM.VIDEOFRAMERATE);
        while (iter.hasNext()) {
            Resource r = iter.nextResource();
            HashMap data = new HashMap();
            if (r.getProperty(R.XMPDM.VIDEOFRAMERATE) != null) {
                data.put("videoFrameRate", r.getProperty(R.XMPDM.VIDEOFRAMERATE).getString());
            }
            videoFrameRate.add(data);
        }
        return videoFrameRate;
    }

    public ArrayList getDuration() {
        ArrayList duration = new ArrayList();
        ResIterator iter = metadata.listSubjectsWithProperty(R.XMPDM.DURATION);
        while (iter.hasNext()) {
            Resource r = iter.nextResource();
            HashMap data = new HashMap();
            if (r.getProperty(R.XMPDM.DURATION) != null) {
                data.put("duration", r.getProperty(R.XMPDM.DURATION).getString());
            }
            duration.add(data);
        }
        return duration;
    }

}
