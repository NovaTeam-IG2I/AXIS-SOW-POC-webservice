package rocks.novateam.axis.sow.poc.frameworks;

import java.io.IOException;

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

    // ---- Begin framework field definition
    /**
     * The entity id of which the framework refers to.
     */
    private String id = "";

    /**
     * The work original title.
     */
    private String title = "";

    /**
     * The work theme.
     */
    private String theme = "";

    /**
     * The year of the work release.
     */
    private String release = "";

    /**
     * The work duration in minutes.
     */
    private String duration = "";

    /**
     * The work nationality.
     */
    // private String nationality = "";  // No reference in the ontology

    /**
     * The work director's name.
     */
    private String director = "";

    /**
     * The work productor's name, can be a society.
     */
    private String productor = "";
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
        fillObjectWithFakeData();
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
        json += "\"title\" : \"" + title + "\",\n";
        json += "\"theme\" : \"" + theme + "\",\n";
        json += "\"release\" : \"" + release + "\",\n";
        json += "\"duration\" : \"" + duration + "\",\n";
        // json += "\"nationality\" : \"" + nationality + "\",\n";
        json += "\"director\" : \"" + director + "\",\n";
        json += "\"productor\" : \"" + productor + "\"\n";
        json += "}";
        return json;
    }

    public static void main(String[] args) throws IOException {
        String filmIdURI = Reg.POC_URI + "Selma";
        Production production = new Production(filmIdURI);
        System.out.println(production.exportJSONFormat());
    }
}
