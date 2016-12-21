package rocks.novateam.axis.sow.poc.frameworks;

import java.io.IOException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import rocks.novateam.axis.sow.poc.backend.Configuration;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

/**
 * This class holds all informations about the technical framework.
 *
 * A technical framework describes all technical information of a file like its
 * name, size...
 *
 * @author alex
 */
public class Technical {

    private String fileName = "";

    /**
     * Fills the object with fake data: DELETE THIS FUNCTION.
     */
    private void fillWithFakeData() {
        fileName = "Selma";
    }

    public Technical(String register) {
        // TODO:
        // 1. SPARQL request
        // 2. Setting everything
        fillWithFakeData();
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String exportJSONFormat() {
        // TODO
        return " { fileName : \"" + fileName + "\" }";
    }

    public static void main(String[] args) throws IOException {
        // URIs and SPARQL prefix, should be moved in a special class
        final String POC_URI = "http://axis.sow/poc/";
        final String DATAMODEL_URI = "http://titan.be/axis-csrm/datamodel/ontology/0.4#";
        final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#";
        final String PREFIX = "PREFIX rdfs: <" + RDFS_URI + ">\n" +
                "PREFIX rdf: <" + RDF_URI + ">\n" +
                "PREFIX datamodel: <" + DATAMODEL_URI + ">\n" +
                "PREFIX poc: <" + POC_URI + ">\n";  // NOTE: Have to be decided

        // Data test definition
        String filmId = "Selma";
        String fileName = "Selma.mp4";
        String filmIdURI = POC_URI + filmId;
        String fileNamePropertyURI = DATAMODEL_URI + "FileName";

        // Inserting data in the model
        Model model = FileManager.get().loadModel(
                Configuration.getInstance().getDatamodelFile(), null, "TURTLE"
        );
        Resource selma = model.createResource(filmIdURI);
        Property isA = model.createProperty(RDF_URI + "type");
        selma.addProperty(isA, DATAMODEL_URI + "AudiovisualWork");
        Property fileNamePro = model.createProperty(fileNamePropertyURI);
        selma.addProperty(fileNamePro, fileName);

        // model.write(System.out);  // Printing the data (should be saved)

        // Looking for information
        String queryString = PREFIX + "SELECT ?fileName WHERE \n" +
                "{ <" + filmIdURI + "> <" + fileNamePropertyURI + "> ?fileName. }";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution solution = results.nextSolution();
                System.out.println(solution.toString());
                Literal name = solution.getLiteral("fileName");
                System.out.println(name);
            }
        }
    }
}
