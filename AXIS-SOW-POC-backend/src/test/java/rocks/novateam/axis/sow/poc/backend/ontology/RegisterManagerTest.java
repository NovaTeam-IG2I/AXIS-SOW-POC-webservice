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
        
        testGetClassPropertiesLocalNamesByClassName();
        testGetRegisterCategories();
        testGetCategoriesRecusivelyByClassName();
        testGetAllIndividualsLocalNames();

        testAddRegisterInstanceByClassName();
        testInstanceExistsByInstanceName();
        testGetPropertiesOfAnIndividualByIndividualName();
        
        testAddPredicate();
        testPredicateExistsByPredicateName();
        
        testAddPredicateToRegistersByNames();

        testDeleteInstanceByName();
        testInstanceExistsByInstanceName();
    }

    public void testGetRegisterCategories() {
        System.out.println("\nExecuting: rm.getRegisterCategories();");
        ArrayList<Category> arc = rm.getRegisterCategories();
        for(Category c : arc) System.out.println(c.toTree());
        System.out.println("Done.");
    }

    public void testGetCategoriesRecusivelyByClassName() {
        System.out.println("\nExecuting: rm.getCategoriesRecusivelyByClassName(\"Document\");");
        ArrayList<Category> arc = rm.getCategoriesRecusivelyByClassName("Document"); // build the uri, then calls getCategoriesRecusively
        for(Category c : arc) System.out.println(c.toTree());
        System.out.println("Done.");
    }

    public void testGetClassPropertiesLocalNamesByClassName() {//if this method works, other methods to get properties will work
        System.out.println("\nExecuting: rm.getClassPropertiesLocalNamesByClassName(\"PhysicalPerson\");");
        ArrayList<String> ars = rm.getClassPropertiesLocalNamesByClassName("PhysicalPerson"); // build the uri, then calls getClassProperties
        for(String s : ars) System.out.println(s);
        System.out.println("Done.");
    }

    public void testGetAllIndividualsLocalNames() {
        System.out.println("\nExecuting: rm.getAllIndividualsLocalNames();");
        ArrayList<String> ars = rm.getAllIndividualsLocalNames();
        for(String s : ars) System.out.println(s);
        System.out.println("Done.");
    }

    public void testAddRegisterInstanceByClassName() {
        Map<String,String> map = new HashMap<>();
        map.put("prop1", "valueprop1");
        map.put("prop2", "valueprop2");
        System.out.println("\nMap<String,String> map = new HashMap<>();\nmap.put(\"prop1\", \"valueprop1\");\nmap.put(\"prop2\", \"valueprop2\");");
        System.out.println("mGson.toJson(map, mStringStringMapType.getType()) :\n"+mGson.toJson(map, mStringStringMapType.getType()));
        System.out.println("\nExecuting: rm.addRegisterInstance(\"TEst avec Map\", \"PhysicalPerson\",map);");
        boolean b = rm.addRegisterInstanceByClassName("TEst avec Map", "PhysicalPerson",map);  // build the uri, then calls addRegisterInstance
        boolean c = rm.addRegisterInstanceByClassName("TEst avec Map2", "PhysicalPerson",map); // build the uri, then calls addRegisterInstance
        System.out.println("Done.\nrm.addRegisterInstance(\"TEst avec Map\", \"PhysicalPerson\",map); returned "+(b==true?"true; 2: ":"false;")+(c==true?"true":"false"));
    }

    public void testGetPropertiesOfAnIndividualByIndividualName() {
        System.out.println("\nExecuting: rm.getPropertiesOfAnIndividualByIndividualName(\"testAvecMap\");");
        Map<String, String> map = rm.getPropertiesOfAnIndividualByIndividualName("testAvecMap"); // build the uri, then calls getPropertiesValuesOfAnIndividual
        System.out.println("mGson.toJson(map, mStringStringMapType.getType()) :\n"+mGson.toJson(map, mStringStringMapType.getType()));
        System.out.println("Done.");
    }
    
    public void testAddPredicate() {
        System.out.println("\nExecuting: rm.addPredicate(\"i am a test\");");
        boolean b = rm.addPredicate("i am a test");
        System.out.println("Done.\nrm.addPredicate(\"i am a test\"); returned "+(b==true?"true":"false"));
    }

    public void testPredicateExistsByPredicateName(){
        System.out.println("\nExecuting: rm.predicateExistsByPredicateName(\"iAmATest\");");
        boolean b = rm.predicateExistsByPredicateName("iAmATest"); // build uri then calls predicateExists
        System.out.println("Done.\nrm.predicateExistsByPredicateName(\"iAmATest\"); returned "+(b==true?"true":"false"));
    }
    
    public void testAddPredicateToRegistersByNames() {
        System.out.println("\nExecuting: rm.addPredicateToRegistersByNames(\"testAvecMap\", \"testAvecMap2\", \"iAmATest\");");
        boolean b = rm.addPredicateToRegistersByNames("testAvecMap", "testAvecMap2", "iAmATest"); // build uri then calls addPredicateToRegistersByNames
        System.out.println("Done.\nrm.addPredicateToRegistersByNames(\"testAvecMap\", \"testAvecMap2\", \"iAmATest\"); returned "+(b==true?"true":"false"));
    }

    public void testDeleteInstanceByName() {
        System.out.println("\nExecuting: rm.deleteInstance(\"testAvecMap\");");
        boolean b = rm.deleteInstanceByName("testAvecMap");  // build uri then calls deleteInstance
        boolean c = rm.deleteInstanceByName("testAvecMap2"); // build uri then calls deleteInstance
        System.out.println("Done.\nrm.deleteInstance(\"testAvecMap\"); returned "+(b==true?"true; 2: ":"false;")+(c==true?"true":"false"));
    }

    public void testInstanceExistsByInstanceName(){
        System.out.println("\nExecuting: rm.instanceExistsByInstanceName(\"testAvecMap\");");
        InstanceExistenceState ies = rm.instanceExistsByInstanceName("testAvecMap"); // build uri then calls instanceExists
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
