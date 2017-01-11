package rocks.novateam.axis.sow.poc.backend.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import rocks.novateam.axis.sow.poc.backend.R;
import rocks.novateam.axis.sow.poc.backend.ontology.RegisterManager;

/**
 * Defines a person.
 *
 * @author Alex Canales
 */
public class Person extends Register {

    /**
     * The person type.
     */
    public static final String TYPE = "person";

    public Person(String uri) {
        this.uri = uri;
        RegisterManager manager = new RegisterManager();

        Map<String, String> values = manager.getPropertiesOfAnIndividual(uri);

        if(values == null)
            return;

        this.label = values.get(R.RDFS_LABEL_PROPERTY);
        System.out.println(label);
    }

    @Override
    public String toJSON() {
        String json = "{\n";
        json += "\"uri\" : \"" + uri + "\",\n";
        json += "\"label\" : \"" + label + "\",\n";
        json += "}";
        return json;
    }

    public static void main(String[] args) throws IOException {
        String personID = R.POC_NS + "MLK";
        Register person = RegisterFactory.getInstance("person", personID);
        System.out.println(person.toJSON());
    }
}
