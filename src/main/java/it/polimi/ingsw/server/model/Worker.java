package it.polimi.ingsw.server.model;

import java.awt.*;
import java.util.Objects;

/**
 * This class contains all the info about a Worker. In particular its ID and the Player associated to it.
 * Furthermore it contains the info about the current position of the Worker on the Board.
 */
class Worker {

    /**
     * This is the ID of the Worker which is the concatenation of the Player nickname + "." + the number starting from 1.
     */
    private final String ID;
    private final String playerID;
    private Point position;


    Worker(String ID, String playerID){
        this.ID = ID;
        this.playerID = playerID;
        this.position = null;
    }

    /**
     * Getter that returns the ID of the Worker.
     * @return a String containing the id of the Worker.
     */
    public String getID(){ return this.ID; }


    /**
     * set worker's position to null
     */
    public void removeFromBoard(){
        this.position = null;
    }

    /**
     * Setter that sets the position of the Worker to the given position.
     * @param position is an instance of position that contains the info about the position to set.
     */
    public void setPosition(Point position){
        assert position != null;
        if(position.x >= 0 && position.x < Board.ROWS && position.y >= 0 && position.y < Board.COLUMNS)
            this.position = new Point(position);
    }

    /**
     * Getter that returns the position of the Worker.
     * @return a Point with the coordinates of the position.
     */
    public Point getPosition(){ return (this.position != null ? new Point(this.position) : null); }

    /**
     * Getter that returns the Player (ID) associated to the Worker.
     * @return ID of the player.
     */
    public String getPlayerID(){ return this.playerID; }

    /**
     * This method checks if the given obj equals the Worker.
     * @param obj is an instance of Object to check.
     * @return true if obj and the Worker are identical, false otherwise.
     */
    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        Worker other = (Worker)obj;
        return this.ID.equals(other.ID);
    }

    /**
     * This method returns a clone of the Worker.
     * @return an cloned instance of the Worker.
     */
    @Override
    protected Worker clone(){
        Worker w = new Worker(this.ID, this.playerID);
        if (this.position != null)
            w.setPosition(this.position);
        return w;
    }

    /**
     * Get the hash code for this worker
     * @return Hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.ID, this.playerID);
    }
}
