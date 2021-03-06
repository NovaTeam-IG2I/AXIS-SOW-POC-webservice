package rocks.novateam.axis.sow.poc.backend.frameworks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Map;

import rocks.novateam.axis.sow.poc.backend.Configuration;
import rocks.novateam.axis.sow.poc.backend.R;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import rocks.novateam.axis.sow.poc.backend.helpers.TechnicalData;
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
 * @author Olivier Sailly
 */
public class Technical {

    /**
     * Holds the model, should be deleted when it will be possible to get the
     * model correctly. Do not use this field directly, use getModel function.
     */
    private Model model = null;

    /**
     * Data contained in the Technical Framework.
     */
    private TechnicalData data;

    /**
     * Gson :JSON generator and serializer.
     */
    private final Gson jsonBuilder;

    /**
     * The typeToken to give to Gson.
     */
    private final TypeToken<TechnicalData> technicalDataType;

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
     * This class holds all informations about the technical framework. Data are
     * automatically loaded from the TDB. If no data are not found, the
     * framework holds empty information.
     *
     * @param uri The entity uri of which the framework refers to. If null, the
     * data holds empty information.
     */
    public Technical(String uri) {
        jsonBuilder = new Gson();
        technicalDataType = new TypeToken<TechnicalData>(){};
        data = new TechnicalData();
        if(uri == null||uri.isEmpty())
            return;
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
        data.fileName = values.get(FILE_NAME_PROPERTY);
        data.fileSize = values.get(FILE_SIZE_PROPERTY);
        data.hyperlink = values.get(HYPERLINK_PROPERTY);
        data.rights = values.get(RIGHTS_PROPERTY);
        data.duration = values.get(DURATION_PROPERTY);
        data.importDate = values.get(IMPORT_DATE_PROPERTY);
        data.uri = uri;
        System.out.println(values);
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String toJSON() {
        return jsonBuilder.toJson(data, technicalDataType.getType());
    }

    public static void main(String[] args) throws IOException {
        String filmID = R.DATAMODEL_NS + "selma";
        Technical technical = new Technical(filmID);
        System.out.println(technical.toJSON());
    }
}
