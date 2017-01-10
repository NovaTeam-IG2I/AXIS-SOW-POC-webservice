package rocks.novateam.axis.sow.poc.model;

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
}
