/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.ontology;

import rocks.novateam.axis.sow.poc.backend.helpers.Category;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import org.apache.jena.util.iterator.ExtendedIterator;

import rocks.novateam.axis.sow.poc.backend.helpers.CamelCaseConverter;

/**
 *
 * @author Mélody
 * @author Olivier Sailly
 */
public class RegisterManager {
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
     */
    public void addRegisterInstance(String name, String className, Map<String,String> properties){
        int cpt = 0;
        String label=name;
        name = CamelCaseConverter.convertToCamelCase(name);
        
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.WRITE);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        Individual thisInd = ont.getIndividual(NS+name);
        //if this instance already exist
        if(thisInd != null){
            System.out.println("This ind already exists");
            ds.commit();
            return;
        }
        
        Individual afp = ont.getOntClass(NS + "AFP").createIndividual(NS + name + "_AFP");
        OntClass class_ = ont.getOntClass(NS+className);
        Individual ind = class_.createIndividual(NS+name);
        ind.addLabel(label,"EN");
/*
        ExtendedIterator<OntProperty> exItr;      
        exItr = class_.listDeclaredProperties();
        while (exItr.hasNext()) {
          OntProperty prop = exItr.next();
          if(prop.isDatatypeProperty()){
            System.out.println("Datatype prop: "+ prop.getLocalName());
            if(cpt<properties.size()){
                ind.addProperty(prop, properties.get(cpt));
            }
            cpt++;
          }
        }
*/

        for (Map.Entry<String,String> property : properties.entrySet()) {
            OntProperty prprt = ont.getOntProperty(NS+property.getKey());
            ind.addProperty(prprt, property.getValue());
        }

        ind.addProperty(ont.getProperty(NS + "isDeclaredBy"), afp);
        ds.commit();
    }
    
    /**
     * Add a predicate to the ontology. Put its name in camelCase 
     * before the insertion.
     * @param name 
     */
    public void addPredicate(String name){
        //put the string in camelCase
        name = CamelCaseConverter.convertToCamelCase(name);
        System.out.println(name);
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.WRITE);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        ont.createOntProperty(name);
        ds.commit();
    }
    
    
    /**
     * Get all the datatypeProperties from the ontModel
     * Add their name to an array called properties and return it
     * @return properties
     */
    public ArrayList getProperties(String className){
        ArrayList properties = new ArrayList();
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.READ);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        ds.end();
        OntClass class_ = ont.getOntClass(NS+className);
        ExtendedIterator<OntProperty> exItr;        
        exItr = class_.listDeclaredProperties();      
        while (exItr.hasNext()) {
          OntProperty prop = exItr.next();
          if(prop.isDatatypeProperty()){
            System.out.println("Datatype prop: "+ prop.getLocalName());
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
     * @return A list of categories
     */
    public ArrayList<Category> getCategoriesRecusively(String className) throws NullPointerException { // before the first loop, we have to create our variables
        ArrayList<Category> categories = new ArrayList<>();
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.READ);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        ont.setStrictMode(false);
        /*
            Note :

                setStrictMode() is set to false to solve the problem with the ConversionException with classes as
                ~/interoperabilitymodel/ontology/0.4#AXE

                As stated in http://stackoverflow.com/a/17447438/7358724 , we have to call setStrictMode(false) on our OntModel,
                in order to be able to view every resource as a class, by switching off strict checking.
        */
        ds.end();
        OntClass mOntClass = ont.getOntClass(NS+className);
        if(mOntClass == null) throw new NullPointerException("\nError on getting \""+NS+className+"\" OntClass.");
        System.out.println("Processing class: " + mOntClass.getLocalName());
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
                System.out.println("Processing class: " + currentLoopOntClass.getLocalName());
                c.addSubCategories(getCategoriesRecusively(new ArrayList<Category>(), currentLoopOntClass));
            }
        }
        categories.add(c);
        return categories;
    }
    
    /**
     * Delete an instance by name
     * @param name 
     */
    public void deleteInstance(String name){
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.WRITE);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        try {
        OntResource resource = ont.getOntResource(NS+name);
        OntResource resourceAFP = ont.getOntResource(NS+name+"_AFP");
        resource.remove();
        resourceAFP.remove();
        }
        catch (Exception e){
            System.out.println(e.fillInStackTrace());
        }
        ds.commit();
    }
    
    /**
     * Create a statement with given registers
     * @param subjectName
     * @param objectName
     * @param predicateName 
     */
    public void addPredicateToRegisters(String subjectName, String objectName, String predicateName){
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.WRITE);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        Resource subject = ont.getIndividual(NS+subjectName);
        Resource object = ont.getIndividual(NS+objectName);
        OntProperty predicate = ont.getOntProperty(NS+predicateName);
        subject.addProperty(predicate, object);
        ds.commit();
    }
    
    public ArrayList<String> getAllIndividuals(){
        ArrayList<String> individuals = new ArrayList();
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.READ);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        ExtendedIterator<Individual> i = ont.listIndividuals();
        while(i.hasNext())
        {
            Individual ind = i.next();
            System.out.println("Processing individual: " + ind.getLocalName() + " of category : "+ind.getOntClass().getLocalName());
            individuals.add(ind.getLocalName());
        }
        ds.commit();
        return individuals;
    }
    
    ///getPropertiesOfIndividual
    
    
    
    public static void main(String[] args) {
        RegisterManager rm = new RegisterManager();
        //System.out.println("Executing: rm.getProperties(\"PhysicalPerson\");");
        //rm.getProperties("PhysicalPerson");
        //System.out.println("\nExecuting: rm.getProperties(\"AXE\");");
        //rm.getProperties("AXE");
        //rm.getCategories();
        /*System.out.println("\nExecuting: rm.getRegisterCategories();");
        ArrayList<Category> arc = rm.getRegisterCategories();
        for(Category c : arc) System.out.println(c.toTree());*/
        //System.out.println("\nExecuting: rm.getCategoriesRecusively(\"AXE\");");
        //rm.getCategoriesRecusively("PhysicalPerson");
        //System.out.println("\nExecuting: rm.getCategoriesRecusively(\"Document\");");
        //ArrayList<Category> arc = rm.getCategoriesRecusively("Document");
        //for(Category c : arc) System.out.println(c.toTree());
        Map<String,String> map = new HashMap<>();
        map.put("prop1", "valueprop1");
        map.put("prop2", "valueprop2");
        rm.deleteInstance("testAvecMap");
        rm.addRegisterInstance("TEst avec Map", "PhysicalPerson",map);
        
        //rm.getAllIndividuals();
        //rm.deleteInstance("TEEEEEEEEST2");
        rm.getAllIndividuals();
        //rm.getProperties("PhysicalPerson");
        //rm.getCategories();
        //rm.getCategoriesRecusively();
        /*ArrayList<String> al = new ArrayList();
        al.add("p1");
        al.add("p2");
        al.add("p2");
        al.add("p2");
        al.add("p2");
        al.add("p2");
        al.add("p2");
        al.add("p2");
        rm.addRegisterInstance("InstanceNameTest","MoralPerson",al);*/
        //rm.addPredicate("aaah dzgeb dee_fbj");
        //rm.addRegisterInstance("Martin Luther King");
        //rm.addSubRegisterInstance("Test");
        //rm.deleteInstance("Test");
    }
}
