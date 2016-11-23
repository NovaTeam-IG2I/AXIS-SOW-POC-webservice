package rocks.novateam.axis.sow.poc.backend.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Paths;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;

/**
 * This servlet handles media importation.
 * 
 * The HTTP request @b must
 *     + Have a <code>multipart/form-data</code> MIME type ;
 *     + Contain a field named <code>file</code> containing the file to import.
 * 
 * The HTTP response will have a <code>application/json</code> MIME type, and may contain:
 *     + <code>{'status': 'ok', 'filename': <em>filename</em>}</code> if the request succeeded ;
 *     + <code>{'status': 'ko', 'message': <em>message</em>}</code> if the request failed.
 * 
 * @author Richard Degenne
 */
@MultipartConfig(location="E:\\Users\\richou\\Desktop") // This folder is for temporary storage ONLY.
public class importServlet extends HttpServlet {

    /**
     * Target folder for the uploaded files.
     */
    public static String UPLOAD_FOLDER = "E:\\Users\\richou\\Desktop\\";

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
        try {
            // Get data from the request object
            Part filePart = request.getPart("file");
            if(filePart == null) {
                throw new FileNotFoundException("No 'file' part given in the request.");
            }
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // This is an IE fix.
            InputStream fileContent = filePart.getInputStream();

            // Copy media to disk
            File file = new File(UPLOAD_FOLDER+fileName);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file, false); // TODO: Have an incremental id for file names.
            IOUtils.copy(fileContent, fileOutputStream);
            
            json.add("status", "ok")
                    .add("filename", fileName);
        }
        catch(FileNotFoundException e) {
            json.add("status", "ko")
                    .add("message", e.getMessage());
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
        return "This servlet handles media importation.";
    }// </editor-fold>

}
