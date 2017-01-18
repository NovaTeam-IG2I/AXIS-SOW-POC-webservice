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
import java.util.Map;
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
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register/categories/*","/register/properties/*","/register/individuals/*","/register/add/*","/register/remove/instance/*"})
public class RegisterServlet extends HttpServlet {
    private final RegisterManager mRegisterManager;
    private final Gson mGson;
    private final TypeToken<ArrayList<Category>> mCategoriesListType;
    private final TypeToken<ArrayList<String>> mStringListType;
    private final TypeToken<Map<String,String>> mStringStringMapType;

    public RegisterServlet() {
        super();
        this.mRegisterManager = new RegisterManager();
        this.mGson = new Gson();
        this.mCategoriesListType = new TypeToken<ArrayList<Category>>(){};
        this.mStringListType = new TypeToken<ArrayList<String>>(){};
        this.mStringStringMapType = new TypeToken<Map<String,String>>(){};
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
            ArrayList<String> uriFields = this.getAllFieldsFromURI(request.getRequestURI());
            switch(uriFields.get(0).toLowerCase()){
                case "categories":
                    if(!request.getParameterMap().isEmpty()) out.print(this.processGetCategories(request.getParameter("classuri")));
                    else out.print(this.processGetCategories());
                break;
                case "individuals":
                    out.print(this.processGetAllIndividuals());
                break;
                case "properties":
                    if(!request.getParameterMap().isEmpty()) out.print(this.processGetProperties(request.getParameter("classuri")));
                    else {
                        if(uriFields.size() > 1) out.print(this.processGetPropertiesByName(uriFields.get(1)));
                        else response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                break;
                case "add":
                    if(uriFields.size() > 1) {
                        switch(uriFields.get(1).toLowerCase()){
                            case "instance":
                                if(!request.getParameterMap().isEmpty()){
                                    try {
                                        boolean result = this.processAddRegisterInstance(request.getParameter("name"),request.getParameter("classuri"),request.getParameter("properties"));
                                        if(result){
                                            response.sendError(HttpServletResponse.SC_CREATED);
                                        } else {
                                            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                        }
                                    } catch (Exception err) {
                                        response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                    }
                                } else {
                                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                                }
                            break;
                            case "predicate":
                                if(!request.getParameterMap().isEmpty()){
                                    try {
                                        boolean result = this.processAddPredicate(request.getParameter("name"));
                                        if(result){
                                            response.sendError(HttpServletResponse.SC_CREATED);
                                        } else {
                                            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                        }
                                    } catch (Exception err) {
                                        response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                    }
                                } else {
                                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                                }
                            break;
                            case "existingpredicatetoregister":
                                if(!request.getParameterMap().isEmpty()){
                                    try {
                                        boolean result = this.processAddPredicateToRegisters(request.getParameter("subjecturi"),request.getParameter("objecturi"),request.getParameter("predicateuri"));
                                        if(result){
                                            response.sendError(HttpServletResponse.SC_CREATED);
                                        } else {
                                            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                        }
                                    } catch (Exception err) {
                                        response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                    }
                                } else {
                                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                                }
                            break;
                            default:
                                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                            break;
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                break;
                case "remove":
                    if(uriFields.size() > 1) {
                        switch(uriFields.get(1).toLowerCase()){
                            case "instance":
                                if(!request.getParameterMap().isEmpty()){
                                    try {
                                        boolean result = this.processDeleteInstance(request.getParameter("uri"));
                                        if(result){
                                            response.sendError(HttpServletResponse.SC_OK);
                                        } else {
                                            response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                        }
                                    } catch (Exception err) {
                                        response.sendError(HttpServletResponse.SC_EXPECTATION_FAILED);
                                    }
                                } else {
                                    response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                                }
                            break;
                            default:
                                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                            break;
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
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

    private String processGetCategories(String classURI){
        return this.mGson.toJson(this.mRegisterManager.getCategoriesRecusively(classURI), mCategoriesListType.getType());
    }
    
    private String processGetProperties(String classURI){
        return this.mGson.toJson(this.mRegisterManager.getClassPropertiesLocalNames(classURI), mStringListType.getType());
    }

    private String processGetPropertiesByName(String className){
        return this.mGson.toJson(this.mRegisterManager.getClassPropertiesLocalNamesByClassName(className), mStringListType.getType());
    }

    private String processGetAllIndividuals(){
        return this.mGson.toJson(this.mRegisterManager.getAllIndividualsLocalNames(), mStringListType.getType());
    }

    private boolean processAddRegisterInstance(String name, String classURI, String jsonProperties){
        Map<String,String> mProperties = this.mGson.fromJson(jsonProperties, this.mStringStringMapType.getType());
        return this.mRegisterManager.addRegisterInstance(name, classURI, mProperties);
    }

    private boolean processAddPredicate(String name){
        return this.mRegisterManager.addPredicate(name);
    }

    private boolean processAddPredicateToRegisters(String subjectURI, String objectURI, String predicateURI){
        return this.mRegisterManager.addPredicateToRegisters(subjectURI, objectURI, predicateURI);
    }

    private boolean processDeleteInstance(String uri){
        return this.mRegisterManager.deleteInstance(uri);
    }

    private String calledMethodFromURI(String uri){
        String newS = uri.replace("/AXIS-SOW-POC-backend/register/", "");
        return (newS.contains("/")?newS.substring(0, newS.indexOf("/")):newS);
    }
    
    private ArrayList<String> getAllFieldsFromURI(String uri){
        ArrayList<String> als = new ArrayList<>();
        String s = uri.replace("/AXIS-SOW-POC-backend/register/", "");
        if(!s.contains("/")) {
            als.add(s);
        } else {
            als.addAll(Arrays.asList(s.split("/")));
        }
        return als;
    }
    
    private ArrayList<String> getAllFieldsFromURI(String uri, String calledMethod){
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
