/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.ontology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import rocks.novateam.axis.sow.poc.backend.helpers.Category;
import rocks.novateam.axis.sow.poc.backend.helpers.InstanceExistenceState;

/**
 *
 * @author Olivier Sailly
 */
public class RegisterManagerTest {
    private final RegisterManager rm;

    public RegisterManagerTest(){
        rm = new RegisterManager();
    }

    public void runAllTests(){
        System.out.println("Running all tests...");
        
        testGetProperties();
        testGetRegisterCategories();
        testGetCategoriesRecusively();
        testGetAllIndividuals();

        testAddRegisterInstance();
        testInstanceExists();
        testDeleteInstance();
        testInstanceExists();
        
        /*
        testAddPredicate();
        testPredicateExists();
        */
    }

    public void testGetRegisterCategories() {
        System.out.println("\nExecuting: rm.getRegisterCategories();");
        ArrayList<Category> arc = rm.getRegisterCategories();
        for(Category c : arc) System.out.println(c.toTree());
        System.out.println("Done.");
    }

    public void testGetCategoriesRecusively() {
        System.out.println("\nExecuting: rm.getCategoriesRecusively(\"Document\");");
        ArrayList<Category> arc = rm.getCategoriesRecusively("Document");
        for(Category c : arc) System.out.println(c.toTree());
        System.out.println("Done.");
    }

    public void testGetProperties() {
        System.out.println("\nExecuting: rm.getProperties(\"PhysicalPerson\");");
        ArrayList<String> ars = rm.getProperties("PhysicalPerson");
        for(String s : ars) System.out.println(s);
        System.out.println("Done.");
    }

    public void testGetAllIndividuals() {
        System.out.println("\nExecuting: rm.getAllIndividuals();");
        ArrayList<String> ars = rm.getAllIndividuals();
        for(String s : ars) System.out.println(s);
        System.out.println("Done.");
    }

    public void testAddRegisterInstance() {
        Map<String,String> map = new HashMap<>();
        map.put("prop1", "valueprop1");
        map.put("prop2", "valueprop2");
        System.out.println("\nExecuting: rm.addRegisterInstance(\"TEst avec Map\", \"PhysicalPerson\",map);");
        rm.addRegisterInstance("TEst avec Map", "PhysicalPerson",map);
        System.out.println("Done.");
    }

    public void testDeleteInstance() {
        System.out.println("\nExecuting: rm.deleteInstance(\"testAvecMap\");");
        boolean b = rm.deleteInstance("testAvecMap");
        System.out.println("Done.\nrm.deleteInstance(\"testAvecMap\"); returned "+(b==true?"true":"false"));
    }

    public void testInstanceExists(){
        System.out.println("\nExecuting: rm.instanceExists(\"testAvecMap\");");
        InstanceExistenceState ies = rm.instanceExists("testAvecMap");
        System.out.println("Done.\nrm.instanceExists(\"testAvecMap\"); returned "+ies.toString());
    }
    
    public void testAddPredicate() {
        throw new UnknownError("Predicate not created");
        /*System.out.println("\nExecuting: rm.addPredicate(\"i am a test\");");
        boolean b = rm.addPredicate("i am a test");
        System.out.println("Done.\nrm.addPredicate(\"i am a test\"); returned "+(b==true?"true":"false"));*/
    }
    
    public void testPredicateExists(){
        throw new UnknownError("Nothing to test");
        /*System.out.println("\nExecuting: rm.predicateExists(\"\");");
        boolean b = rm.predicateExists("");
        System.out.println("Done.\nrm.predicateExists(\"\"); returned "+(b==true?"true":"false"));*/
    }

    public static void main(String[] args) {
        RegisterManagerTest trm = new RegisterManagerTest();

        trm.runAllTests();

        //trm.testAddPredicate(); - FAILED
        //trm.testAddRegisterInstance(); - FIXED
        //trm.testInstanceExists(); - WORKS
        //trm.testDeleteInstance(); - WORKS
    }

    // OLD DATA
    // rm.addRegisterInstance("Martin Luther King");
    // rm.addSubRegisterInstance("Test");
    // rm.deleteInstance("Test");
}
