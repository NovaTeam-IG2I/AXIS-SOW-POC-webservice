package rocks.novateam.axis.sow.poc.backend.model;

/**
 * This register defines a null register.
 *
 * If in the factory, the type of the register wanted is unknown, this register
 * is given.
 *
 * @author alex
 */
public class NullRegister extends Register {

    @Override
    public String toJSON() {
        return "{ \"type\" : \"unknown\", \"uri\" : \"" + uri + "\" }";
    }

}
