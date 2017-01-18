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
import org.apache.jena.rdf.model.RDFNode;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 *
 * @author richou
 */
public class FragmentServlet extends HttpServlet {

    private static class Type {

        static String POINT = "point";
        static String SEGMENT = "segment";
    }

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
        String track = request.getParameter("track");
        String register = request.getParameter("register");
        String type = request.getParameter("type");
        String start = request.getParameter("start");
        int startTime = Integer.valueOf(start);

        if (start == null || type == null || register == null || track == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonObjectBuilder json = Json.createObjectBuilder();
        try {
            if (type.equals(Type.POINT)) {
                Individual point = createPoint(track, register, startTime);
                json.add("uri", point.getURI());
                json.add("status", "ok");
            }
        } catch (NoSuchElementException | NullPointerException ex) {
            json.add("status", "ko");
            json.add("message", ex.getMessage());
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

    private Individual createPoint(String trackUri, String registerUri, int start) throws NoSuchElementException, NullPointerException {
        String NS = TDBManager.DATAMODEL_NS;

        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.WRITE);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());

        // Get the track and find its ESOStructure. Create it if need be.
        Individual track = model.getIndividual(trackUri);
        if(track == null) {
            throw new NoSuchElementException("The requested URI could not be found.");
        }
        Individual structure = getStructure(model, track);

        // Create the Point and its data
        OntClass pointClass = model.getOntClass(NS + "MediaUnifiedPoint");
        Individual point = pointClass.createIndividual(NS + TDBManager.getUniqueURN(model, track.getLocalName() + "_Fragment"));
        Property hasStart = model.getProperty(NS + "hasStart");
        point.addLiteral(hasStart, start);
        Property expresses = model.getProperty(NS + "expresses");
        Individual register = model.getIndividual(registerUri);
        if (register == null) {
            throw new NoSuchElementException("The requested URI could not be found.");
        }
        point.addProperty(expresses, register);

        // Link the Point to the Structure
        linkToStructure(model, structure, point);

        dataset.commit();

        return point;
    }

    private Individual getStructure(OntModel model, Individual track) {
        String NS = TDBManager.DATAMODEL_NS;

        if (track == null) {
            throw new NoSuchElementException("The requested URI does not exist.");
        }
        Property isDefinedByStructure = model.getProperty(NS + "isDefinedByStructure");
        Individual structure;
        try {
            structure = track.getPropertyValue(isDefinedByStructure).as(Individual.class);
        } catch (NullPointerException ex) {
            OntClass esoStructureClass = model.getOntClass(NS + "ESOStructure");
            structure = esoStructureClass.createIndividual(track.getURI() + "_ESO");
            track.addProperty(isDefinedByStructure, structure);
        }

        return structure;
    }

    private void linkToStructure(OntModel model, Individual structure, Individual fragment) {
        String NS = TDBManager.DATAMODEL_NS;

        Property starts = model.getProperty(NS + "starts");
        RDFNode fragmentNode = structure.getPropertyValue(starts);
        if (fragmentNode == null) {
            structure.addProperty(starts, fragment);
        } else {
            // Iterate over the linked list until its end
            Property next = model.getProperty(NS + "next");
            Individual element = fragmentNode.as(Individual.class);
            while (element.hasProperty(next)) {
                element = element.getPropertyValue(next).as(Individual.class);
            }
            element.addProperty(next, fragment);
        }
    }
}
