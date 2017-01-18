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
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 * Handles track creation.
 *
 * The HTTP request must:
 * <ul>
 * <li>Contain a <code>uri</code> parameter, containing the URI of the
 * <code>Film</code> for the new track;</li>
 * <li>Contain a <code>name</code> parameter, containing the URN of the new
 * Track.</li>
 * </ul>
 *
 * The HTTP response will have a <code>"application/json</code> MIME type and
 * will contain:
 * <ul>
 * <li><code>{"status": "ok", "uri": uri}</code> if the request succeeded, where
 * <code>uri</code> is the new track's URI;</li>
 * <li><code>{"status: "ko", "message": message}</code> if the request
 * failed.</li>
 * </ul>
 *
 * @author Richard Degenne
 */
public class TrackServlet extends HttpServlet {

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
        String name = request.getParameter("name");

        if (uri == null || name == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonObjectBuilder json = Json.createObjectBuilder();
        try {
            Individual indexedTrack = createIndexedTrack(uri, name);

            // Build the response object
            json.add("uri", indexedTrack.getURI());
            json.add("status", "ok");
        } catch (NoSuchElementException | NullPointerException ex) {
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
        return "Handles track creation.";
    }// </editor-fold>

    /**
     * Creates a new <code>EditingTrack</code> in the triple store.
     *
     * This method will also create the associated <code>ESOStructure</code> and
     * link all the different entities.
     *
     * @param uri The <code>Film</code>'s URI
     * @param name The URN to use for the new <code>Track</code>
     *
     * @return The created {@link Individual}
     *
     * @throws NoSuchElementException When the <code>Film</code> could not be
     * found.
     */
    private Individual createIndexedTrack(String uri, String name) throws NoSuchElementException {
        String NS = TDBManager.DATAMODEL_NS;

        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.WRITE);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());

        Individual film = model.getIndividual(uri);
        if (film == null) {
            throw new NoSuchElementException("The requested URI does not exist.");
        }

        String trackUri = NS + TDBManager.getUniqueURN(model, name);
        OntClass indexedTrackClass = model.getOntClass(NS + "EditingTrack");
        Individual track = indexedTrackClass.createIndividual(trackUri);
        Property usesProperty = model.getProperty(NS + "uses");
        film.addProperty(usesProperty, track);
        OntClass esoStructureClass = model.getOntClass(NS + "ESOStructure");
        Individual structure = esoStructureClass.createIndividual(trackUri + "_ESO");
        Property isDefinedByStructure = model.getProperty(NS + "isDefinedByStructure");
        track.addProperty(isDefinedByStructure, structure);
        dataset.commit();

        return track;
    }

}
