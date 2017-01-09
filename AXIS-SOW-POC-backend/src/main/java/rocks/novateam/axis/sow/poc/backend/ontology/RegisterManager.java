/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.ontology;

import rocks.novateam.axis.sow.poc.backend.helpers.Category;
import java.util.ArrayList;
import java.util.Map;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import org.apache.jena.util.iterator.ExtendedIterator;

import rocks.novateam.axis.sow.poc.backend.helpers.CamelCaseConverter;
import rocks.novateam.axis.sow.poc.backend.helpers.InstanceExistenceState;

/**
 *
 * @author Mélody
 * @author Olivier Sailly
 */
public class RegisterManager {
    private final class NeededEnvironment {
        private Dataset mDataset;
        private Model mModel;
        private OntModel mOntModel;
        private ReadWrite mReadWrite;

        /**
         * Constructor of the class neededEnvironnement.
         * This public method sets the dataset, model, and ontmodel by default.
         * It uses the ReadWrite entry to know what to do.
         *
         * @param rw Tells whether to begin writing or reading
         */
        public NeededEnvironment(ReadWrite rw) {
            this.mReadWrite = rw;
            this.mDataset = tdbm.getDataset();
            this.mDataset.begin(this.mReadWrite);
            this.mModel = mDataset.getDefaultModel();
            this.mOntModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, mModel);
        }

        /**
         * Constructor of the class neededEnvironnement.
         * This public method calls the default constructor.
         * It is able to set the OntModel's StrictMode.
         *
         * Note :
         *
         *        setStrictMode() is set to false to solve the problem with the ConversionException with classes as
         *        ~/interoperabilitymodel/ontology/0.4#AXE
         *
         *        As stated in http://stackoverflow.com/a/17447438/7358724 , we have to call setStrictMode(false) on our OntModel,
         *        in order to be able to view every resource as a class, by switching off strict checking.
         *
         * @param rw Tells whether to begin writing or reading
         * @param ontModelStrictMode Tells whether to set StrictMode to true or false
         */
        public NeededEnvironment(ReadWrite rw, boolean ontModelStrictMode) {
            this(rw);
            this.mOntModel.setStrictMode(ontModelStrictMode);
        }

        /**
         * This public method should be used to finish any process.
         * The method knows whether to finish a READ or a WRITE, otherwise, it aborts everything.
         */
        public void finish(){
            switch(this.mReadWrite){
                case READ:
                    this.mDataset.end();
                break;
                case WRITE:
                    this.mDataset.commit();
                break;
                default:
                    this.mDataset.abort();
                break;
            }
        }

        public Dataset getDataset() {
            return mDataset;
        }

        public void setDataset(Dataset ds) {
            this.mDataset = ds;
        }

        public Model getModel() {
            return mModel;
        }

        public void setModel(Model mModel) {
            this.mModel = mModel;
        }

        public OntModel getOntModel() {
            return mOntModel;
        }

