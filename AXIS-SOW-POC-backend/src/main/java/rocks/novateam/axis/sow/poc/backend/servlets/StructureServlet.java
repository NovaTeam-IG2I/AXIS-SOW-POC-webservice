/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 * Fetches the structure of a video.
 *
 * The HTTP request @b must
 * <ul>
 * <li>Be a GET request;</li>
 * <li>Contain a field named <code>uri</code> containing the film's register's
 * URI.</li>
 * </ul>
 *
 * The HTTP response will have a <code>application/json</code> MIME type, and
 * will contain data formatted as per {@link StructureServlet#getIndexedTracks}
 * and {@link StructureServlet#buildFragment}.
 *
 * @author Richard Degenne
 */
public class StructureServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getParameter("uri");
        if (uri == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonObjectBuilder json = Json.createObjectBuilder();
        try {
            // Build the response object
            json.add("status", "ok");
            json.add("indexedTracks", getIndexedTracks(uri));
        } catch (NoSuchElementException ex) {
            json.add("status", "ko")
                    .add("message", ex.getMessage());
        }

        // Send response
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            out.println(json.build().toString());
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    /**
     * Get all the <code>IndexedTrack</code>s of a given <code>Film</code>.
     *
     * The data is formatted as such:
     * <pre>
     * [{
     *   "name": "Name of the IndexedTrack",
     *   "uri":  "URI of the IndexedTrack",
     *   "fragments": [List of fragments]
     * }]
     * </pre>
     *
     * See {@link StructureServlet#buildFragment} for the fragment's format.
     *
     * @param uri The <code>Film</code>'s URI.
     * @return A {@link JsonArray} containing the requested data
     * @throws NoSuchElementException When the <code>Film</code> could not be
     * found.
     *
     * @see {StructureServlet#buildFragment}
     */
    private JsonArray getIndexedTracks(String uri) throws NoSuchElementException {
        String NS = TDBManager.DATAMODEL_NS;
        JsonArrayBuilder indexedTracks = Json.createArrayBuilder();

        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.READ);
        Model base = dataset.getDefaultModel();
        dataset.end();

        // Build the inferred model
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        OntModelSpec ontModelSpec = OntModelSpec.OWL_MEM;
        reasoner.bindSchema(base);
        ontModelSpec.setReasoner(reasoner);
        OntModel model = ModelFactory.createOntologyModel(ontModelSpec, base);

        Individual film = model.getIndividual(uri);
        if (film == null) {
            throw new NoSuchElementException("The requested URI does not exist.");
        }

        Individual object;

        // For all "used" individuals
        for (NodeIterator i = film.listPropertyValues(model.getProperty(NS + "uses")); i.hasNext();) {
            object = i.nextNode().as(Individual.class);

            // If it is an EditingTrack, build it and its fragments, and add it to the response.
            if (object.getOntClass().getLocalName().equals("EditingTrack")) {
                JsonObjectBuilder indexedTrack = Json.createObjectBuilder();
                indexedTrack.add("name", object.getLocalName());
                indexedTrack.add("uri", object.getURI());
                indexedTrack.add("fragments", getFragments(model, object));

                indexedTracks.add(indexedTrack.build());
            }
        }

        return indexedTracks.build();
    }

    /**
     * Get all the <code>Fragment</code>s for a given <code>IndexedTrack</code>.
     *
     * See {@link StructureServlet#buildFragment} for the data's format.
     *
     * @param model The {@link OntModel} of the ontology
     * @param editingTrack The <code>IndexedTrack</code> to look up
     * @return A {@link JsonArray} containing the requested data
     *
     * @see StructureServlet#buildFragment
     */
    private JsonArray getFragments(OntModel model, Individual editingTrack) {
        String NS = TDBManager.DATAMODEL_NS;
        JsonArrayBuilder fragments = Json.createArrayBuilder();
        Individual esoStructure = editingTrack.getPropertyValue(model.getProperty(NS + "isDefinedByStructure")).as(Individual.class);

        // Fragments are stored as a linked list in the ontology.
        // The following instructions iterate over this list.
        Individual fragment = esoStructure.getPropertyValue(model.getProperty(NS + "starts")).as(Individual.class);
        fragments.add(buildFragment(model, fragment));
        while (fragment.hasProperty(model.getProperty(NS + "next"))) {
            try {
                fragment = fragment.getPropertyValue(model.getProperty(NS + "next")).as(Individual.class);
                fragments.add(buildFragment(model, fragment));
            } catch (NullPointerException ex) {
                System.out.println("Error building a fragment: " + ex.getLocalizedMessage());
            }
        }

        return fragments.build();
    }

    /**
     * Get data for a given <code>Fragment</code>.
     *
     * The data will be formatted as such:
     * <pre>
     * {
     *   "type":  "segment"|"point",
     *   "start": <start timestamp>,
     *   "end": <end timestamp, only for Segments>,
     *   "name": "Name of the expressed Register",
     *   "uri": "URI of the expressed Register"
     * }
     * </pre>
     *
     * @param model The {@link OntModel} of the ontology
     * @param fragment The <code>Fragment</code> to look up
     * @return A {@link JsonObject} containing the requested data
     */
    private JsonObject buildFragment(OntModel model, Individual fragment) {
        String NS = TDBManager.DATAMODEL_NS;
        JsonObjectBuilder fragmentJson = Json.createObjectBuilder();

        if (fragment.hasOntClass(NS + "MediaUnifiedSegment")) {
            fragmentJson.add("type", "segment");
            // TODO: Get start and end
            int end = fragment.getPropertyValue(model.getProperty(NS + "hasEnd")).asLiteral().getInt();
            fragmentJson.add("end", end);

        } else if (fragment.hasOntClass(NS + "MediaUnifiedPoint")) {
            fragmentJson.add("type", "point");
            // TODO: Get start
        }
        int start = fragment.getPropertyValue(model.getProperty(NS + "hasStart")).asLiteral().getInt();
        fragmentJson.add("start", start);

        // Note: As of iteration 3, there may be multiple referenced Registers.
        RDFNode registerNode = fragment.getPropertyValue(model.getProperty(NS + "expresses"));
        if (registerNode != null) {
            Individual register = registerNode.as(Individual.class);
            fragmentJson.add("name", register.getLocalName());
            fragmentJson.add("uri", register.getURI());
        }

        return fragmentJson.build();
    }
}
