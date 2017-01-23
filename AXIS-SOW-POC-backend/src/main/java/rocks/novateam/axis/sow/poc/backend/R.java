package rocks.novateam.axis.sow.poc.backend;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

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

    public static class RDF {

        public static final Resource DESCRIPTION_TAG = ResourceFactory.createResource(RDF_NS + "Description");

    }

    public static class XMP {

        public static final Property CREATEDATE = ResourceFactory.createProperty(XMP_NS + "CreateDate");
        public static final Property MODIFYDATE = ResourceFactory.createProperty(XMP_NS + "ModifyDate");
        public static final Property METADATADATE = ResourceFactory.createProperty(XMP_NS + "MetadataDate");
        public static final Property CREATORTOOL = ResourceFactory.createProperty(XMP_NS + "CreatorTool");

    }

    public static class XMPDM {

        public static final Resource PROJECTREF_TAG = ResourceFactory.createResource(XMPDM_NS + "projectRef");
        public static final Resource VIDEOFRAMESIZE_TAG = ResourceFactory.createResource(XMPDM_NS + "videoFrameSize");
        public static final Resource STARTTIMECODE_TAG = ResourceFactory.createResource(XMPDM_NS + "startTimecode");
        public static final Resource ALTTIMECODE_TAG = ResourceFactory.createResource(XMPDM_NS + "altTimecode");
        public static final Resource DURATION_TAG = ResourceFactory.createResource(XMPDM_NS + "duration");
        public static final Resource TRACKS_TAG = ResourceFactory.createResource(XMPDM_NS + "Tracks");
        public static final Resource MARKERS_TAG = ResourceFactory.createResource(XMPDM_NS + "markers");
        
        public static final Property VIDEOFRAMERATE = ResourceFactory.createProperty(XMPDM_NS + "videoFrameRate");
        public static final Property STARTTIME = ResourceFactory.createProperty(XMPDM_NS + "startTime");
        public static final Property DURATION = ResourceFactory.createProperty(XMPDM_NS + "duration");
        public static final Property COMMENT = ResourceFactory.createProperty(XMPDM_NS + "comment");
        public static final Property NAME = ResourceFactory.createProperty(XMPDM_NS + "name");
        public static final Property VALUE = ResourceFactory.createProperty(XMPDM_NS + "value");
        public static final Property SCALE = ResourceFactory.createProperty(XMPDM_NS + "scale");
        public static final Property STARTTIMESCALE = ResourceFactory.createProperty(XMPDM_NS + "startTimeScale");
        public static final Property STARTTIMESAMPLESIZE = ResourceFactory.createProperty(XMPDM_NS + "startTimeSampleSize");
        public static final Property VIDEOFIELDORDER = ResourceFactory.createProperty(XMPDM_NS + "videoFieldOrder");
        public static final Property VIDEOPIXELASPECTRATIO = ResourceFactory.createProperty(XMPDM_NS + "videoPixelAspectRatio");
        public static final Property TRACKTYPE = ResourceFactory.createProperty(XMPDM_NS + "trackType");

    }

    public static class XMPMM {

        public static final Resource HISTORY_TAG = ResourceFactory.createResource(XMPMM_NS + "History");
        public static final Resource INGREDIENTS_TAG = ResourceFactory.createResource(XMPMM_NS + "Ingredients");
        public static final Resource DERIVEDFROM_TAG = ResourceFactory.createResource(XMPMM_NS + "DerivedFrom");
        public static final Resource PANTRY_TAG = ResourceFactory.createResource(XMPMM_NS + "Pantry");

    }

    public static class DC {

        public static final Resource TITLE_TAG = ResourceFactory.createResource(DC_NS + "title");
        public static final Resource DESCRIPTION_TAG = ResourceFactory.createResource(DC_NS + "description");

    }

    public static class CREATORATOM {

        public static final Resource WindowsAtomTag = ResourceFactory.createResource(CREATORATOM_NS + "windowsAtom");
        public static final Resource MacAtomTag = ResourceFactory.createResource(CREATORATOM_NS + "macAtom");

    }

}
