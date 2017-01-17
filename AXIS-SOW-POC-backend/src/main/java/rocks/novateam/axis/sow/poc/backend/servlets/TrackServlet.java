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
import javax.json.JsonArrayBuilder;
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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 *
 * @author richou
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
        
        if(uri==null || name==null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        JsonObjectBuilder json = Json.createObjectBuilder();
        try {
            Individual indexedTrack = createIndexedTrack(uri, name);
            
            // Build the response object
            json.add("uri", indexedTrack.getURI());
            json.add("status", "ok");
        }
        catch(NoSuchElementException | NullPointerException ex) {
            json.add("status", "ko")
                    .add("message", ex.getMessage());
        }
        
        // Send response
        response.setContentType("application/json");
        try(PrintWriter out = response.getWriter()) {
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

    private Individual createIndexedTrack(String uri, String name) throws NoSuchElementException {
        String NS = TDBManager.DATAMODEL_NS;

        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.WRITE);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());
        
        Individual film = model.getIndividual(uri);
        if(film == null) {
            throw new NoSuchElementException("The requested URI does not exist.");
        }
        
        String trackUri = NS+getNextName(model, name);
        OntClass indexedTrackClass = model.getOntClass(NS+"EditingTrack");
        Individual track = indexedTrackClass.createIndividual(trackUri);
        Property usesProperty = model.getProperty(NS+"uses");
        film.addProperty(usesProperty, track);
        OntClass esoStructureClass = model.getOntClass(NS+"ESOStructure");
        Individual structure = esoStructureClass.createIndividual(trackUri+"_ESO");
        Property isDefinedByStructure= model.getProperty(NS + "isDefinedByStructure");
        track.addProperty(isDefinedByStructure, structure);
        dataset.commit();
        
        return track;
    }

    private String getNextName(OntModel model, String name) {
        String NS = TDBManager.DATAMODEL_NS;
        Individual individual = model.getIndividual(NS+name);
        if(individual == null) {
            return name;
        }
        int i=1;
        while(individual != null) {
            individual = model.getIndividual(NS+name);
            i++;
        }
        return name+i;
    }

}
