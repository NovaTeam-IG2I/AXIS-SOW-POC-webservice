package rocks.novateam.axis.sow.poc.frameworks;

/**
 * This class holds all defintions for doing SPARQL requests (URIs and PREFIX).
 * All those definitions should be on the TDB manager part and this class will
 * be removed after this refactoring.
 * 
 * @author Alex Canales
 */
public class Reg {

    /**
     * The proof of concept URI.
     */
    public static final String POC_URI = "http://axis.sow/poc/";

    /**
     * The AXIS-CSRM datamodel URI.
     */
    public static final String DATAMODEL_URI = "http://titan.be/axis-csrm/datamodel/ontology/0.4#";

    /**
     * The CIDOC CRM URI.
     */
    public static final String CIDOC_URI = "http://www.cidoc-crm.org/cidoc-crm/";

    /**
     * The RDF URI.
     */
    public static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * The RDFS URI.
     */
    public static final String RDFS_URI = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * The prefix used in every SPARQL requests.
     */
    public static final String PREFIX = "PREFIX rdfs: <" + RDFS_URI + ">\n" +
            "PREFIX rdf: <" + RDF_URI + ">\n" +
            "PREFIX datamodel: <" + DATAMODEL_URI + ">\n" +
            "PREFIX cidoc: <" + CIDOC_URI + ">\n" +
            "PREFIX poc: <" + POC_URI + ">\n";
}