package it.polimi.ingsw.model;

import java.awt.*;

/**
 * This class contains all the info about a Worker. In particular its ID and the Player associated to it.
 * Furthermore it contains the info about the current position of the Worker on the Board.
 */
public class Worker {

    private final String ID;
    private Player player;
    private Point position;


    Worker(String ID, Player p){
        this.ID = ID;
        this.player = p;
    }

    /**
     * Getter that returns the ID of the Worker.
     * @return a String containing the id of the Worker.
     */
    public  String getID(){ return this.ID; }

    /**
     * Setter that sets the position of the Worker to the given position.
     * @param cell is an instance of Cell that contains the info about the position to set.
     */
    public void setPosition(Point cell){
        if(cell == null) this.position = null;
        else if(cell.x >= 0 && cell.x < 5 && cell.y >= 0 && cell.y < 5){
            this.position = cell;
        }
        else assert false;
    }

    /**
     * Getter that returns the position of the Worker.
     * @return a Point with the coordinates of the position.
     */
    public Point getPosition(){ return this.position; }

    /**
     * Getter that returns the Player associated to the Worker.
     * @return an instance of Player.
     */
    public Player getPlayer(){ return this.player; }

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

}
