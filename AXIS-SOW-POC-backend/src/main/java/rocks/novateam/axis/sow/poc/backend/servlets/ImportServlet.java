package rocks.novateam.axis.sow.poc.backend.servlets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.ModelFactory;
import rocks.novateam.axis.sow.poc.backend.Configuration;
import rocks.novateam.axis.sow.poc.backend.ontology.TDBManager;

/**
 * This servlet handles media importation.
 *
 * Uploaded files will be stored in the folder given in the `configuration.yml`
 * file.
 *
 * The HTTP request @b must
 * <ul>
 * <li>Have a <code>multipart/form-data</code> MIME type;</li>
 * <li>Contain a field named <code>file</code> containing the file to
 * import.</li>
 * </ul>
 *
 * It can also have an optional <code>title</code> attribute to name the
 * semantic entity. If <code>title</code> is not provided, the uploaded file
 * name will be used instead.
 *
 * The HTTP response will have a <code>application/json</code> MIME type, and
 * may contain:
 * <ul>
 * <li><code>{'status': 'ok', 'uri': <em>uri</em>}</code> if the
 * request succeeded, where <code>uri</code> is the Film register's URI;</li>
 * <li><code>{'status': 'ko', 'message': <em>message</em>}</code> if the request
 * failed.</li>
 * </ul>
 *
 * @see Configuration
 *
 * @author Richard Degenne
 */
@MultipartConfig
public class ImportServlet extends HttpServlet {

    /**
     * File extension for uploaded files.
     *
     * AXIS-SOW-POC will only work with MP4 files for the time being.
     */
    public static String FILE_EXTENSION = ".mp4";

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
            String title = request.getParameter("title");
            if (filePart == null) {
                throw new FileNotFoundException("No 'file' part given in the request.");
            }
            String fileName = getNextFileName();
            InputStream fileContent = filePart.getInputStream();

            // Copy media to disk
            File file = new File(Configuration.getInstance().getUploadFolder() + fileName);
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file, false); // TODO: Have an incremental id for file names.
            IOUtils.copy(fileContent, fileOutputStream);

            // Create semantic entites in the TDB
            String urn = getUniqueName((title == null) ? fileName : title);
            Individual film = persist(urn, file.getAbsolutePath());

            json.add("status", "ok")
                    .add("uri", film.getURI());
        } catch (FileNotFoundException e) {
            json.add("status", "ko")
                    .add("message", e.getMessage());
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
        return "This servlet handles media importation.";
    }// </editor-fold>

    /**
     * Gets the best filename for an uploaded file.
     *
     * Iterates over the upload folder to find the first integer number not
     * already present in the folder.
     *
     * @return The best filename to give to a newly uplaoded file.
     *
     * @see Configuration#uploadFolder()
     */
    private String getNextFileName() {
        System.out.println("Upload folder: " + Configuration.getInstance().getUploadFolder());
        File folder = new File(Configuration.getInstance().getUploadFolder());
        File[] files = folder.listFiles();
        int fileNumber = 1; // File names start from 1.

        for (int i = 0; i < files.length; ++i, ++fileNumber) {
            System.out.println("Comparing " + files[i] + " to " + fileNumber + FILE_EXTENSION);
            if (files[i].compareTo(new File(Configuration.getInstance().getUploadFolder() + fileNumber + FILE_EXTENSION)) != 0) {
                break;
            }
        }
        return fileNumber + FILE_EXTENSION; // WARNING: Hardcoded file extension here.
    }
    
    /**
     * Creates all the necessary AXIS-CSRM individuals in the triple store.
     * 
     * This method creates the following individuals:
     * <ul>
     * <li>A <code>Film</code>;</li>
     * <li>An <code>AFP</code> (AXIS FootPrint) for the Film;</li>
     * <li>A <code>VideoDocument</code> for the Film;</li>
     * <li>A <code>VideoEmbodiment</code> for the Document;</li>
     * <li>A <code>Location</code> for the Embodiment.</li>
     * 
     * The absolute path to the video file is stored as an <code>hyperlink</code> property on the Location individual.
     * 
     * @param name     The URN to give to the new entities
     * @param filePath The absolute path to the video file
     * 
     * @return         The Film register {@link Individual}
     */
    private Individual persist(String name, String filePath) {
        Dataset dataset = TDBManager.getInstance().getDataset();
        String NS = TDBManager.DATAMODEL_NS;

        dataset.begin(ReadWrite.WRITE);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());

        Individual film = model.getOntClass(NS + "Film").createIndividual(NS + name);
        Individual afp = model.getOntClass(NS + "AFP").createIndividual(NS + name + "_AFP");
        film.addProperty(model.getProperty(NS + "isDeclaredBy"), afp);
        Individual document = model.getOntClass(NS + "VideoDocument").createIndividual(NS + name + "_Document");
        document.addLiteral(model.getDatatypeProperty("http://www.w3.org/ns/ma-ont#title"), name);
        film.addProperty(model.getProperty(NS + "hasExpression"), document);
        Individual embodiment = model.getOntClass(NS + "VideoEmbodiment").createIndividual(NS + name + "_Embodiment");
        document.addProperty(model.getProperty(NS + "hasManifestation"), embodiment);
        Individual location = model.getOntClass(NS + "Location").createIndividual(NS + name + "_Location");
        location.addProperty(model.getDatatypeProperty(NS + "hyperlink"), filePath);
        embodiment.addProperty(model.getProperty(NS + "hasLocation"), location);
        dataset.commit();
        
        return film;
    }

    /**
     * Finds a unique URN in the model for the given name.
     *
     * This method will look up the given <code>name</code> in the triple store
     * and finds a suitable URN to avoid duplicates by adding a numerical suffix
     * if needed.
     *
     * @param name The wanted URN
     * @return A unique URN that doesn't already exist in the triple store
     */
    private String getUniqueName(String name) {
        Dataset dataset = TDBManager.getInstance().getDataset();
        String NS = TDBManager.DATAMODEL_NS;

        dataset.begin(ReadWrite.READ);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());
        dataset.end();

        // If name is already unique, return it as is.
        Individual film = model.getIndividual(NS + name);
        if (film == null) {
            return name;
        }

        // Else, increment a numerical suffix until it becomes unique.
        int i = 1;
        while (film != null) {
            film = model.getIndividual(NS + name + i);
            i++;
        }
        return name + i;
    }
}
