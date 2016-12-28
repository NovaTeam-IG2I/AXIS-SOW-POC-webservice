package rocks.novateam.axis.sow.poc.backend.ontology;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import rocks.novateam.axis.sow.poc.backend.Configuration;

/**
 * A <code>TDBManager</code> is a singleton object through which one can access
 * and construct the TDB store.
 *
 * It contains the following models:
 *
 * <ul>
 * <li>AXIS-CSRM data model, as the default model. Accessible using
 * {@link org.apache.jena.query.Dataset#getDefaultModel()}.</li>
 * <li>AXIS-CSRM interoperability model, as a named model. Accessible using
 * {@link org.apache.jena.query.Dataset#getNamedModel()}, with
 * {@link rocks.novateam.axis.sow.poc.backend.ontology.TDBManager.Models#INTEROPERABILITYMODEL_NAME}
 * as argument.</li>
 * <li>AXIS-CSRM functional model, as a named model. Accessible using
 * {@link org.apache.jena.query.Dataset#getNamedModel()}, with
 * {@link rocks.novateam.axis.sow.poc.backend.ontology.TDBManager.Models#FUNCTIONALMODEL_NAME}
 * as argument.</li>
 * </ul>
 *
 * @author richou
 */
public class TDBManager {


    /**
     * This nested class contains the TDB models' name as static Strings.
     */
    public class Models {

        /**
         * This is the name of the interoperability model in the TDB.
         */
        public static final String INTEROPERABILITYMODEL_NAME = "InteroperabilityModel";

        /**
         * This is the name of the functional model in the TDB.
         */
        public static final String FUNCTIONALMODEL_NAME = "FunctionalModel";
    }

    public static String DATAMODEL_NS = "http://titan.be/axis-csrm/datamodel/ontology/0.4#";

    private static TDBManager INSTANCE;

    /**
     * Once the {@link TDBManager} is connected to a database, interaction must
     * be done through this {@link org.apache.jena.query.Dataset}.
     * 
     * @see TDBManager#getDataset()
     * @see org.apache.jena.query.Dataset
     */
    private Dataset dataset;

    /**
     * Constructs a new {@link TDBManager} by connecting to the given TDB
     * folder.
     *
     * This constructor is private. Use {@link TDBManager#getInstance()}
     * instead.
     *
     * @see TDBManager#getInstance()
     */
    private TDBManager() {
        dataset = TDBFactory.createDataset(Configuration.getInstance().getTdbFolder());
    }

    /**
     * Gets the {@link TDBManager} instance. If it does not exist, constructs a
     * new one.
     *
     * Since {@link TDBManager} is a singleton, use this method instead of the
     * constructor {@link TDBManager#TDBManager()}.
     *
     * @return The instance of {@link TDBManager}.
     *
     */
    public static TDBManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TDBManager();
        }
        return INSTANCE;
    }

    /**
     * This method wipes all the TDB and sets a new one up using the given
     * files.
     *
     * @see DATAMODEL_FILE
     * @see INTEROPERABILITY_FILE
     * @see FUNCTIONALMADEL_FILE
     */
    public void setUp() {
        dataset.begin(ReadWrite.WRITE);
        Model dataModel = dataset.getDefaultModel();
        Model interoperabilityModel = dataset.getNamedModel(Models.INTEROPERABILITYMODEL_NAME);
        Model functionalModel = dataset.getNamedModel(Models.FUNCTIONALMODEL_NAME);
        Configuration config = Configuration.getInstance();

        try {
            dataModel.read(new FileInputStream(config.getDatamodelFile()), null, null);
            interoperabilityModel.read(new FileInputStream(config.getInteroperabilityModelFile()), null, null);
            functionalModel.read(new FileInputStream(config.getFunctionalModelFile()), null, null);
            dataset.commit();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TDBManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            dataset.end();
        }
    }

    /**
     * Fetches the {@link org.apache.jena.query.Dataset} associated with the
     * {@link TDBManager}.
     *
     * @return The {@link org.apache.jena.query.Dataset} the {@link TDBManager}
     * is connected to.
     */
    public Dataset getDataset() {
        return dataset;
    }

    public static void main(String[] args) {
        TDBManager tdbm = TDBManager.getInstance();

        // Uncomment the following line to set up a new TDB, comment it to work with an existing one.
        tdbm.setUp();
        
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.READ);
        Model model = ds.getDefaultModel();
        ds.end();
        
        // Prints out every statement the TDB contains.
        for(StmtIterator i = model.listStatements() ; i.hasNext() ;) {
            System.out.println(i.nextStatement().toString());
        }
    }
}