package it.polimi.ingsw.model.turnInfo;


import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;

import java.awt.*;
import java.util.List;

/**
 * 
 */
public class MoveData extends TurnData {


    /**
     * 
     */
    private final List<Point> data;


    /**
     * Default constructor
     */
    public MoveData(Player player, Worker worker, List<Point> data) {
        super(player,worker);
        this.data = data;
    }



    /**
     * @return
     */
    public List<Point> getData() {
       return data;
    }

}