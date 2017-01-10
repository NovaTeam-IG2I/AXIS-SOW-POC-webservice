package rocks.novateam.axis.sow.poc.frameworks;

import java.io.IOException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import rocks.novateam.axis.sow.poc.backend.Configuration;
import rocks.novateam.axis.sow.poc.backend.R;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;

/**
 * This class holds all informations about the technical framework.
 *
 * A technical framework describes all technical information of a file like its
 * name, size...
 *
 * So far, it can only have one data by field (for example, only one right
 * owner).
 *
 * @author Alex Canales
 */
public class Technical {

    /**
     * Holds the model, should be deleted when it will be possible to get the
     * model correctly. Do not use this field directly, use getModel function.
     */
    private Model model = null;

    /**
     * The entity id of which the framework refers to.
     */
    private String id = "";

    /**
     * The duration of the video in minutes.
     */
    private String duration = "";

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
     * The import date of the file.
     */
    private String importDate = "";

    /**
     * The owner or entity name who has the rights on the file.
     */
    private String rights = "";

    // ---- Begin framework ontology property and value defintion
    /**
     * The type property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>rdf:type</code></li>
     * <li><strong>DOMAIN</strong>: None specified</li>
     * <li><strong>RANGE</strong>: None specified</li>
     * </ul>
     */
    public static String TYPE_PROPERTY = R.RDF_URI + "type";

    /**
     * The duration property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P43_has_dimension</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E70_Thing</code> (subclass of <code>axis:Register</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E54_Dimensions</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String DURATION_PROPERTY = R.CIDOC_URI + "P43_has_dimension";  // NOTE: should have a specific property

    /**
     * The file name property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:fileName</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:MediaEmbodiment</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:string</code></li>
     * </ul>
     */
    public static String FILE_NAME_PROPERTY = R.DATAMODEL_URI + "fileName";

    /**
     * The file name property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:fileSize</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:MediaEmbodiment</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:float</code></li>
     * </ul>
     */
    public static String FILE_SIZE_PROPERTY = R.DATAMODEL_URI + "fileSize";

    /**
     * The hyperlink property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:hyperlink</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:Location</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:string</code></li>
     * </ul>
     */
    public static String HYPERLINK_PROPERTY = R.DATAMODEL_URI + "hyperlink";

    /**
     * The import date property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:date</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:EventAnnotation</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:date</code></li>
     * </ul>
     */
    public static String IMPORT_DATE_PROPERTY = R.DATAMODEL_URI + "date";

    /**
     * The import date property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P75i_is_possessed_by</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E30_Right</code> (subclass of <code>axis:Register</code>)</li>
     * <li><strong>RANGE</strong>: <code>axis:Agent</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String RIGHTS_PROPERTY = R.DATAMODEL_URI + "P75i_is_possessed_by";  // NOTE: AXIS does not have anything specific?
    // ---- End framework ontology property and value defintion

    /**
     * The type object value.
     */
    public static String TYPE_OBJECT = R.DATAMODEL_URI + "AudiovisualWork";

    /**
     * Fills the object with fake data: for test purpose, can be deleted.
     */
    private void fillObjectWithFakeData() {
        id = R.POC_URI + "Selma";
        fileName = "Selma.mp4";
        fileSize = "700";
        hyperlink = "http://www.imdb.com/title/tt1020072/";
        rights = "Pathé";
        duration = "128";
        importDate = "2016-12-22";
    }

