/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.tools;

import org.apache.commons.lang3.text.WordUtils;

/**
 * A basic class created to help with CamelCase conversion
 *
 * @author Olivier Sailly
 */
public class CamelCaseConverter {

    /**
     * Adds spaces to a CamelCase string 
     *
     * @param camelCaseString The string that will be converted
     * @return A string containing multiple words separated by spaces
     */
    public static String addSpacesToCamelCase(String camelCaseString) {
        String spaceAddedString = "";
        spaceAddedString += camelCaseString.charAt(0);
        for (int i = 1; i < camelCaseString.length(); i++) {
            if (Character.isUpperCase(camelCaseString.charAt(i))) {
                spaceAddedString += " ";
            }
            spaceAddedString += camelCaseString.charAt(i);
        }
        return spaceAddedString;
    }

    /**
     * Convert a common string to CamelCase
     *
     * @param nonCamelCaseString The string that will be converted
     * @return A camelCase string
     */
    public static String convertToCamelCase(String nonCamelCaseString) {
        return Character.toLowerCase(nonCamelCaseString.charAt(0)) + WordUtils.capitalizeFully(nonCamelCaseString).replaceAll(" ", "").substring(1);
    }

    /**
     * A basic main to test methods
     *
     * @param args
     */
    public static void main(String[] args) {
        String camelCase = "camelCaseText";
        String nonCamelCase = "NON camel Case";
        System.out.println("addSpacesToCamelCase(\"" + camelCase + "\") : \"" + addSpacesToCamelCase(camelCase) + "\"");
        System.out.println("convertToCamelCase(\"" + nonCamelCase + "\") : \"" + convertToCamelCase(nonCamelCase) + "\"");
    }
}
