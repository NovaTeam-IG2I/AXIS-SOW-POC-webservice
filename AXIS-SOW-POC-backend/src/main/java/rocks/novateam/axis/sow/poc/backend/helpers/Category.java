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
    private String label;
    private ArrayList<Category> subCategories;

    public Category() {
        this.label = "";
        this.subCategories = new ArrayList<>();
    }
    
    public Category(String label) {
        this();
        this.label = label;
    }

    public Category(ArrayList<Category> subClass) {
        this();
        this.subCategories = subClass;
    }

    public Category(String label, ArrayList<Category> subClass) {
        this.label = label;
        this.subCategories = subClass;
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<Category> getSubCategories() {
        return subCategories;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setSubCategories(ArrayList<Category> subCategories) {
        this.subCategories = subCategories;
    }
    
    public void addSubCategory(String label){
        Category cat = new Category(label);
        this.subCategories.add(cat);
    }
    
    public void addSubCategory(Category c){
        this.subCategories.add(c);
    }

    public void addSubCategories(ArrayList<Category> alc){
        this.subCategories.addAll(alc);
    }

    @Override
    public String toString() {
        return "Category{" + "label=" + label + ", subClass=" + subCategories + '}';
    }
    
    public String toTree() {
        String tree = "";
        if (this.subCategories.isEmpty()) {
            return this.label;
        } else {
            return recursiveTree(tree, this, 0);
        }
    }
    
    private String recursiveTree(String s, Category c, int level){
        if(level>0) {
            s += "|";
            for(int i = 0; i < level; i++) s+= "\t";
            s += "- ";
        }
        s += c.label + "\n";

        if (c.subCategories != null && !c.subCategories.isEmpty()){
            for (Category subCat : c.getSubCategories()) {
                s = recursiveTree(s, subCat, (1 + level));
            }
        }
        return s;
    }
}
