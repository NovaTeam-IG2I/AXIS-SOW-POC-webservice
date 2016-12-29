package rocks.novateam.axis.sow.poc.frameworks;

import java.io.IOException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import rocks.novateam.axis.sow.poc.backend.Configuration;

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
     * The entity id of which the framework refers to.
     */
    private String id = "";

    /**
     * The work director's name.
     */
    private String director = "";

    /**
     * The work duration in minutes.
     */
    private String duration = "";

    /**
     * The work productor's name, can be a society.
     */
    private String productor = "";

    /**
     * The year of the work release.
     */
    private String release = "";

    /**
     * The work theme.
     */
    private String theme = "";

    /**
     * The work original title.
     */
    private String title = "";

    // private String nationality = "";  // No reference in the ontology

    // ---- End framework field definition

    // ---- Begin framework ontology property and value defintion
    /**
     * The type object value.
     */
    public static String TYPE_OBJECT = Reg.DATAMODEL_URI + "AudiovisualWork";

    /**
     * The type property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>rdf:type</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E71_Man-Made_Thing</code> (subclass of <code>axis:Register</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E35_Title</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String TYPE_PROPERTY = Reg.RDF_URI + "type";

    /**
     * The director property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P94_has_created</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E65_Creation</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E28_Conceptual_Object</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String DIRECTOR_PROPERTY = Reg.CIDOC_URI + "E65_Creation";

    /**
     * The duration property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>ma:duration</code></li>
     * <li><strong>DOMAIN</strong>: <code>ma:MediaResource and (not(ma:Image)</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>xsd:Decimal</code></li>
     * </ul>
     */
    public static String DURATION_PROPERTY = Reg.MA_URI + "duration";

    /**
     * The productor property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P108_has_produced</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E12_Production</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E24_Physical_Man-Made_Thing</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String PRODUCTOR_PROPERTY = Reg.CIDOC_URI + "P108_has_produced";

    /**
     * The release date property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>ma:releaseDate</code> (subproperty of <code>ma:date</code>)</li>
     * <li><strong>DOMAIN</strong>: <code>ma:MediaResource</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: None specified</li>
     * </ul>
     */
    public static String RELEASE_PROPERTY = Reg.MA_URI + "releaseDate";

    /**
     * The theme property URI. Note we consider a genre as a thema.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>ma:hasGenre</code></li>
     * <li><strong>DOMAIN</strong>: <code>ma:MediaResource</code> (subclass of <code>axis:Register</code> and <code>axis:Document</code>)</li>
     * <li><strong>RANGE</strong>: None specified</li>
     * </ul>
     */
    public static String THEME_PROPERTY = Reg.MA_URI + "hasGenre";

    /**
     * The title property URI.
     *
     * <ul>
     * <li><strong>ABOUT</strong>: <code>cidoc:P102_has_title</code></li>
     * <li><strong>DOMAIN</strong>: <code>cidoc:E71_Man-Made_Thing</code> (subclass of <code>axis:Register</code>)</li>
     * <li><strong>RANGE</strong>: <code>cidoc:E35_Title</code> (subclass of <code>axis:Register</code>)</li>
     * </ul>
     */
    public static String TITLE_PROPERTY = Reg.CIDOC_URI + "P102_has_title";

    // No reference to the nationality was found in the ontology
    // public static String NATIONALITY_PROPERTY = "";

    // ---- End framework ontology property and value defintion

    /**
     * Fills the object with fake data: for test purpose, can be deleted.
     */
    private void fillObjectWithFakeData() {
        id = Reg.POC_URI + "Selma";
        title = "Selma";
        theme = "Historical drama";
        release = "2015-01-09";
        duration = "128";
        // nationality = "United States";
        director = "Ava DuVernay";
        productor = " Cloud Eight Films";
    }

    /**
     * Fills the model with fake data: for test purpose, can be deleted.
     */
    private void fillModelWithFakeData(String id) {
        String director = "Ava DuVernay";
        String duration = "128";
        String productor = " Cloud Eight Films";
        String release = "2015-01-09";
        String theme = "Historical drama";
        String title = "Selma";

        Model model = getModel();
        Resource resource = model.createResource(id);
        resource.addProperty(model.createProperty(TYPE_PROPERTY), TYPE_OBJECT);
        resource.addProperty(model.createProperty(DIRECTOR_PROPERTY), director);
        resource.addProperty(model.createProperty(DURATION_PROPERTY), duration);
        resource.addProperty(model.createProperty(PRODUCTOR_PROPERTY), productor);
        resource.addProperty(model.createProperty(RELEASE_PROPERTY), release);
        resource.addProperty(model.createProperty(THEME_PROPERTY), theme);
        resource.addProperty(model.createProperty(TITLE_PROPERTY), title);
    }

    /**
     * This class holds all informations about the production framework. Data
     * are automatically loaded from the TDB. If no data are not found, the
     * framework holds empty information.
     *
     * @param id The entity id of which the framework refers to. If null, the
     * data holds empty information.
     */
    public Production(String id) {
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
            model = FileManager.get().loadModel(
                Configuration.getInstance().getDatamodelFile(), null, "TURTLE"
            );
        }
        return model;
    }
    /**
     * Retrieves all the production framework data.
     *
     * @param id The entity id the production is associated with.
     */
    private void retrieveData(String id)
    {
        String DIRECTOR_SELECT = "director";
        String DURATION_SELECT = "duration";
        String PRODUCTOR_SELECT = "productor";
        String RELEASE_SELECT = "release";
        String THEME_SELECT = "theme";
        String TITLE_SELECT = "title";

        String queryString = Reg.PREFIX + "SELECT " +
                "?" + DIRECTOR_SELECT + " ?" + DURATION_SELECT + " " +
                "?" + PRODUCTOR_SELECT + " ?" + RELEASE_SELECT + " " +
                "?" + THEME_SELECT + " ?" + TITLE_SELECT + " " +
                "WHERE \n{\n" +
                "<" + id + "> <" + DIRECTOR_PROPERTY + "> ?" + DIRECTOR_SELECT + ".\n" +
                "<" + id + "> <" + DURATION_PROPERTY + "> ?" + DURATION_SELECT + ".\n" +
                "<" + id + "> <" + PRODUCTOR_PROPERTY + "> ?" + PRODUCTOR_SELECT + ".\n" +
                "<" + id + "> <" + RELEASE_PROPERTY + "> ?" + RELEASE_SELECT + ".\n" +
                "<" + id + "> <" + THEME_PROPERTY + "> ?" + THEME_SELECT + ".\n" +
                "<" + id + "> <" + TITLE_PROPERTY + "> ?" + TITLE_SELECT + ".\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, getModel())) {
            ResultSet results = qexec.execSelect();
            while ( results.hasNext() ) {
                QuerySolution solution = results.nextSolution();

                // Should maybe use Literal
                director = solution.getLiteral(DIRECTOR_SELECT).toString();
                duration = solution.getLiteral(DURATION_SELECT).toString();
                productor = solution.getLiteral(PRODUCTOR_SELECT).toString();
                release = solution.getLiteral(RELEASE_SELECT).toString();
                theme = solution.getLiteral(THEME_SELECT).toString();
                title = solution.getLiteral(TITLE_SELECT).toString();
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
        json += "\"director\" : \"" + director + "\",\n";
        json += "\"duration\" : \"" + duration + "\",\n";
        json += "\"productor\" : \"" + productor + "\"\n";
        json += "\"release\" : \"" + release + "\",\n";
        json += "\"theme\" : \"" + theme + "\",\n";
        json += "\"title\" : \"" + title + "\",\n";
        // json += "\"nationality\" : \"" + nationality + "\",\n";
        json += "}";
        return json;
    }

    public static void main(String[] args) throws IOException {
        String filmIdURI = Reg.POC_URI + "Selma";
        Production production = new Production(filmIdURI);
        System.out.println(production.exportJSONFormat());
    }
}
