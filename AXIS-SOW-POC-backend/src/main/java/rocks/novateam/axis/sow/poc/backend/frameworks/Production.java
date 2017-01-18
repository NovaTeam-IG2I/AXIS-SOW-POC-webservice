package rocks.novateam.axis.sow.poc.backend.frameworks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;

import rocks.novateam.axis.sow.poc.backend.Configuration;
import rocks.novateam.axis.sow.poc.backend.R;
import rocks.novateam.axis.sow.poc.backend.helpers.ProductionData;
import rocks.novateam.axis.sow.poc.backend.ontology.RegisterManager;

/**
 * This class holds all informations about the production framework.
 *
 * A production framework describes all production information of a filming work
 * (director, theme, title...).
 *
 * So far, it can only have one data by field (for example, only one theme
 * owner).
 *
 * @author Alex Canales
 */
public class Production {

    /**
     * Holds the model, should be deleted when it will be possible to get the
     * model correctly. Do not use this field directly, use getModel function.
     */
    private Model model = null;

    // ---- Begin framework field definition

    /**
     * Data contained in the Technical Framework.
     */
    private ProductionData data;

    /**
     * Gson :JSON generator and serializer.
     */
    private final Gson jsonBuilder;

    /**
     * The typeToken to give to Gson.
     */
    private final TypeToken<ProductionData> productionDataType;

    // ---- End framework field definition

    // ---- Begin framework ontology property and value defintion
    /**
     * The type object value.
     */
    public static String TYPE_OBJECT = R.DATAMODEL_NS + "AudiovisualWork";

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
     * The director property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P94_has_created</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E65_Creation</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E28_Conceptual_Object</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String DIRECTOR_PROPERTY = R.CIDOC_NS + "E65_Creation";

    /**
     * The duration property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>ma:duration</code></li>
     * <li><strong>DOMAIN</strong>: <code>ma:MediaResource and (not(ma:Image)</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:Decimal</code></li>
     * </ul>
     */
    public static String DURATION_PROPERTY = R.MA_NS + "duration";

    /**
     * The productor property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P108_has_produced</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E12_Production</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E24_Physical_Man-Made_Thing</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String PRODUCTOR_PROPERTY = R.CIDOC_NS + "P108_has_produced";

    /**
     * The release date property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>ma:releaseDate</code> (subproperty of <code>ma:date</code>)</li>
     * <li><strong>DOMAIN</strong>: <code>ma:MediaResource</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: None specified</li>
     * </ul>
     */
    public static String RELEASE_PROPERTY = R.MA_NS + "releaseDate";

    /**
     * The theme property namespace. Note we consider a genre as a thema.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>ma:hasGenre</code></li>
     * <li><strong>DOMAIN</strong>: <code>ma:MediaResource</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: None specified</li>
     * </ul>
     */
    public static String THEME_PROPERTY = R.MA_NS + "hasGenre";

    /**
     * The title property namespace.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P102_has_title</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E71_Man-Made_Thing</code> (subclass of <code>axis:Register</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E35_Title</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String TITLE_PROPERTY = R.CIDOC_NS + "P102_has_title";

    // No reference to the nationality was found in the ontology
    // public static String NATIONALITY_PROPERTY = "";

    // ---- End framework ontology property and value defintion

    /**
     * This class holds all informations about the technical framework. Data are
     * automatically loaded from the TDB. If no data are not found, the
     * framework holds empty information.
     *
     * @param uri The entity uri of which the framework refers to. If null, the
     * data holds empty information.
     */
    public Production(String uri) {
        jsonBuilder = new Gson();
        productionDataType = new TypeToken<ProductionData>(){};
        data = new ProductionData();
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
        data.director = values.get(DIRECTOR_PROPERTY);
        data.productor = values.get(PRODUCTOR_PROPERTY);
        data.title = values.get(TITLE_PROPERTY);
        data.theme = values.get(THEME_PROPERTY);
        data.release = values.get(RELEASE_PROPERTY);
        data.duration = values.get(DURATION_PROPERTY);
        data.uri = uri;
        System.out.println(values);
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String exportJSONFormat() {
        return jsonBuilder.toJson(data, productionDataType.getType());
    }

    /**
     * Fills the model with fake data: for test purpose, can be deleted.
     */
    public static void fillModelWithFakeData(String uri) {
        RegisterManager manager = new RegisterManager();
        Map<String,String> map = new HashMap<>();
        map.put(DIRECTOR_PROPERTY, "Ava DuVernay");
        map.put(DURATION_PROPERTY, "128");
        map.put(PRODUCTOR_PROPERTY, "Cloud Eight Films");
        map.put(RELEASE_PROPERTY, "2015-01-09");
        map.put(THEME_PROPERTY, "Historical drama");
        map.put(TITLE_PROPERTY, "Selma");

        boolean ari = manager.addRegisterInstance("selma", TYPE_OBJECT, map);

        System.out.println(ari);
    }

    public static void main(String[] args) throws IOException {
        String filmID = R.DATAMODEL_NS + "selma";
        Production.fillModelWithFakeData(filmID);
        Production production = new Production(filmID);
        System.out.println(production.exportJSONFormat());
    }
}
