package it.polimi.ingsw.model;

import java.awt.*;

/**
 * This class contains all the info about a Worker. In particular its ID and the Player associated to it.
 * Furthermore it contains the info about the current position of the Worker on the Board.
 */
public abstract class Worker {

    /**
     * Getter that returns the ID of the Worker.
     * @return a String containing the id of the Worker.
     */
    public abstract String getID();

    /**
     * Setter that sets the position of the Worker to the given position.
     * @param cell is an instance of Cell that contains the info about the position to set.
     */
    public abstract void setPosition(Point cell);

    /**
     * Getter that returns the position of the Worker.
     * @return a Point with the coordinates of the position.
     */
    public abstract Point getPosition();

    /**
     * Getter that returns the Player associated to the Worker.
     * @return an instance of Player.
     */
    public abstract Player getPlayer();

    /**
     * This method checks if the given obj equals the Worker.
     * @param obj is an instance of Object to check.
     * @return true if obj and the Worker are identical, false otherwise.
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * This method returns a clone of the Worker.
     * @return an cloned instance of the Worker.
     */
    @Override
    protected abstract Worker clone();

}
