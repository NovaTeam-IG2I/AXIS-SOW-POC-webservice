/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntResource;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 * Lists all the available media.
 * 
 * The HTTP response will have a <code>application/json</code> MIME type, and may contain:
 * 
 * <ul>
 *     <li><code>{'status': 'ok', 'videos': <em>videos</em>}</code> if the request succeeded ;</li>
 *     <li><code>{'status': 'ko', 'message': <em>message</em>}</code> if the request failed.</li>
 * </ul>
 * 
 * The <code>videos</code> attribute is an array of objects of the following form:
 * 
 * <pre>
 * "id": media id
 * </pre>
 * 
 * @author richou
 */
public class ListServlet extends HttpServlet {

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
        JsonObjectBuilder json = Json.createObjectBuilder();
        JsonArrayBuilder videos = Json.createArrayBuilder();

        try {
            for(JsonObject data : getData()) {
                videos.add(data);
            }
            
            // Build the response object
            json.add("status", "ok");
            json.add("videos", videos);
        } catch (IOException ex) {
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
        return "Lists all the available media.";
    }// </editor-fold>

    /**
     * Lists all films present in the triple store.
     *
     * @return an Iterable containing film data as {@link JsonObject}s.
     */
    private Iterable<JsonObject> getData() throws IOException {
        String NS = TDBManager.DATAMODEL_NS;
        ArrayList<JsonObject> jsons = new ArrayList<>();
        
        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.READ);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());
        dataset.end();
        
        for(Iterator i = model.getOntClass(NS+"Film").listInstances(); i.hasNext() ;) {
            JsonObjectBuilder json = Json.createObjectBuilder();
            Individual film = ((OntResource) i.next()).asIndividual();
            Individual document = film.getPropertyValue(model.getProperty(NS+"hasExpression")).as(Individual.class);
            String title = document.getPropertyValue(model.getProperty("http://www.w3.org/ns/ma-ont#title")).asLiteral().getString();
            Individual embodiment = document.getPropertyValue(model.getProperty(NS+"hasManifestation")).as(Individual.class);
            Individual location = embodiment.getPropertyValue(model.getProperty(NS+"hasLocation")).as(Individual.class);
            String filename = location.getPropertyValue(model.getProperty(NS+"hyperlink")).asLiteral().getString();
            
            json.add("uri", film.getURI());
            json.add("title", title);
            json.add("id", getId(filename));
            
            jsons.add(json.build());
        }
        
        return jsons;
    }
    
    private int getId(String filename) {
        return Integer.valueOf(FilenameUtils.getBaseName(filename));
    }
}
