package rocks.novateam.axis.sow.poc.frameworks;

import java.util.ArrayList;

/**
 * This class holds all informations about the technical framework.
 *
 * A technical framework describes all technical information of a file like its
 * name, size...
 *
 * @author alex
 */
public class Technical {
    
    private String fileName = "";
    
    /**
     * Fills the object with fake data: DELETE THIS FUNCTION.
     */
    private void fillWithFakeData() {
        fileName = "Selma";
    }

    public Technical(String register) {
        // TODO:
        // 1. SPARQL request
        // 2. Setting everything
        fillWithFakeData();
    }

    /**
     * Exports the information in JSON.
     *
     * @return The information in JSON.
     */
    public String exportJSONFormat() {
        // TODO
        return " { fileName : \"" + fileName + "\" }";
    }
}
