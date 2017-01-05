/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.servlets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import rocks.novateam.axis.sow.poc.backend.helpers.Category;
import rocks.novateam.axis.sow.poc.backend.ontology.RegisterManager;

/**
 *
 * @author Olivier Sailly
 */
@WebServlet(name = "registerServlet", urlPatterns = {"/register/categories/*","/register/properties/*"})
public class registerServlet extends HttpServlet {
    private final RegisterManager mRegisterManager;
    private final Gson mGson;
    private final TypeToken<ArrayList<Category>> mCategoriesListType;
    private final TypeToken<ArrayList<String>> mStringListType;

    public registerServlet() {
        super();
        this.mRegisterManager = new RegisterManager();
        this.mGson = new Gson();
        this.mCategoriesListType = new TypeToken<ArrayList<Category>>(){};
        this.mStringListType = new TypeToken<ArrayList<String>>(){};
    }

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
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            switch(this.calledMethod(request.getRequestURI())){
                case "categories":
                    out.print(this.processGetCategories());
                break;
                case "properties":
                    out.print(this.processGetProperties(this.getAllFields(request.getRequestURI(), "properties").get(0)));
                break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
            }
        }
    }

    private String processGetCategories(){
        return this.mGson.toJson(this.mRegisterManager.getRegisterCategories(), mCategoriesListType.getType());
    }

    private String processGetProperties(String className){
        return this.mGson.toJson(this.mRegisterManager.getProperties(className), mStringListType.getType());
    }

    private String calledMethod(String uri){
        String newS = uri.replace("/AXIS-SOW-POC-backend/register/", "");
        return (newS.contains("/")?newS.substring(0, newS.indexOf("/")):newS);
    }
    
    private ArrayList<String> getAllFields(String uri){
        ArrayList<String> als = new ArrayList<>();
        String s = uri.replace("/AXIS-SOW-POC-backend/register/", "");
        if(!s.contains("/")) {
            als.add(s);
        } else {
            als.addAll(Arrays.asList(s.split("/")));
        }
        return als;
    }
    
    private ArrayList<String> getAllFields(String uri, String calledMethod){
        ArrayList<String> als = new ArrayList<>();
        String s = uri.replace("/AXIS-SOW-POC-backend/register/"+calledMethod+"/", "");
        if(!s.contains("/")) {
            als.add(s);
        } else {
            als.addAll(Arrays.asList(s.split("/")));
        }
        return als;
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

}
