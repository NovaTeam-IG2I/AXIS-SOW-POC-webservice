package rocks.novateam.axis.sow.poc.backend.model;

/**
 * Register Factory
 *
 * @author Alex Canales
 */
public class RegisterFactory {

    /**
     * Gets an instance the register having this URI with all its data found in
     * the TDB.
     *
     * @param type The register type.
     * @param uri The register uri.
     * @return The register instance.
     */
    public static Register getInstance(String type, String uri) {
        switch(type) {
            default:
                return new NullRegister();
        }
    }
}
