/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rocks.novateam.axis.sow.poc.backend.helpers;

/**
 *
 * @author Olivier Sailly
 */
public enum InstanceExistenceState {

    /**
     * The instance has both a resource and an AFP
     */
    EXISTS,

    /**
     * The instance has neither a resource nor an AFP
     */
    DOES_NOT_EXIST,

    /**
     * The instance exists but no AFP was found
     */
    NO_AFP_FOUND,

    /**
     * The instance was not found, but its AFP was found
     */
    NO_INSTANCE_FOUND;
}
