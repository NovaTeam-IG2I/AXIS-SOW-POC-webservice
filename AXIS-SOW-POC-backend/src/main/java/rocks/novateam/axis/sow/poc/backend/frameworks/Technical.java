package rocks.novateam.axis.sow.poc.backend.frameworks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import rocks.novateam.axis.sow.poc.backend.ontology.RegisterManager;

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
     * Fake name used for testing purpose, do not use it. Should be deleted.
     *
     * The name has to start in lower case since it is converted in camel case.
     */
    public static String FAKE_NAME = "selma";

    /**
     * Holds the model, should be deleted when it will be possible to get the
     * model correctly. Do not use this field directly, use getModel function.
     */
    private Model model = null;

    /**
     * The entity uri of which the framework refers to.
     */
    private String uri = "";

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
     * The type property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>rdf:type</code></li>
     * <li><strong>DOMAIN</strong>: None specified</li>
     * <li><strong>RANGE</strong>: None specified</li>
     * </ul>
     */
    public static String TYPE_PROPERTY = R.RDF_NS + "type";

    /**
     * The duration property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P43_has_dimension</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E70_Thing</code> (subclass of <code>axis:Register</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E54_Dimensions</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String DURATION_PROPERTY = R.CIDOC_NS + "P43_has_dimension";  // NOTE: should have a specific property

    /**
     * The file name property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:fileName</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:MediaEmbodiment</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:string</code></li>
     * </ul>
     */
    public static String FILE_NAME_PROPERTY = R.DATAMODEL_NS + "fileName";

    /**
     * The file name property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:fileSize</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:MediaEmbodiment</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:float</code></li>
     * </ul>
     */
    public static String FILE_SIZE_PROPERTY = R.DATAMODEL_NS + "fileSize";

    /**
     * The hyperlink property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:hyperlink</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:Location</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:string</code></li>
     * </ul>
     */
    public static String HYPERLINK_PROPERTY = R.DATAMODEL_NS + "hyperlink";

    /**
     * The import date property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>axis:date</code></li>
     * <li><strong>DOMAIN</strong>: <code>axis:EventAnnotation</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:date</code></li>
     * </ul>
     */
    public static String IMPORT_DATE_PROPERTY = R.DATAMODEL_NS + "date";

    /**
     * The import date property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P75i_is_possessed_by</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E30_Right</code> (subclass of <code>axis:Register</code>)</li>
     * <li><strong>RANGE</strong>: <code>axis:Agent</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String RIGHTS_PROPERTY = R.CIDOC_NS + "P75i_is_possessed_by";
    // ---- End framework ontology property and value defintion

    /**
     * The type object value.
     */
    public static String TYPE_OBJECT = R.DATAMODEL_NS + "AudiovisualWork";

    /**
     * Fills the object with fake data: for test purpose, can be deleted.
     */
    private void fillObjectWithFakeData() {
        uri = R.POC_NS + "Selma";
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
    private void fillModelWithFakeData(String uri) {
        String fileName = "Selma.mp4";
        String fileSize = "700";
        String hyperlink = "http://www.imdb.com/title/tt1020072/";
        String rights = "Pathé";
        String duration = "128";
        String importDate = "2016-12-22";

        RegisterManager manager = new RegisterManager();
        HashMap<String, String> values = new HashMap<>();

        values.put(FILE_NAME_PROPERTY, fileName);
        values.put(FILE_SIZE_PROPERTY, fileSize);
        values.put(HYPERLINK_PROPERTY, hyperlink);
        values.put(RIGHTS_PROPERTY, rights);
        values.put(DURATION_PROPERTY, duration);
        values.put(IMPORT_DATE_PROPERTY, importDate);
        System.out.println("Adding values:");
        System.out.println(values);

        boolean result = manager.addRegisterInstance(FAKE_NAME, TYPE_OBJECT, values);
        if(!result)
            System.out.println("Imposible to add technical framework");
    }

    /**
     * This class holds all informations about the technical framework. Data are
     * automatically loaded from the TDB. If no data are not found, the
     * framework holds empty information.
     *
     * @param uri The entity uri of which the framework refers to. If null, the
     * data holds empty information.
     */
    public Technical(String uri) {
        if(uri == null||uri.isEmpty())
            return;
        fillModelWithFakeData(uri);
        retrieveData(uri);
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
     * @param uri The entity uri the framewok is associated with.
     */
    private void retrieveData(String uri)
    {
        RegisterManager manager = new RegisterManager();
        Map<String, String> values = manager.getPropertiesOfAnIndividual(uri);

        if(values == null) {
            System.out.println("No value found");
            return;
        }

        fileName = values.get(FILE_NAME_PROPERTY);
        fileSize = values.get(FILE_SIZE_PROPERTY);
        hyperlink = values.get(HYPERLINK_PROPERTY);
        rights = values.get(RIGHTS_PROPERTY);
        duration = values.get(DURATION_PROPERTY);
        importDate = values.get(IMPORT_DATE_PROPERTY);
        this.uri = uri;

        System.out.println(values);
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String toJSON() {
        // We could use Gson here - to discuss
        // Not using a JsonObjectBuilder because of build error problems
        // java.lang.ClassNotFoundException for javax.json.Json
        String json = "{\n";
        json += "\"uri\" : \"" + uri + "\",\n";
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
        String filmURI = R.DATAMODEL_NS + FAKE_NAME;
        Technical framework = new Technical(filmURI);
        System.out.println(framework.toJSON());
    }
}
