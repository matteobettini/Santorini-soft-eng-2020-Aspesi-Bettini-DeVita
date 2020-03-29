package it.polimi.ingsw.model.turnInfo;


import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * 
 */
public class BuildData extends TurnData {


    /**
     * 
     */
    private final Map<Point, List<BuildingType>> data;

    /**
     * Default constructor
     */
    public BuildData(Player player, Worker worker, Map<Point,List<BuildingType>> data) {
        super( player, worker);
        this.data = data;
    }

    /**
     * @return
     */
    public Map<Point, List<BuildingType>> getData() {
        // TODO implement here
        return null;
    }

}