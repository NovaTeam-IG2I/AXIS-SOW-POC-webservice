package rocks.novateam.axis.sow.poc.frameworks;

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
    public static String TYPE_PROPERTY = Reg.RDF_URI + "type";
    public static String TITLE_PROPERTY = Reg.CIDOC_URI + "P102_has_title";  // TODO: find better
    public static String THEME_PROPERTY = "";
    public static String RELEASE_PROPERTY = "";
    public static String DURATION_PROPERTY = "";
    public static String NATIONALITY_PROPERTY = "";
    public static String DIRECTOR_PROPERTY = "";
    public static String PRODUCTOR_PROPERTY = "";
}
