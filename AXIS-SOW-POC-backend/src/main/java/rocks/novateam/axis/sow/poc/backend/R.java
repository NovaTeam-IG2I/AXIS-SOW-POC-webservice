package rocks.novateam.axis.sow.poc.backend;

/**
 * This class holds all static resources used in the project in various places.
 *
 * It can be defintion for doing SPARQL requests or namespaces.
 *
 * @author Alex Canales
 */
public class R {

    /**
     * The proof of concept namespace.
     */
    public static final String POC_NS = "http://axis.sow/poc/";

    /**
     * The AXIS-CSRM datamodel namespace.
     */
    public static final String DATAMODEL_NS = "http://titan.be/axis-csrm/datamodel/ontology/0.4#";

    /**
     * The CIDOC CRM namespace.
     */
    public static final String CIDOC_NS = "http://www.cidoc-crm.org/cidoc-crm/";

    /**
     * The RDF namespace.
     */
    public static final String RDF_NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * The RDFS namespace.
     */
    public static final String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * The MA namespace.
     */
    public static final String MA_NS = "http://www.w3.org/ns/ma-ont#";

    /**
     * The XSD namespace.
     */
    public static final String XSD_NS = "http://www.w3.org/2001/XMLSchema#";


    /**
     * The prefix used in every SPARQL requests.
     */
    public static final String PREFIX = "PREFIX rdfs: <" + RDFS_NS + ">\n" +
            "PREFIX rdf: <" + RDF_NS + ">\n" +
            "PREFIX datamodel: <" + DATAMODEL_NS + ">\n" +
            "PREFIX cidoc: <" + CIDOC_NS + ">\n" +
            "PREFIX poc: <" + POC_NS + ">\n";

    /**
     * The rdfs label property.
     */
    public static String RDFS_LABEL_PROPERTY = R.RDFS_NS + "label";

}
