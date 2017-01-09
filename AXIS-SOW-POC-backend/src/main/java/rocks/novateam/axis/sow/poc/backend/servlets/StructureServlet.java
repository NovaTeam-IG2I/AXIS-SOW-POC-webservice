/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.servlets;

import java.io.IOException;
import java.util.ArrayList;
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
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 *
 * @author richou
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

    private JsonArray getIndexedTracks(String uri) {
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
        
        Individual film = model.getIndividual(NS+uri);
        Statement statement;
        Individual object;
        OntClass editingTrackClass = model.getOntClass(NS+"EditingTrack");
        
        // For all "used" individuals
        for(StmtIterator i = film.listProperties(model.getProperty(NS+"uses")) ; i.hasNext() ;) {
            statement = i.nextStatement();
            object = statement.getObject().as(Individual.class);
             
            if(object.getOntClass() == editingTrackClass) {
                JsonObjectBuilder indexedTrack = Json.createObjectBuilder();
                indexedTrack.add("name", object.getLocalName());
                indexedTrack.add("uri", object.getURI());
                indexedTrack.add("segments", getSegments(model, object));
                
                indexedTracks.add(indexedTrack.build());
            }
        }
        
        return indexedTracks.build();
    }

    private JsonArray getSegments(OntModel model, Individual editingTrack) {
        String NS = TDBManager.DATAMODEL_NS;
        JsonArrayBuilder segments = Json.createArrayBuilder();

        Individual esoStructure = editingTrack.getPropertyValue(model.getProperty(NS+"isDefinedByStructure")).as(Individual.class);
        //TODO: Build segments
        return segments.build();
    }
}
