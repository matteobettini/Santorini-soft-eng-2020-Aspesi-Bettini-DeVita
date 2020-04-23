package it.polimi.ingsw.model.turnInfo;


import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;

import java.awt.*;
import java.util.List;

/**
 *  Contains a purpose of move to be validated by the model and eventually applied to the game.
 *  Contains info points where the player wants to move with a worker, ordered.
 */
public class MoveData extends TurnData {
    private final List<Point> data;

    public MoveData(Player player, Worker worker, List<Point> data) {
        super(player,worker);
        assert (data != null);
        this.data = data;
    }

    /**
     * Gets the points data contained in this packet
     * @return List of points
     */
    public List<Point> getData() {
       return data;
    }

}