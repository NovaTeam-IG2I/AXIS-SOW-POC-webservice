/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.model;

import java.util.HashMap;
import java.util.Map;
import rocks.novateam.axis.sow.poc.backend.ontology.RegisterManager;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 *
 * @author Mélody
 */
public class Event extends Register{
    private static final String NS = TDBManager.DATAMODEL_NS;
    
    public String date;
    public String placeOfEvent;
    public String description;
    public String type;

    @Override
    public String toJSON() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Register getInstance(String uri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Event(String uri) {
        RegisterManager rm = new RegisterManager();
        Map<String,String> map = new HashMap<>();
        map = rm.getPropertiesValuesOfAnIndividual(uri);

    }
    
    //public getProperty
  
    public static void main(String[] args) {
        Map<String,String> properties = new HashMap<>();
        properties.put("prop1","value1");
        RegisterManager rm = new RegisterManager();
        //need to be remove, useful just for a test
        rm.addRegisterInstance("melo", "Event", properties);
        Event e = new Event(NS+"melo");
    }
    
    
}
