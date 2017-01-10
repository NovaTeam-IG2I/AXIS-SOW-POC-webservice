package rocks.novateam.axis.sow.poc.backend.model;

/**
 * Defines a register structure.
 *
 * @author alex
 */
public abstract class Register {

    /**
     * The Register URI.
     */
    protected String uri;

    /**
     * The Register label.
     */
    protected String label;

    /**
     * Serializes the Register in a JSON.
     *
     * @return The JSON.
     */
    public abstract String toJSON();

    /**
     * Returns a instance the register having this URI with all its data found
     * in the TDB.
     *
     * @param uri The Register URI.
     * @return The register instance.
     */
    public abstract Register getInstance(String uri);
}
