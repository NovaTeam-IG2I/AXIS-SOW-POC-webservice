/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 * Lists all available Register subclasses.
 *
 * The HTTP response will have a <code>"application/json"</code> MIME type and
 * will contain:
 * <ul>
 * <li><code>{'status': 'ok', 'categories': <em>categories</em>}</code> if the
 * request succeeded ;</li>
 * <li><code>{'status': 'ko', 'message': <em>message</em>}</code> if the request
 * failed.</li>
 * </ul>
 *
 * The <code>categories</code> attribute is an array of objects of the following
 * form:
 *
 * <pre>
 * {"uri": category URI, "name": cateogry name}
 * </pre>
 *
 * @author Richard Degenne
 */
public class CategoriesServlet extends HttpServlet {

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
        JsonArrayBuilder categories = Json.createArrayBuilder();

        try {
            for (JsonObject data : getData()) {
                categories.add(data);
            }

            // Build the response object
            json.add("status", "ok");
            json.add("categories", categories);
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
     * Lists all sub-classes of <code>Register</code> present in the triple
     * store.
     *
     * For each sub-class, builds a {@link JsonObject} containing the class'
     * name and URI.
     *
     * @return An {@link Iterable} of {@link JsonObject}s.
     */
    private Iterable<JsonObject> getData() {
        String NS = TDBManager.DATAMODEL_NS;
        ArrayList<JsonObject> jsons = new ArrayList<>();

        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.READ);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, dataset.getDefaultModel());
        dataset.end();

        OntClass registerClass = model.getOntClass(NS + "Register");
        OntClass current;
        for (ExtendedIterator<OntClass> i = registerClass.listSubClasses(false); i.hasNext();) {
            current = i.next();
            JsonObjectBuilder json = Json.createObjectBuilder();
            try {
                json.add("name", current.getLocalName());
                json.add("uri", current.getURI());
                jsons.add(json.build());
            } catch (NullPointerException ex) {
                System.out.println("Error: " + ex.getMessage());
                System.out.println("Skipping...");
            }
        }

        return jsons;
    }

}
