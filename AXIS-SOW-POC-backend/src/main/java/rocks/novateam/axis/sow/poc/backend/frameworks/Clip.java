package rocks.novateam.axis.sow.poc.backend.frameworks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rocks.novateam.axis.sow.poc.backend.Configuration;
import rocks.novateam.axis.sow.poc.backend.R;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileManager;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Production.DIRECTOR_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Production.DURATION_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Production.PRODUCTOR_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Production.RELEASE_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Production.THEME_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Production.TITLE_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Technical.FILE_NAME_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Technical.FILE_SIZE_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Technical.HYPERLINK_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Technical.IMPORT_DATE_PROPERTY;
import static rocks.novateam.axis.sow.poc.backend.frameworks.Technical.RIGHTS_PROPERTY;

import rocks.novateam.axis.sow.poc.backend.helpers.TechnicalData;
import rocks.novateam.axis.sow.poc.backend.ontology.RegisterManager;

/**
 * This class holds all informations about the technical framework.
 *
 * A clip framework describes all clip information
 *
 * @author Olivier Sailly
 */
public class Clip {

    /**
     * Holds the model, should be deleted when it will be possible to get the
     * model correctly. Do not use this field directly, use getModel function.
     */
    private Model model = null;

    /**
     * Data contained in the Clip Framework.
     */
    private Map<String,String> data;

    /**
     * Gson :JSON generator and serializer.
     */
    private final Gson jsonBuilder;

    /**
     * The typeToken to give to Gson.
     */
    private final TypeToken<Map<String,String>> clipDataType;

    /**
     * This class holds all informations about the clip framework. Data are
     * automatically loaded from the TDB. If no data are not found, the
     * framework holds empty information.
     *
     * @param uri The entity uri of which the framework refers to. If null, the
     * data holds empty information.
     */
    public Clip(String uri) {
        jsonBuilder = new Gson();
        clipDataType = new TypeToken<Map<String,String>>(){};
        data = new HashMap<>();
        if(uri == null||uri.isEmpty())
            return;
        retrieveAllDataButProdTech(uri);
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
     * Retrieves all the clip framework data.
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
        data.put("uri", uri);
        data.putAll(values);
    }

    /**
     * Retrieves all the clip framework data.
     *
     * @param uri The entity uri the framewok is associated with.
     */
    private void retrieveAllDataButProdTech(String uri)
    {
        RegisterManager manager = new RegisterManager();
        Map<String, String> values = manager.getPropertiesOfAnIndividual(uri);
        if(values == null) {
            System.out.println("No value found");
            return;
        }
        values.remove(FILE_NAME_PROPERTY);
        values.remove(FILE_SIZE_PROPERTY);
        values.remove(HYPERLINK_PROPERTY);
        values.remove(RIGHTS_PROPERTY);
        values.remove(IMPORT_DATE_PROPERTY);
        values.remove(DIRECTOR_PROPERTY);
        values.remove(PRODUCTOR_PROPERTY);
        values.remove(TITLE_PROPERTY);
        values.remove(THEME_PROPERTY);
        values.remove(RELEASE_PROPERTY);
        values.remove(DURATION_PROPERTY);
        data.put("uri", uri);
        data.putAll(values);
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String toJSON() {
        return jsonBuilder.toJson(data, clipDataType.getType());
    }

    public static void main(String[] args) throws IOException {
        String filmID = R.DATAMODEL_NS + "selma";
        Clip technical = new Clip(filmID);
        System.out.println(technical.toJSON());
    }
}
