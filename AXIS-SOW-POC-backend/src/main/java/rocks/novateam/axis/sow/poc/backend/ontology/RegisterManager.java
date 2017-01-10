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
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.iterator.ExtendedIterator;
import rocks.novateam.axis.sow.poc.backend.helpers.CamelCaseConverter;
import rocks.novateam.axis.sow.poc.backend.helpers.InstanceExistenceState;
import rocks.novateam.axis.sow.poc.backend.helpers.TDBHelper;

/**
 *
 * @author Mélody
 * @author Olivier Sailly
 */
public class RegisterManager {
    private static final String NS = TDBManager.DATAMODEL_NS;
    
    public RegisterManager() { }

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
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
        Individual thisInd = mTDBHelper.getOntModel().getIndividual(NS+name);
        if(thisInd != null){//if it is impossible to get the individual
            mTDBHelper.finish();
            return false;
        }
        Individual afp = mTDBHelper.getOntModel().getOntClass(NS + "AFP").createIndividual(NS + name + "_AFP");
        OntClass class_ = mTDBHelper.getOntModel().getOntClass(NS+className);
        Individual ind = class_.createIndividual(NS+name);
        ind.addLabel(label,"EN");
        for (Map.Entry<String,String> property : properties.entrySet()) {
            OntProperty prprt = mTDBHelper.getOntModel().createOntProperty(NS+property.getKey()); // create an ontProperty
            ind.addProperty(prprt, property.getValue());
        }
        ind.addProperty(mTDBHelper.getOntModel().getProperty(NS + "isDeclaredBy"), afp);
        mTDBHelper.finish();
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
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
        mTDBHelper.getOntModel().createOntProperty(NS+name);
        mTDBHelper.finish();
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

        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        OntClass class_ = mTDBHelper.getOntModel().getOntClass(NS+className);
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
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ, false);
        mTDBHelper.finish();
        OntClass mOntClass = mTDBHelper.getOntModel().getOntClass(NS+className);
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
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        OntResource resource = mTDBHelper.getOntModel().getOntResource(NS+name);
        OntResource resourceAFP = mTDBHelper.getOntModel().getOntResource(NS+name+"_AFP");
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
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        return (mTDBHelper.getOntModel().getOntProperty(NS+ccName)!=null);
    }
    
    /**
     * Delete an instance by name
     * @param name 
     * @return
     */
    public boolean deleteInstance(String name){
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
        try {
            OntResource resource = mTDBHelper.getOntModel().getOntResource(NS+name);
            OntResource resourceAFP = mTDBHelper.getOntModel().getOntResource(NS+name+"_AFP");
            resource.remove();
            resourceAFP.remove();
        } catch (Exception e){
            System.out.println(e.fillInStackTrace());
        } finally {
            mTDBHelper.finish();
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
            TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
            Resource subject = mTDBHelper.getOntModel().getIndividual(NS+subjectName);
            Resource object = mTDBHelper.getOntModel().getIndividual(NS+objectName);
            OntProperty predicate = mTDBHelper.getOntModel().getOntProperty(NS+predicateName);
            subject.addProperty(predicate, object);
            mTDBHelper.finish();
        } else { //predicate doesn't exist
            return false;
        }
        return (statementExists(subjectName, predicateName, objectName));
    }
    
    public boolean statementExists(String subjectName, String predicateName, String objectName){
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        //if subject has predicate in his properties and is linked to the object, return true
        return mTDBHelper.getOntModel().getOntResource(NS+subjectName).hasProperty(mTDBHelper.getOntModel().getOntProperty(NS+predicateName),mTDBHelper.getOntModel().getOntResource(NS+objectName));
    }
    
    public ArrayList<String> getAllIndividuals(){
        ArrayList<String> individuals = new ArrayList();
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        ExtendedIterator<Individual> i = mTDBHelper.getOntModel().listIndividuals();
        while(i.hasNext())
        {
            Individual ind = i.next();
            individuals.add(ind.getLocalName());
        }
        return individuals;
    }
    
    ///getPropertiesOfIndividual
}
