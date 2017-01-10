/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.ontology;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
    private final Gson mGson;
    private final TypeToken<ArrayList<Category>> mCategoriesListType;
    private final TypeToken<ArrayList<String>> mStringListType;
    private final TypeToken<Map<String,String>> mStringStringMapType;

    public RegisterManagerTest(){
        this.rm = new RegisterManager();
        this.mGson = new Gson();
        this.mCategoriesListType = new TypeToken<ArrayList<Category>>(){};
        this.mStringListType = new TypeToken<ArrayList<String>>(){};
        this.mStringStringMapType = new TypeToken<Map<String,String>>(){};
    }

    public void runAllTests(){
        System.out.println("Running all tests...");
        
        testGetProperties();
        testGetRegisterCategories();
        testGetCategoriesRecusively();
        testGetAllIndividuals();

        testAddRegisterInstance();
        testInstanceExists();
        testGetPropertiesValuesOfAnIndividual();
        
        testAddPredicate();
        testPredicateExists();
        
        testAddPredicateToRegisters();

        testDeleteInstance();
        testInstanceExists();
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
        System.out.println("\nMap<String,String> map = new HashMap<>();\nmap.put(\"prop1\", \"valueprop1\");\nmap.put(\"prop2\", \"valueprop2\");");
        System.out.println("mGson.toJson(map, mStringStringMapType.getType()) :\n"+mGson.toJson(map, mStringStringMapType.getType()));
        System.out.println("\nExecuting: rm.addRegisterInstance(\"TEst avec Map\", \"PhysicalPerson\",map);");
        boolean b = rm.addRegisterInstance("TEst avec Map", "PhysicalPerson",map);
        boolean c = rm.addRegisterInstance("TEst avec Map2", "PhysicalPerson",map);
        System.out.println("Done.\nrm.addRegisterInstance(\"TEst avec Map\", \"PhysicalPerson\",map); returned "+(b==true?"true; 2: ":"false;")+(c==true?"true":"false"));
    }

    public void testGetPropertiesValuesOfAnIndividual() {
        System.out.println("\nExecuting: rm.getPropertiesValuesOfAnIndividual(TDBManager.DATAMODEL_NS+\"testAvecMap\");");
        Map<String, String> map = rm.getPropertiesValuesOfAnIndividual(TDBManager.DATAMODEL_NS+"testAvecMap");
        System.out.println("mGson.toJson(map, mStringStringMapType.getType()) :\n"+mGson.toJson(map, mStringStringMapType.getType()));
        System.out.println("Done.");
    }
    
    public void testAddPredicate() {
        System.out.println("\nExecuting: rm.addPredicate(\"i am a test\");");
        boolean b = rm.addPredicate("i am a test");
        System.out.println("Done.\nrm.addPredicate(\"i am a test\"); returned "+(b==true?"true":"false"));
    }

    public void testPredicateExists(){
        System.out.println("\nExecuting: rm.predicateExists(\"iAmATest\");");
        boolean b = rm.predicateExists("iAmATest");
        System.out.println("Done.\nrm.predicateExists(\"iAmATest\"); returned "+(b==true?"true":"false"));
    }
    
    public void testAddPredicateToRegisters() {
        System.out.println("\nExecuting: rm.addPredicateToRegisters(\"testAvecMap\", \"testAvecMap2\", \"iAmATest\");");
        boolean b = rm.addPredicateToRegisters("testAvecMap", "testAvecMap2", "iAmATest");
        System.out.println("Done.\nrm.addPredicateToRegisters(\"testAvecMap\", \"testAvecMap2\", \"iAmATest\"); returned "+(b==true?"true":"false"));
    }

    public void testDeleteInstance() {
        System.out.println("\nExecuting: rm.deleteInstance(\"testAvecMap\");");
        boolean b = rm.deleteInstance("testAvecMap");
        boolean c = rm.deleteInstance("testAvecMap2");
        System.out.println("Done.\nrm.deleteInstance(\"testAvecMap\"); returned "+(b==true?"true; 2: ":"false;")+(c==true?"true":"false"));
    }

    public void testInstanceExists(){
        System.out.println("\nExecuting: rm.instanceExists(\"testAvecMap\");");
        InstanceExistenceState ies = rm.instanceExists("testAvecMap");
        System.out.println("Done.\nrm.instanceExists(\"testAvecMap\"); returned "+ies.toString());
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