    /**
     * Fills the model with fake data: for test purpose, can be deleted.
     */
    private void fillModelWithFakeData(String id) {
        String fileName = "Selma.mp4";
        String fileSize = "700";
        String hyperlink = "http://www.imdb.com/title/tt1020072/";
        String rights = "Pathé";
        String duration = "128";
        String importDate = "2016-12-22";

        Model model = getModel();
        Resource resource = model.createResource(id);
        resource.addProperty(model.createProperty(TYPE_PROPERTY), TYPE_OBJECT);
        resource.addProperty(model.createProperty(FILE_NAME_PROPERTY), fileName);
        resource.addProperty(model.createProperty(FILE_SIZE_PROPERTY), fileSize);
        resource.addProperty(model.createProperty(HYPERLINK_PROPERTY), hyperlink);
        resource.addProperty(model.createProperty(RIGHTS_PROPERTY), rights);
        resource.addProperty(model.createProperty(DURATION_PROPERTY), duration);
        resource.addProperty(model.createProperty(IMPORT_DATE_PROPERTY), importDate);
    }

    /**
     * This class holds all informations about the technical framework. Data are
     * automatically loaded from the TDB. If no data are not found, the
     * framework holds empty information.
     *
     * @param id The entity id of which the framework refers to. If null, the
     * data holds empty information.
     */
    public Technical(String id) {
        if(id == null)
            return;
        fillModelWithFakeData(id);
        retrieveData(id);
    }

    /**
     * Returns the model used. It should be replaced when good CRUD methods will
     * be implemented.
     *
     * @return The model used.
     */
    private Model getModel() {
        if(model == null) {
            if(FileManager.get() == null)
                System.out.println("Return null from FileManger.get()");
            model = FileManager.get().loadModel(
                Configuration.getInstance().getDatamodelFile(), null, "TURTLE"
            );
        }
        return model;
    }

    /**
     * Retrieves all the technical framework data.
     *
     * @param id The entity id the framewok is associated with.
     */
    private void retrieveData(String id)
    {
        String FILE_NAME_SELECT = "fileName";
        String FILE_SIZE_SELECT = "fileSize";
        String HYPERLINK_SELECT = "hyperlink";
        String RIGHTS_SELECT = "rights";
        String DURATION_SELECT = "duration";
        String IMPORT_DATE_SELECT = "importDate";

        String queryString = R.PREFIX + "SELECT " +
                "?" + FILE_NAME_SELECT + " ?" + FILE_SIZE_SELECT + " " +
                "?" + HYPERLINK_SELECT + " ?" + RIGHTS_SELECT + " " +
                "?" + DURATION_SELECT + " ?" + IMPORT_DATE_SELECT + " " +
                "WHERE \n{\n" +
                "<" + id + "> <" + FILE_NAME_PROPERTY + "> ?" + FILE_NAME_SELECT + ".\n" +
                "<" + id + "> <" + FILE_SIZE_PROPERTY + "> ?" + FILE_SIZE_SELECT + ".\n" +
                "<" + id + "> <" + HYPERLINK_PROPERTY + "> ?" + HYPERLINK_SELECT + ".\n" +
                "<" + id + "> <" + RIGHTS_PROPERTY + "> ?" + RIGHTS_SELECT + ".\n" +
                "<" + id + "> <" + DURATION_PROPERTY + "> ?" + DURATION_SELECT + ".\n" +
                "<" + id + "> <" + IMPORT_DATE_PROPERTY + "> ?" + IMPORT_DATE_SELECT + ".\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, getModel())) {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution solution = results.nextSolution();

                // Should maybe use Literal
                fileName = solution.getLiteral(FILE_NAME_SELECT).toString();
                fileSize = solution.getLiteral(FILE_SIZE_SELECT).toString();
                hyperlink = solution.getLiteral(HYPERLINK_SELECT).toString();
                rights = solution.getLiteral(RIGHTS_SELECT).toString();
                duration = solution.getLiteral(DURATION_SELECT).toString();
                importDate = solution.getLiteral(IMPORT_DATE_SELECT).toString();
            }
        }
        this.id = id;
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String exportJSONFormat() {
        // Not using a JsonObjectBuilder because of build error problems
        // java.lang.ClassNotFoundException for javax.json.Json
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
        String filmIdURI = R.POC_URI + "Selma";
        Technical framework = new Technical(filmIdURI);
        System.out.println(framework.exportJSONFormat());
    }
}
