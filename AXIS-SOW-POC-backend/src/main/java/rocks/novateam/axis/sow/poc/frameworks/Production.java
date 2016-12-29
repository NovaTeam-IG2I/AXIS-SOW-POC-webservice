package rocks.novateam.axis.sow.poc.frameworks;

import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

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
    private String nationality = "";

    /**
     * The work director's name.
     */
    private String director = "";

    /**
     * The work productor's name, can be a society.
     */
    private String productor = "";

    // All property names relative to the framework
    // TODO: define the properties
    public static String TYPE_PROPERTY = Reg.RDF_URI + "type";
    public static String TITLE_PROPERTY = "";
    public static String THEME_PROPERTY = "";
    public static String RELEASE_PROPERTY = "";
    public static String DURATION_PROPERTY = "";
    public static String NATIONALITY_PROPERTY = "";
    public static String DIRECTOR_PROPERTY = "";
    public static String PRODUCTOR_PROPERTY = "";

    /**
     * Fills the object with fake data: for test purpose, can be deleted.
     */
    private void fillObjectWithFakeData() {
        id = Reg.POC_URI + "Selma";
        title = "Selma";
        theme = "Historical drama";
        release = "2015-01-09";
        duration = "128";
        nationality = "United States";
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
        String json = "{\n";
        json += "\"id\" : \"" + id + "\",\n";
        json += "\"title\" : \"" + title + "\",\n";
        json += "\"theme\" : \"" + theme + "\",\n";
        json += "\"release\" : \"" + release + "\",\n";
        json += "\"duration\" : \"" + duration + "\",\n";
        json += "\"nationality\" : \"" + nationality + "\",\n";
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
