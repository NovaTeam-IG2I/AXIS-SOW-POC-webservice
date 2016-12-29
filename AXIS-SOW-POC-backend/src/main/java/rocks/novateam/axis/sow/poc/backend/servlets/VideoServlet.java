/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.servlets;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 * This servlet allows one to access a video from its Register's URI.
 *
 * The HTTP request @b must
 * <ul>
 * <li>Be a GET request <em>(this might be a bug)</em>;</li>
 * <li>Contain a field named <code>uri</code> containing the film's register's
 * URI.</li>
 * </ul>
 *
 * The HTTP response will have a <code>video/mp4</code> MIME type, and will
 * have:
 * <ul>
 * <li>A <code>200 OK</code> HTTP status code and contain the video if it has
 * been found;</li>
 * <li>A <code>404 Not Found</code> HTTP status code if the video has not been
 * found in the triple store. This is probably due to an invalid URI;</li>
 * <li>A <code>400 Bad Request</code> HTTP statud code if the <code>uri</code>
 * parameter is missing from the request.</li>
 * </ul>
 *
 * <p>
 * <em>Note</em>: This implementation is considered <strong>bad</strong> as of
 * now: the web client will have to download the full video before being able to
 * play it. A way better way to do this is to stream the video. Revamping this
 * servlet might be done in the future.
 * </p>
 *
 * @author Richard Degenne
 */
public class VideoServlet extends HttpServlet {

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

        FileInputStream file = getFile(uri);
        if (file == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Set response headers;
        response.reset();
        response.setContentType("video/mp4");
        response.setHeader("Cache-Control", "public");

        // Open streams.
        // BufferedInputStream input = new BufferedInputStream(file);
        try (BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream());
                BufferedInputStream input = new BufferedInputStream(file)) {
            // Write file contents to response.
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
        return "Retrieve a video from its AXIS-CSRM URI.";
    }// </editor-fold>

    /**
     * Gets a {@link FileInputStream} for the video of the given URI.
     *
     * This method looks up in the triple store for a Film register with the
     * given URI and opens a {@link FileInputStream} to the associated video
     * file.
     *
     * @param uri A {@link String} to the requested film's register.
     * @return A {@link FileInputStream} to the requested video file, or
     * <code>null</code> if the file could not be found.
     */
    private FileInputStream getFile(String uri) {
        String NS = TDBManager.DATAMODEL_NS;
        Dataset dataset = TDBManager.getInstance().getDataset();
        dataset.begin(ReadWrite.READ);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());
        dataset.end();

        try {
            Individual film = model.getIndividual(uri);
            Individual document = film.getPropertyValue(model.getProperty(NS + "hasExpression")).as(Individual.class);
            Individual embodiment = document.getPropertyValue(model.getProperty(NS + "hasManifestation")).as(Individual.class);
            Individual location = embodiment.getPropertyValue(model.getProperty(NS + "hasLocation")).as(Individual.class);
            String filename = location.getPropertyValue(model.getProperty(NS + "hyperlink")).asLiteral().getString();
            return new FileInputStream(filename);
        } catch (NullPointerException | FileNotFoundException ex) {
            Logger.getLogger(VideoServlet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }
}
