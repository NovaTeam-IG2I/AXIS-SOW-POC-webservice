/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import rocks.novateam.axis.sow.poc.backend.R;
import rocks.novateam.axis.sow.poc.backend.helpers.CamelCaseConverter;
import rocks.novateam.axis.sow.poc.backend.helpers.Category;
import rocks.novateam.axis.sow.poc.backend.helpers.InstanceExistenceState;
import rocks.novateam.axis.sow.poc.backend.helpers.TDBHelper;

/**
 * This class was created in order to be able to add, delete, and get registers.
 *
 * @author Mélody
 * @author Olivier Sailly
 */
public class RegisterManager {

    private static final String NS = R.DATAMODEL_NS;

    public RegisterManager() {
    }

    /**
     * Add a predicate to the ontology. Put its name in camelCase before the
     * insertion.
     *
     * @param name The name of the predicate to create
     * @return
     */
    public boolean addPredicate(String name) {
        //put the string in camelCase
        name = CamelCaseConverter.convertToCamelCase(name); // System.out.println(name);
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
        mTDBHelper.getOntModel().createOntProperty(NS + name);
        mTDBHelper.finish();
        return this.predicateExists(name);
    }

    /**
     * Create a new statement with given registers if the given predicate
     * exists.
     *
     * @param subjectURI The URI of the subject
     * @param objectURI The URI of the object
     * @param predicateURI The URI of the predicate
     * @return true if the statement was created, false otherwise
     */
    public boolean addPredicateToRegisters(String subjectURI, String objectURI, String predicateURI) {
        if (predicateExists(predicateURI)) {
            TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
            Resource subject = mTDBHelper.getOntModel().getIndividual(subjectURI);
            Resource object = mTDBHelper.getOntModel().getIndividual(objectURI);
            OntProperty predicate = mTDBHelper.getOntModel().getOntProperty(predicateURI);
            subject.addProperty(predicate, object);
            mTDBHelper.finish();
        } else { //predicate doesn't exist
            return false;
        }
        return (statementExists(subjectURI, predicateURI, objectURI));
    }

    /**
     * Create a statement with given registers names.
     *
     * @param subjectName The name of the subject
     * @param objectName The name of the object
     * @param predicateName The name of the predicate
     * @return true if the statement was created, false otherwise
     */
    public boolean addPredicateToRegistersByNames(String subjectName, String objectName, String predicateName) {
        return addPredicateToRegisters(NS + subjectName, NS + objectName, NS + predicateName);
    }

    /**
     * Checks if the Instance already exists, then if not, checks if the class
     * exists, and add the new Instance and its AFP. The given name is converted
     * to a camelCase syntax string.
     *
     * @param name The name of the Instance to add
     * @param classURI The URI of its class
     * @param properties The properties of the Instance to add: keys are
     * properties URIs and values are objects.
     * @return true if the Instance was added successfully or already exists,
     * false otherwise
     */
    public boolean addRegisterInstance(String name, String classURI, Map<String, String> properties) {
        String label = name;
        name = CamelCaseConverter.convertToCamelCase(name);
        //if this instance already exist
        if (instanceExistsByInstanceName(name) == InstanceExistenceState.EXISTS) {
            return true; // This ind already exists
        }
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
        OntModel model = mTDBHelper.getOntModel();
        OntClass mOntClass = model.getOntClass(classURI);
        if (mOntClass == null) {//if the ont class does not exist
            mTDBHelper.finish();
            return false;
        }

        Individual mAFP = model.getOntClass(NS + "AFP").createIndividual(NS + name + "_AFP");
        Individual mIndividual = mOntClass.createIndividual(NS + name);
        mIndividual.addLabel(label, "EN");
        for (Map.Entry<String, String> currentProperty : properties.entrySet()) {
            Property property = model.getProperty(currentProperty.getKey());
            Literal value = ResourceFactory.createStringLiteral(currentProperty.getValue());
            mIndividual.addLiteral(property, value);
        }
        mIndividual.addProperty(model.getProperty(NS + "isDeclaredBy"), mAFP);
        mTDBHelper.finish();

        return (instanceExistsByInstanceName(name) == InstanceExistenceState.EXISTS); //test if it has been created
    }

