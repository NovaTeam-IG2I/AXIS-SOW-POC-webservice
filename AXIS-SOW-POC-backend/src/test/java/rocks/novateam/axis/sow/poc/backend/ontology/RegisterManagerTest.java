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
import rocks.novateam.axis.sow.poc.backend.R;
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
    
    public void testAlexProblem() {
        Map<String,String> map = new HashMap<>();
        
        map.put("prop1", "valueprop1");
        map.put("prop2", "valueprop2");
        map.put(R.DATAMODEL_NS + "fileName", "Selma.mp4");
        map.put("http://www.cidoc-crm.org/cidoc-crm/P75i_is_possessed_by", "Pathé");
        
        System.out.println("\nMap<String,String> map = new HashMap<>();\n" +
        "map.put(\"prop1\", \"valueprop1\");\n" +
        "map.put(\"prop2\", \"valueprop2\");\n" +
        "map.put(R.DATAMODEL_NS + \"fileName\", \"Selma.mp4\");\n" +
        "map.put(R.DATAMODEL_NS + \"P75i_is_possessed_by\", \"Pathé\");");
        
        System.out.println("mGson.toJson(map, mStringStringMapType.getType()) :\n"+mGson.toJson(map, mStringStringMapType.getType()));
        
        String TYPE_OBJECT = R.DATAMODEL_NS + "AudiovisualWork";
        
        System.out.println("\nExecuting: rm.addRegisterInstance(\"selma\", "+TYPE_OBJECT+", map);");
        boolean b = rm.addRegisterInstance("selma", TYPE_OBJECT, map);
        System.out.println("Done.\nrm.addRegisterInstance(\"selma\", "+TYPE_OBJECT+", map); returned "+(b==true?"true":"false"));
        
        System.out.println("\nExecuting: rm.getPropertiesOfAnIndividualByIndividualName(\"selma\");");
        Map<String, String> result = rm.getPropertiesOfAnIndividualByIndividualName("selma");
        System.out.println("Done.\nm.getPropertiesOfAnIndividualByIndividualName(\"selma\"); returned result");
        System.out.println("mGson.toJson(result, mStringStringMapType.getType()) :\n"+mGson.toJson(result, mStringStringMapType.getType()));
    }

    public void testAlexCode(){
        String FAKE_NAME = "selma";
        String URI = R.DATAMODEL_NS + FAKE_NAME;
        String TYPE_OBJECT = R.DATAMODEL_NS + "AudiovisualWork";
        String FILE_NAME_PROPERTY = "http://titan.be/axis-csrm/datamodel/ontology/0.4#fileName";
        String RIGHTS_PROPERTY = "http://www.cidoc-crm.org/cidoc-crm/P75i_is_possessed_by";
        HashMap<String, String> values = new HashMap<>();

        if(rm.instanceExists(URI) == InstanceExistenceState.EXISTS) {
            rm.deleteInstance(URI);
            System.out.println("Deleting: " + URI);
        }

        values.put(FILE_NAME_PROPERTY, "Selma.mp4");
        values.put(RIGHTS_PROPERTY, "Pathé");
        System.out.println("Adding values:");
        System.out.println(values);

        boolean b = rm.addRegisterInstance(FAKE_NAME, TYPE_OBJECT, values);
        if(!b)
            System.out.println("Imposible to add technical framework");

        System.out.println("\nExecuting: rm.getPropertiesOfAnIndividualByIndividualName(\"selma\");");
        Map<String, String> result = rm.getPropertiesOfAnIndividualByIndividualName("selma");
        System.out.println("Done.\nm.getPropertiesOfAnIndividualByIndividualName(\"selma\"); returned result");
        System.out.println("mGson.toJson(result, mStringStringMapType.getType()) :\n"+mGson.toJson(result, mStringStringMapType.getType()));
    }

    public static void main(String[] args) {
        RegisterManagerTest trm = new RegisterManagerTest();

        //trm.runAllTests();

        //trm.testAlexProblem();
        trm.testAlexCode();

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
