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
    private Gson mGson;
    private TypeToken<ArrayList<Category>> mCategoriesListType;

    public registerServlet() {
        super();
        this.mRegisterManager = new RegisterManager();
        this.mGson = new Gson();
        this.mCategoriesListType = new TypeToken<ArrayList<Category>>(){};
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
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
            }
        }
    }

    private String processGetCategories(){
        return this.mGson.toJson(this.mRegisterManager.getRegisterCategories(), mCategoriesListType.getType());
    }

    private String calledMethod(String s){
        String newS = s.replace("/AXIS-SOW-POC-backend/register/", "");
        return (newS.contains("/")?newS.substring(0, newS.indexOf("/")):newS);
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
