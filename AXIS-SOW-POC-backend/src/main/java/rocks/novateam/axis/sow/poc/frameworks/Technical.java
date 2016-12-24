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
 * @author Alex Canales
 */
public class Technical {

    /**
     * The entity id of which the framework refers to.
     */
    private String id = "";

    /**
     * The file name.
     */
    private String fileName = "";

    /**
     * The file size in Mega Octet.
     */
    private String fileSize = "";

    /**
     * Hyperlink giving more information about the file or content.
     */
    private String hyperlink = "";

    /**
     * The owner or entity name who has the rights on the file.
     */
    private String rights = "";

    /**
     * The duration of the video in minutes.
     */
    private String duration = "";

    /**
     * The import date of the file.
     */
    private String importDate = "";

    // TODO: move all those URIs and SPARQL prefix in a special Register class
    /**
     * URI of the proof of concept.
     */
    private static final String POC_URI = "http://axis.sow/poc/";

    /**
     * The AXIS-CSRM datamodel uri.
     */
    private static final String DATAMODEL_URI = "http://titan.be/axis-csrm/datamodel/ontology/0.4#";

    /**
     * The CIDOC CRM uri.
     */
    private static final String CIDOC_URI = "http://www.cidoc-crm.org/cidoc-crm/";

    /**
     * The RDF URI.
     */
    private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * The RDFS URI.
     */
    private static final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * The prefix used in every SPARQL requests.
     */
    private static final String PREFIX = "PREFIX rdfs: <" + RDFS_URI + ">\n" +
            "PREFIX rdf: <" + RDF_URI + ">\n" +
            "PREFIX datamodel: <" + DATAMODEL_URI + ">\n" +
            "PREFIX cidoc: <" + CIDOC_URI + ">\n" +
            "PREFIX poc: <" + POC_URI + ">\n";

    // All property names relative to the framework
    public static String TYPE_PROPERTY = RDF_URI + "type";
    public static String FILE_NAME_PROPERTY = DATAMODEL_URI + "FileName";
    public static String FILE_SIZE_PROPERTY = DATAMODEL_URI + "fileSize";
    public static String HYPERLINK_PROPERTY = DATAMODEL_URI + "hyperlink";
    public static String RIGHTS_PROPERTY = DATAMODEL_URI + "rights";
    public static String DURATION_PROPERTY = CIDOC_URI + "P43_has_dimension";  // NOTE: should have a specific property
    public static String IMPORT_DATE_PROPERTY = DATAMODEL_URI + "date";

    /**
     * The type object value.
     */
    public static String TYPE_OBJECT = DATAMODEL_URI + "AudiovisualWork";

    /**
     * Fills the object with fake data: DELETE THIS FUNCTION.
     */
    private void fillObjectWithFakeData() {
        id = POC_URI + "Selma";
        fileName = "Selma.mp4";
        fileSize = "700";
        hyperlink = "http://www.imdb.com/title/tt1020072/";
        rights = "Pathé";
        duration = "128";
        importDate = "2016-12-22";
    }

    /**
     * Fills the model with fake data: DELETE THIS FUNCTION.
     */
    private void fillModelWithFakeData(String id) {
        String fileName = "Selma.mp4";
        String fileSize = "700";
        String hyperlink = "http://www.imdb.com/title/tt1020072/";
        String rights = "Pathé";
        String duration = "128";
        String importDate = "2016-12-22";

        Model model = FileManager.get().loadModel(
                Configuration.getInstance().getDatamodelFile(), null, "TURTLE"
        );
        Resource selma = model.createResource(id);
        selma.addProperty(model.createProperty(TYPE_PROPERTY), TYPE_OBJECT);
        selma.addProperty(model.createProperty(FILE_NAME_PROPERTY), fileName);
        selma.addProperty(model.createProperty(FILE_SIZE_PROPERTY), fileSize);
        selma.addProperty(model.createProperty(HYPERLINK_PROPERTY), hyperlink);
        selma.addProperty(model.createProperty(RIGHTS_PROPERTY), rights);
        selma.addProperty(model.createProperty(DURATION_PROPERTY), duration);
        selma.addProperty(model.createProperty(IMPORT_DATE_PROPERTY), importDate);
    }

    public Technical(String id) {
        // TODO:
        // 1. SPARQL request
        // 2. Setting everything
        fillModelWithFakeData(id);
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String exportJSONFormat() {
        String json = "{\n";
        json += "\"id\" : \"" + id + "\",\n";
        json += "\"fileName\" : \"" + fileName + "\",\n";
        json += "\"fileSize\" : " + fileSize + ",\n";
        json += "\"hyperLink\" : \"" + hyperlink + "\",\n";
        json += "\"rights\" : \"" + rights + "\",\n";
        json += "\"duration\" : " + duration + ",\n";
        json += "\"importDate\" : \"" + importDate + "\"\n";
        json += "}";
        return json;
    }

    public static void main(String[] args) throws IOException {
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

        // Testing JSON exportation
        Technical framework = new Technical(filmIdURI);
        System.out.println(framework.exportJSONFormat());
    }
}
