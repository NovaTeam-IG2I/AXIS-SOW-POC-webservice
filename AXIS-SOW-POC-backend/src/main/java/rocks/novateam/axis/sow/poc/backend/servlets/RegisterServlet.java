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
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 * Handles register creation.
 *
 * The HTTP request must:
 * <ul>
 * <li>Contain a <code>class</code> parameter, containing the URI of the class
 * for the new track;</li>
 * <li>Contain a <code>name</code> parameter, containing the name of the new
 * Register.</li>
 * </ul>
 *
 * The HTTP response will have a <code>"application/json</code> MIME type and
 * will contain:
 * <ul>
 * <li><code>{"status": "ok", "uri": uri}</code> if the request succeeded, where
 * <code>uri</code> is the new register's URI;</li>
 * <li><code>{"status: "ko", "message": message}</code> if the request
 * failed.</li>
 * </ul>
 *
 * @author Richard Degenne
 */
public class RegisterServlet extends HttpServlet {

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
        String name = request.getParameter("name");
        String uri = request.getParameter("class");

        if (uri == null || name == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        JsonObjectBuilder json = Json.createObjectBuilder();
        try {
            Individual register = createRegister(uri, name);

            // Build the response object
            json.add("uri", register.getURI());
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
        return "Short description";
    }// </editor-fold>

    /**
     * Creates a new <code>Register</code> in the triple store.
     *
     * This method will also create the associated label.
     *
     * <p>
     * <strong>Warning</strong>: The method does not assert that the given class
     * is a sub-class of <code>Register</code>. This might result in unexpected
     * behavior.
     *
     * @param classUri The new register's class' URI
     * @param name The URN to use for the new <code>Register</code>
     *
     * @return The created {@link Individual}
     *
     * @throws NoSuchElementException When the class could not be found.
     */
    private Individual createRegister(String classUri, String name) {
        String NS = TDBManager.DATAMODEL_NS;
        System.out.println("Name: "+name);
        System.out.println("Class: "+classUri);

        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.WRITE);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());
        Individual register = null;
        try {
        OntClass class_ = model.getOntClass(classUri);

        if (class_ == null) {
            throw new NoSuchElementException("The requested URI does not exist.");
        }

        String registerUri = NS + TDBManager.getUniqueURN(model, name);
        register = class_.createIndividual(registerUri);
        register.setLabel(name, null);
        dataset.commit();
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
            dataset.abort();
        }

        return register;
    }
}
