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
        this.label = "";
        this.subClass = new ArrayList<>();
    }
    
    public Category(String label) {
        this();
        this.label = label;
    }

    public Category(ArrayList<Category> subClass) {
        this();
        this.subClass = subClass;
    }

    public Category(String label, ArrayList<Category> subClass) {
        this.label = label;
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
    
    public void addSubClass(Category c){
        this.subClass.add(c);
    }

    public void addSubClasses(ArrayList<Category> alc){
        this.subClass.addAll(alc);
    }

    @Override
    public String toString() {
        return "Category{" + "label=" + label + ", subClass=" + subClass + '}';
    }
    
    public String toTree() {
        String tree = "";
        if (this.subClass.isEmpty()) {
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

        if (c.subClass != null && !c.subClass.isEmpty()){
            for (Category subCat : c.getSubClass()) {
                s = recursiveTree(s, subCat, (1 + level));
            }
        }
        return s;
    }
}