        public void setOntModel(OntModel mOntModel) {
            this.mOntModel = mOntModel;
        }
    }

    private static final String NS = TDBManager.DATAMODEL_URL+"#";


    private TDBManager tdbm;
    
    public RegisterManager() {
        tdbm = TDBManager.getInstance();
    }

    /**
     * TODO: AFP + search existence + put name given in label and camelCase in ont
     * @param name
     * @param className
     * @param properties 
     * @return
     */
    public boolean addRegisterInstance(String name, String className, Map<String,String> properties){
        int cpt = 0;
        String label=name;
        name = CamelCaseConverter.convertToCamelCase(name);
        //if this instance already exist
        if(instanceExists(name)==InstanceExistenceState.EXISTS) return true; // This ind already exists
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.WRITE);
        Individual thisInd = nEnv.getOntModel().getIndividual(NS+name);
        if(thisInd != null){//if it is impossible to get the individual
            nEnv.finish();
            return false;
        }
        Individual afp = nEnv.getOntModel().getOntClass(NS + "AFP").createIndividual(NS + name + "_AFP");
        OntClass class_ = nEnv.getOntModel().getOntClass(NS+className);
        Individual ind = class_.createIndividual(NS+name);
        ind.addLabel(label,"EN");
        for (Map.Entry<String,String> property : properties.entrySet()) {
            OntProperty prprt = nEnv.getOntModel().createOntProperty(NS+property.getKey()); // create an ontProperty
            ind.addProperty(prprt, property.getValue());
        }
        ind.addProperty(nEnv.getOntModel().getProperty(NS + "isDeclaredBy"), afp);
        nEnv.finish();
        return (instanceExists(name)==InstanceExistenceState.EXISTS); //test if it has been created
    }
    
    /**
     * Add a predicate to the ontology. Put its name in camelCase 
     * before the insertion.
     * @param name 
     * @return
     */
    public boolean addPredicate(String name){
        //put the string in camelCase
        name = CamelCaseConverter.convertToCamelCase(name); // System.out.println(name);
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.WRITE);
        nEnv.getOntModel().createOntProperty(NS+name);
        nEnv.finish();
        return this.predicateExists(name);
    }
    
    
    /**
     * Get all the datatypeProperties from the ontModel
     * Add their name to an array called properties and return it
     * @param className
     * @return properties
     */
    public ArrayList getProperties(String className){
        ArrayList properties = new ArrayList();

        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.READ);
        nEnv.finish();
        OntClass class_ = nEnv.getOntModel().getOntClass(NS+className);
        ExtendedIterator<OntProperty> exItr;        
        exItr = class_.listDeclaredProperties();      
        while (exItr.hasNext()) {
          OntProperty prop = exItr.next();
          if(prop.isDatatypeProperty()){ // System.out.println("Datatype prop: "+ prop.getLocalName());
            properties.add(prop.getLocalName());
          }
        }
        return properties;
    }

    /**
     * Get recursively all categories from the Register class.
     * This public method calls getCategoriesRecusively().
     *
     * @return A list of categories
     */
    public ArrayList<Category> getRegisterCategories() { return getCategoriesRecusively("Register"); }

    /**
     * Get recursively all categories from the given class.
     * This public method is used to instantiate variables, before looping, and to return the result.
     *
     * @param className Name of the class you want to retrieve categories from
     * 
     * @return A list of categories
     */
    public ArrayList<Category> getCategoriesRecusively(String className) throws NullPointerException { // before the first loop, we have to create our variables
        ArrayList<Category> categories = new ArrayList<>();
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.READ, false);
        nEnv.finish();
        OntClass mOntClass = nEnv.getOntModel().getOntClass(NS+className);
        if(mOntClass == null) throw new NullPointerException("\nError on getting \""+NS+className+"\" OntClass.");
        return getCategoriesRecusively(categories, mOntClass);
    }

    /**
     * Get recursively all categories from the given OntClass.
     * This private method is used to loop, and finally to return the result.
     *
     * @param categories This is the list we want to put register's categories in
     * @param currentOntClass This is the current OntClass from which we want to retrieve categories
     *
     * @return A list of categories
     */
    private ArrayList<Category> getCategoriesRecusively(ArrayList<Category> categories, OntClass currentOntClass){
        Category c = new Category(currentOntClass.getLocalName());
        if(currentOntClass.hasSubClass()){
            ExtendedIterator<OntClass> iter = currentOntClass.listSubClasses();
            while(iter.hasNext())
            {
                OntClass currentLoopOntClass = iter.next();
                c.addSubCategories(getCategoriesRecusively(new ArrayList<Category>(), currentLoopOntClass));
            }
        }
        categories.add(c);
        return categories;
    }

    /**
     * Checks the existence state of a given instance.
     * This public method can be use to check whether an instance exists or not, or if there is an instance without AFP or an AFP withou instance
     *
     * @param name The instance name to look up
     *
     * @return An InstanceExistenceState enum
     */
    public InstanceExistenceState instanceExists(String name) {
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.READ);
        nEnv.finish();
        OntResource resource = nEnv.getOntModel().getOntResource(NS+name);
        OntResource resourceAFP = nEnv.getOntModel().getOntResource(NS+name+"_AFP");
        if(resource != null && resourceAFP != null){
            return InstanceExistenceState.EXISTS;
        } else {
            if(resource == null && resourceAFP == null) return InstanceExistenceState.DOES_NOT_EXIST;
            if(resource != null) return InstanceExistenceState.NO_INSTANCE_FOUND; //ressource exists but has no AFP
            return InstanceExistenceState.NO_AFP_FOUND; //ressource does not exists, but its AFP still exists
        }
    }

    /**
     * Checks if a given predicate exists.
     * This public method can be use to check whether an predicate exists or not.
     *
     * @param ccName The predicate name to look up
     *
     * @return true if it exists, false otherwise
     */
    public boolean predicateExists(String ccName){
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.READ);
        nEnv.finish();
        return (nEnv.getOntModel().getOntProperty(NS+ccName)!=null);
    }
    
    /**
     * Delete an instance by name
     * @param name 
     * @return
     */
    public boolean deleteInstance(String name){
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.WRITE);
        try {
            OntResource resource = nEnv.getOntModel().getOntResource(NS+name);
            OntResource resourceAFP = nEnv.getOntModel().getOntResource(NS+name+"_AFP");
            resource.remove();
            resourceAFP.remove();
        } catch (Exception e){
            System.out.println(e.fillInStackTrace());
        } finally {
            nEnv.finish();
        }
        return (this.instanceExists(name) == InstanceExistenceState.DOES_NOT_EXIST);
    }

    /**
     * Create a statement with given registers
     * @param subjectName
     * @param objectName
     * @param predicateName
     * @return boolean
     */
    public boolean addPredicateToRegisters(String subjectName, String objectName, String predicateName){
        if(predicateExists(predicateName)){
            NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.WRITE);
            Resource subject = nEnv.getOntModel().getIndividual(NS+subjectName);
            Resource object = nEnv.getOntModel().getIndividual(NS+objectName);
            OntProperty predicate = nEnv.getOntModel().getOntProperty(NS+predicateName);
            subject.addProperty(predicate, object);
            nEnv.finish();
        } else { //predicate doesn't exist
            return false;
        }
        return (statementExists(subjectName, predicateName, objectName));
    }
    
    public boolean statementExists(String subjectName, String predicateName, String objectName){
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.READ);
        nEnv.finish();
        //if subject has predicate in his properties and is linked to the object, return true
        return nEnv.getOntModel().getOntResource(NS+subjectName).hasProperty(nEnv.getOntModel().getOntProperty(NS+predicateName),nEnv.getOntModel().getOntResource(NS+objectName));
    }
    
    public ArrayList<String> getAllIndividuals(){
        ArrayList<String> individuals = new ArrayList();
        NeededEnvironment nEnv = new NeededEnvironment(ReadWrite.READ);
        nEnv.finish();
        ExtendedIterator<Individual> i = nEnv.getOntModel().listIndividuals();
        while(i.hasNext())
        {
            Individual ind = i.next();
            individuals.add(ind.getLocalName());
        }
        return individuals;
    }
    
    ///getPropertiesOfIndividual
}
