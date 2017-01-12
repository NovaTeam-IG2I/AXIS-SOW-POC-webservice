/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import rocks.novateam.axis.sow.poc.backend.R;
import rocks.novateam.axis.sow.poc.backend.ontology.RegisterManager;

/**
 *
 * @author Mélody
 */
public class Event extends Register{
    public static final String TYPE = "event";
    
    public String date;
    public String placeOfEvent;
    public String description;
    public String type;

    @Override
    public String toJSON() {
        String json = "{\n";
        json += "\"uri\" : \"" + uri + "\",\n";
        json += "\"label\" : \"" + label + "\"\n";
        json += "\"date\" : \"" + date + "\"\n";
        json += "}";
        return json;
    }

    public Register getInstance(String uri) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This constructor create an object Event with his date and label
     * properties from the ontology
     * @param uri 
     */
    public Event(String uri) {
        RegisterManager rm = new RegisterManager();
        this.uri = uri;
        this.type = TYPE;
        Map<String, String> values = rm.getPropertiesOfAnIndividual(uri);
        System.out.println("Valeurs prop evenement:"+values);
        if(values == null)
            return;

        this.label = values.get(R.DATAMODEL_LABEL_PROPERTY);
        this.date=values.get(R.DATAMODEL_DATE_PROPERTY);
        System.out.println("Label: "+label);
        System.out.println("Date: "+date);
    }

    @Override
    public String toString() {
        return "Event{" + "date=" + date + ", placeOfEvent=" + placeOfEvent + ", description=" + description + ", type=" + type + '}';
    }
    
    public static void main(String[] args) {
        Map<String,String> properties = new HashMap<>();
        properties.put("date","23/09/94");
        RegisterManager rm = new RegisterManager();
        //need to be remove, useful just for a test
        rm.addRegisterInstanceByClassName("melo", "Event", properties);
        ArrayList<String> ars = rm.getAllIndividualsLocalNames();
        for(String s : ars) System.out.println(s);
        Event e = new Event(R.DATAMODEL_NS+"melo");
        System.out.println("Evenement melo");
        System.out.println(e.toString());
    }

    
    
}