    /**
     *
     * Checks if the Instance already exists, then if not, checks if the class
     * exists, and add the new Instance and its AFP. The given name is converted
     * to a camelCase syntax string.
     *
     * @param name The name of the Instance to add
     * @param className The name of its class
     * @param properties The properties of the Instance to add
     * @return true if the Instance was added successfully or already exists,
     * false otherwise
     */
    public boolean addRegisterInstanceByClassName(String name, String className, Map<String, String> properties) {
        return addRegisterInstance(name, NS + className, properties);
    }

    /**
     * Delete an instance by uri.
     *
     * @param uri
     * @return
     */
    public boolean deleteInstance(String uri) {
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.WRITE);
        try {
            OntResource resource = mTDBHelper.getOntModel().getOntResource(uri);
            OntResource resourceAFP = mTDBHelper.getOntModel().getOntResource(uri + "_AFP");
            resource.remove();
            resourceAFP.remove();
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
        } finally {
            mTDBHelper.finish();
        }
        return (this.instanceExists(uri) == InstanceExistenceState.DOES_NOT_EXIST);
    }

    /**
     * Delete an instance by name.
     *
     * @param name
     * @return
     */
    public boolean deleteInstanceByName(String name) {
        return deleteInstance(NS + name);
    }

    /**
     *
     * Get all the Individuals from the current OntModel.
     *
     * @return An ArrayList of Individual
     */
    public ArrayList<Individual> getAllIndividuals() {
        ArrayList<Individual> individuals = new ArrayList();
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        ExtendedIterator<Individual> i = mTDBHelper.getOntModel().listIndividuals();
        while (i.hasNext()) {
            Individual ind = i.next();
            individuals.add(ind);
        }
        return individuals;
    }

    /**
     *
     * Get all the Local Names of Individuals from the current OntModel.
     *
     * @return An ArrayList of String containing all LocalNames
     */
    public ArrayList<String> getAllIndividualsLocalNames() {
        ArrayList<String> individuals = new ArrayList();
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        ExtendedIterator<Individual> i = mTDBHelper.getOntModel().listIndividuals();
        while (i.hasNext()) {
            Individual ind = i.next();
            individuals.add(ind.getLocalName());
        }
        return individuals;
    }

    /**
     * Get recursively all categories from the given class. This public method
     * is used to instantiate variables, before looping, and to return the
     * result.
     *
     * @param classURI URI of the class you want to retrieve categories from
     *
     * @return A list of categories
     */
    public ArrayList<Category> getCategoriesRecusively(String classURI) throws NullPointerException { // before the first loop, we have to create our variables
        ArrayList<Category> categories = new ArrayList<>();
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ, false);
        mTDBHelper.finish();
        OntClass mOntClass = mTDBHelper.getOntModel().getOntClass(classURI);
        if (mOntClass == null) {
            throw new NullPointerException("\nError on getting \"" + classURI + "\" OntClass.");
        }
        return getCategoriesRecusively(categories, mOntClass);
    }

    /**
     * Get recursively all categories from the given OntClass. This private
     * method is used to loop, and finally to return the result.
     *
     * @param categories This is the list we want to put register's categories
     * in
     * @param currentOntClass This is the current OntClass from which we want to
     * retrieve categories
     *
     * @return A list of categories
     */
    private ArrayList<Category> getCategoriesRecusively(ArrayList<Category> categories, OntClass currentOntClass) {
        Category c = new Category(currentOntClass.getLocalName());
        if (currentOntClass.hasSubClass()) {
            ExtendedIterator<OntClass> iter = currentOntClass.listSubClasses();
            while (iter.hasNext()) {
                OntClass currentLoopOntClass = iter.next();
                c.addSubCategories(getCategoriesRecusively(new ArrayList<Category>(), currentLoopOntClass));
            }
        }
        categories.add(c);
        return categories;
    }

    /**
     * Get recursively all categories from the given class. This public method
     * is used to instantiate variables, before looping, and to return the
     * result.
     *
     * @param className Name of the class you want to retrieve categories from
     *
     * @return A list of categories
     */
    public ArrayList<Category> getCategoriesRecusivelyByClassName(String className) throws NullPointerException {
        return getCategoriesRecusively(NS + className);
    }

    /**
     * Get all the DatatypeProperties of a given class.
     *
     * @param uri The uri of the class
     * @return An ArrayList of OntProperty containing all DatatypeProperties
     */
    public ArrayList<OntProperty> getClassOntProperties(String uri) {
        return getClassProperties(uri, true);
    }

    /**
     * Get all the DatatypeProperties of a given class. This public method
     * construct by itself the class uri using the given name.
     *
     * @param className The name of the class
     * @return An ArrayList of OntProperty containing all DatatypeProperties
     */
    public ArrayList<OntProperty> getClassOntPropertiesByClassName(String className) {
        return getClassOntProperties(NS + className);
    }

    /**
     * Get all the DatatypeProperties of a given class. This private method
     * returns, whether a className or an URI is given :
     * <ul>
     * <li>An ArrayList of OntProperty containing all DatatypeProperties if
     * isOntPropertyListWanted is set to true</li>
     * <li>An ArrayList of String containing all DatatypeProperties LocalNames
     * if isOntPropertyListWanted is set to false</li>
     * </ul>
     *
     * @param classNameOrURI The uri of the class to look up
     * @param isOntPropertyListWanted Sets whether an ArrayList of String or of
     * OntProperty is wanted
     * @return An ArrayList of String or OntProperty
     */
    private ArrayList getClassProperties(String classURI, boolean isOntPropertyListWanted) {
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        OntClass mOntClass = mTDBHelper.getOntModel().getOntClass(classURI);
        return (isOntPropertyListWanted ? getClassProperties(mOntClass) : getClassPropertiesLocalNames(mOntClass));
    }

    /**
     *
     * Get all the DatatypeProperties of a given OntClass.
     *
     * @param mOntClass The OntClass to look up
     * @return An ArrayList of OntProperty containing all DatatypeProperties
     */
    public ArrayList<OntProperty> getClassProperties(OntClass mOntClass) {
        ArrayList<OntProperty> propertiesList = new ArrayList();
        ExtendedIterator<OntProperty> exItr = mOntClass.listDeclaredProperties();
        while (exItr.hasNext()) {
            OntProperty prop = exItr.next();
            if (prop.isDatatypeProperty()) {
                propertiesList.add(prop);
            }
        }
        return propertiesList;
    }

    /**
     *
     * Get all the Local Names of DatatypeProperties of a given OntClass.
     *
     * @param mOntClass The OntClass to look up
     * @return An ArrayList of String containing all propertyLocalNames
     */
    public ArrayList<String> getClassPropertiesLocalNames(OntClass mOntClass) {
        ArrayList<String> propertiesNamesList = new ArrayList();
        ExtendedIterator<OntProperty> exItr = mOntClass.listDeclaredProperties();
        while (exItr.hasNext()) {
            OntProperty prop = exItr.next();
            if (prop.isDatatypeProperty()) {
                propertiesNamesList.add(prop.getLocalName());
            }
        }
        return propertiesNamesList;
    }

    /**
     *
     * Get all the Local Names of DatatypeProperties of a given class.
     *
     * @param uri The uri of the class
     * @return An ArrayList of String containing all propertyLocalNames
     */
    public ArrayList<String> getClassPropertiesLocalNames(String uri) {
        return getClassProperties(uri, false);
    }

    /**
     * Get all the Local Names of DatatypeProperties of a given class. This
     * public method construct by itself the class uri using the given name.
     *
     * @param className The name of the class
     * @return An ArrayList of String containing all propertyLocalNames
     */
    public ArrayList<String> getClassPropertiesLocalNamesByClassName(String className) {
        return getClassPropertiesLocalNames(NS + className);
    }

    /**
     * This function gives a map with all properties (and their values) of a
     * given individual.
     *
     * @param uri The individual's uri
     * @return A map with property URIs as keys and property values as values or
     * null if the Individual is not found. Only DatatypeProperties are added
     */
    public Map<String, String> getPropertiesOfAnIndividual(String uri) {
        Map<String, String> properties = new HashMap<>();
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ, false);
        mTDBHelper.finish();
        OntModel model = mTDBHelper.getOntModel();
        Individual individual = model.getIndividual(uri);
        if (individual == null) {
            return null;
        }
        StmtIterator exItr = individual.listProperties();
        while (exItr.hasNext()) {
            Property predicate = exItr.next().getPredicate();
            if (predicate.canAs(DatatypeProperty.class)) {
                properties.put(predicate.getURI(), individual.getPropertyValue(predicate).toString());
            }
        }
        return properties;
    }

    /**
     * This function give a map with all properties (and their values) of a
     * given individual. This method construct the uri with the given name
     *
     * @param individualName The individual's name
     * @return A map with property names as keys and property values as values
     */
    public Map<String, String> getPropertiesOfAnIndividualByIndividualName(String individualName) {
        return getPropertiesOfAnIndividual(NS + individualName);
    }

    /**
     * Get recursively all categories from the Register class. This public
     * method calls getCategoriesRecusively().
     *
     * @return A list of categories
     */
    public ArrayList<Category> getRegisterCategories() {
        return getCategoriesRecusivelyByClassName("Register");
    }

    /**
     * Checks the existence state of a given instance. This public method can be
     * use to check whether an instance exists or not, or if there is an
     * instance without AFP or an AFP withou instance
     *
     * @param uri The instance uri to look up
     *
     * @return An InstanceExistenceState enum
     */
    public InstanceExistenceState instanceExists(String uri) {
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        OntResource resource = mTDBHelper.getOntModel().getOntResource(uri);
        OntResource resourceAFP = mTDBHelper.getOntModel().getOntResource(uri + "_AFP");
        if (resource != null && resourceAFP != null) {
            return InstanceExistenceState.EXISTS;
        } else {
            if (resource == null && resourceAFP == null) {
                return InstanceExistenceState.DOES_NOT_EXIST;
            }
            if (resource != null) {
                return InstanceExistenceState.NO_INSTANCE_FOUND; //ressource exists but has no AFP
            }
            return InstanceExistenceState.NO_AFP_FOUND; //ressource does not exists, but its AFP still exists
        }
    }

    /**
     * Checks the existence state of a given instance. This public method can be
     * use to check whether an instance exists or not, or if there is an
     * instance without AFP or an AFP withou instance
     *
     * @param name The instance name to look up
     *
     * @return An InstanceExistenceState enum
     */
    public InstanceExistenceState instanceExistsByInstanceName(String name) {
        return instanceExists(NS + name);
    }

    /**
     * Checks if a given predicate exists. This public method can be use to
     * check whether a predicate exists or not.
     *
     * @param uri The predicate uri to look up
     *
     * @return true if it exists, false otherwise
     */
    public boolean predicateExists(String uri) {
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        return (mTDBHelper.getOntModel().getOntProperty(uri) != null);
    }

    /**
     * Checks if a given predicate exists. This public method can be use to
     * check whether a predicate exists or not.
     *
     * @param ccName The predicate name to look up
     *
     * @return true if it exists, false otherwise
     */
    public boolean predicateExistsByPredicateName(String ccName) {
        return predicateExists(NS + ccName);
    }

    /**
     * Checks if a given statement exists. This public method can be used to
     * check whether a statement exists or not.
     *
     * @param subjectURI The subject uri to look up
     * @param predicateURI The predicate uri to look up
     * @param objectURI The object uri to look up
     *
     * @return true if it exists, false otherwise
     */
    public boolean statementExists(String subjectURI, String predicateURI, String objectURI) {
        TDBHelper mTDBHelper = new TDBHelper(ReadWrite.READ);
        mTDBHelper.finish();
        //if subject has predicate in his properties and is linked to the object, return true
        return mTDBHelper.getOntModel().getOntResource(subjectURI).hasProperty(mTDBHelper.getOntModel().getOntProperty(predicateURI), mTDBHelper.getOntModel().getOntResource(objectURI));
    }

    /**
     * Checks if a given satement exists. This public method can be used to
     * check whether a satement exists or not.
     *
     * @param subjectName The subject name to look up
     * @param predicateName The predicate name to look up
     * @param objectName The object name to look up
     *
     * @return true if it exists, false otherwise
     */
    public boolean statementExistsByNames(String subjectName, String predicateName, String objectName) {
        return statementExists(NS + subjectName, NS + predicateName, NS + objectName);
    }
}
