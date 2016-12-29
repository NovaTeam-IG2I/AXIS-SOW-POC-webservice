/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.ontology;

import java.util.ArrayList;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;

import org.apache.jena.util.iterator.ExtendedIterator;

import rocks.novateam.axis.sow.poc.tools.CamelCaseConverter;

/**
 *
 * @author Mélody
 */
public class RegisterManager {
    private static final String NS = TDBManager.DATAMODEL_URL+"#";
    
    private TDBManager tdbm;
    
    public RegisterManager() {
        tdbm = TDBManager.getInstance();
    }

    /**
     * TODO: AFP + search existence + put name given in labal and camelCase in ont
     * @param name
     * @param className
     * @param properties 
     */
    public void addRegisterInstance(String name, String className, ArrayList<String> properties){
        int cpt = 0;
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.WRITE);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        OntClass class_ = ont.getOntClass(NS+className);
        ExtendedIterator<OntProperty> exItr;        
        exItr = class_.listDeclaredProperties();
        Individual ind = class_.createIndividual(NS+name);
        while (exItr.hasNext()) {
          OntProperty prop = exItr.next();
          if(prop.isDatatypeProperty()){
            System.out.println("Datatype prop: "+ prop.getLocalName());
            ind.addProperty(prop, properties.get(cpt));  
            cpt++;
          }
        }
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
    public ArrayList getProperties(){
        ArrayList properties = new ArrayList();
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.WRITE);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        
        ExtendedIterator<DatatypeProperty> exItr;        
        exItr = ont.listDatatypeProperties();      
        while (exItr.hasNext()) {
          DatatypeProperty prop = exItr.next();
          System.out.println("Datatype prop: "+ prop.getLocalName());
          properties.add(prop.getLocalName());
        }
        ds.commit();
        return properties;
    }
    
    public ArrayList<Category> getCategories(){
        ArrayList<Category> cats = new ArrayList();
        ArrayList<Category> cats2 = new ArrayList();
        Category cat = new Category();
        Category cat2 = new Category();
        Dataset ds = tdbm.getDataset();
        ds.begin(ReadWrite.WRITE);
        Model model = ds.getDefaultModel();
        OntModel ont = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model);
        OntClass classRegister = ont.getOntClass(NS+"Register");
        System.out.println(classRegister);
        ExtendedIterator<OntClass> exItr;
        ExtendedIterator<OntClass> exItr2;
        if(classRegister.hasSubClass()){
            System.out.println("subclass!!!!");
        }
        exItr = classRegister.listSubClasses();
        System.out.println(exItr);
        for (exItr = classRegister.listSubClasses(); exItr.hasNext();) {
            System.out.println("blblbl");
          OntClass class_ = exItr.next();
          cat.setLabel(class_.getLocalName());
          if(class_.hasSubClass()){
              exItr2 = class_.listSubClasses();
              while(exItr2.hasNext()){
                  OntClass subClass= exItr2.next();
                  cat2.setLabel(subClass.getLocalName());
                  cats2.add(cat2);
              }
              cat.setSubClass(cats2);
          }
          cats.add(cat);
          cats.toString();
        }
        
        ds.commit();
        return cats;
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
        Resource resource = ont.getIndividual(NS+name);
        // remove statements where resource is subject
        ont.removeAll(resource, null, (RDFNode) null);
        // remove statements where resource is object
        ont.removeAll(null, null, resource);
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
    
    
    
    public static void main(String[] args) {
        RegisterManager rm = new RegisterManager();
        //rm.getProperties();
        //rm.getCategories();
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
        rm.addPredicate("aaah dzgeb dee_fbj");
        //rm.addRegisterInstance("Martin Luther King");
        //rm.addSubRegisterInstance("Test");
        //rm.deleteInstance("Test");
    }
}
