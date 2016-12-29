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
 *
 * @author richou
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
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
