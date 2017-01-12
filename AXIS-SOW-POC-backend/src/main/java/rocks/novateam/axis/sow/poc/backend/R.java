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
    public static final String PREFIX = "PREFIX rdfs: <" + RDFS_NS + ">\n"
            + "PREFIX rdf: <" + RDF_NS + ">\n"
            + "PREFIX datamodel: <" + DATAMODEL_NS + ">\n"
            + "PREFIX cidoc: <" + CIDOC_NS + ">\n"
            + "PREFIX poc: <" + POC_NS + ">\n";

    /**
     * The namespaces below can be found in some XMP data
     */
    public static final String XMP_NS = "http://ns.adobe.com/xap/1.0/";
    public static final String XMPDM_NS = "http://ns.adobe.com/xmp/1.0/DynamicMedia/";
    public static final String STDIM_NS = "http://ns.adobe.com/xap/1.0/sType/Dimensions#";
    public static final String XMPMM_NS = "http://ns.adobe.com/xap/1.0/mm/";
    public static final String STEVT_NS = "http://ns.adobe.com/xap/1.0/sType/ResourceEvent#";
    public static final String STREF_NS = "http://ns.adobe.com/xap/1.0/sType/ResourceRef#";
    public static final String BEXT_NS = "http://ns.adobe.com/bwf/bext/1.0/";
    public static final String TMRUSH_NS = "http://ns.adobe.com/dva/dynamic/TM-Rush/1.0/";
    public static final String DC_NS = "http://purl.org/dc/elements/1.1/";
    public static final String PHOTOSHOP_NS = "http://ns.adobe.com/photoshop/1.0/";
    public static final String TIFF_NS = "http://ns.adobe.com/tiff/1.0/";
    public static final String EXIF_NS = "http://ns.adobe.com/exif/1.0/";
    public static final String CREATORATOM_NS = "http://ns.adobe.com/creatorAtom/1.0/";
    public static final String TMFILMV2_NS = "http://ns.adobe.com/dva/dynamic/TM-Film-v2/1.0/";

}
