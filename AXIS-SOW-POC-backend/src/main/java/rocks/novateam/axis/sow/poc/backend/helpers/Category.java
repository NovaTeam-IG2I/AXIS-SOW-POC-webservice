/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.helpers;

import java.util.ArrayList;

/**
 *
 * @author Mélody
 */
public class Category {
    public String label;
    public ArrayList<Category> subClass;

    public Category() {
    }

    public Category(String label, ArrayList<Category> subClass) {
        this.label = label;
        this.subClass = subClass;
    }

    
    
    public Category(String label) {
        this.label = label;
    }

    public Category(ArrayList<Category> subClass) {
        this.subClass = subClass;
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<Category> getSubClass() {
        return subClass;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSubClass(ArrayList<Category> subClass) {
        this.subClass = subClass;
    }
    
    public void addSubClass(String label){
        Category cat = new Category(label);
        this.subClass.add(cat);
    }

    @Override
    public String toString() {
        return "Category{" + "label=" + label + ", subClass=" + subClass + '}';
    }
    
    
    
}
