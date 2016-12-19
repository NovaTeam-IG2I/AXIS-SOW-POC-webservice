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
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import rocks.novateam.axis.sow.poc.backend.Configuration;

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
            // For each filename, construct a JSON object
            for (String s : getFilenames()) {
                JsonObjectBuilder video = Json.createObjectBuilder();
                video.add("id", s);
                videos.add(video);
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
     * Lists all filenames present in the upload folder.
     *
     * @return an Iterable containing extension-less filenames.
     */
    private Iterable<String> getFilenames() throws IOException {
        System.out.println("Upload folder: " + Configuration.getInstance().getUploadFolder());
        File folder = new File(Configuration.getInstance().getUploadFolder());

        if (!folder.isDirectory()) {
            throw new IOException(Configuration.getInstance().getUploadFolder() + " is not a directory.");
        }

        File[] files = folder.listFiles();
        ArrayList<String> filenames = new ArrayList<>();

        for (File file : files) {
            filenames.add(FilenameUtils.getBaseName(file.getName()));
        }
        return filenames;
    }
}
