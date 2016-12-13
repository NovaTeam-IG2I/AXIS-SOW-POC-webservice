package rocks.novateam.axis.sow.poc.backend;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 * This class contains configuration elements, such as pathnames.
 * 
 * It uses the <code>configuration.yml</code> YAML file to store the following configuration values:
 * 
 * <ul>
 * <li><code>tdbFolder</code>: This is the folder where the TDB triple store is located.</li>
 * <li><code>datamodelFile</code>: This is the path to the Data Model ontology.</li>
 * <li><code>interoperabilitymodelFile</code>: This is the path to the Interoperability Model ontology.</li>
 * <li><code>functionalmodelFile</code>: This is the path to the Functional Model ontology.</li>
 * </ul>
 * 
 * Note that relative filenames can be used, with the folder `configuration.yml` is in as reference.
 * 
 * @author richou
 */
public final class Configuration {

    private static Configuration INSTANCE;
    
    private static final String YAML_FILE = "configuration.yml";

    private String tdbFolder;
    private String datamodelFile;
    private String interoperabilitymodelFile;
    private String functionalmodelFile;


    /**
     * Constructs a new {@link Configuration} by storing the given values.
     *
     * This constructor is private. Use {@link Configuration#getInstance()}
     * instead.
     *
     * @see Configuration#getInstance()
     */
    private Configuration(String tdbFolder, String datamodelFile, String interoperabilityModelFile, String functionalModelFile) {
        this.tdbFolder = tdbFolder;
        this.datamodelFile = datamodelFile;
        this.interoperabilitymodelFile = interoperabilityModelFile;
        this.functionalmodelFile = functionalModelFile;
    }

    /**
     * Gets the {@link Configuration} instance. If it does not exist, parses the `configuration.yml` file and constructs a
     * new {@link Configuration} instance.
     *
     * Since {@link Configuration} is a singleton, use this method instead of the
     * constructor {@link Configuration#Configuration()}.
     *
     * @return The instance of {@link Configuration}.
     *
     */
    public static Configuration getInstance() {
        if (INSTANCE == null) {
            Yaml yaml = new Yaml();

            Map<String, String> map = null;
            try {
                map = (Map) yaml.load(new FileInputStream(YAML_FILE));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (map != null
                    && map.containsKey("tdbFolder")
                    && map.containsKey("datamodelFile")
                    && map.containsKey("interoperabilitymodelFile")
                    && map.containsKey("functionalmodelFile")) {
                INSTANCE = new Configuration(map.get("tdbFolder"),
                        map.get("datamodelFile"),
                        map.get("interoperabilitymodelFile"),
                        map.get("functionalmodelFile"));
            }
        }
        return INSTANCE;
    }
    
    /**
     * 
     * @return The pathname of the folder where the TDB triple store is located.
     */
    public String getTdbFolder() {
        return tdbFolder;
    }
    
    /**
     * 
     * @return The pathname of the Data Model file.
     */
    public String getDatamodelFile() {
        return datamodelFile;
    }
    
    /**
     * 
     * @return The pathname of the Interoperability Model file.
     */
    public String getInteroperabilityModelFile() {
        return interoperabilitymodelFile;
    }
    
    /**
     * 
     * @return The pathname of the Functional Model file.
     */
    public String getFunctionalModelFile() {
        return functionalmodelFile;
    }

    public static void main(String[] args) throws IOException {
        // Get a Confuration instance.
        Configuration c = Configuration.getInstance();

        // Print out the values.
        System.out.println("TDB Folder:\t" + c.getTdbFolder());
        System.out.println("Data Model:\t" + c.getDatamodelFile());
        System.out.println("Inter Model:\t" + c.getInteroperabilityModelFile());
        System.out.println("Func Model:\t" + c.getFunctionalModelFile());
    }
}
