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

    private final List<Point> dataOrder;



    /**
     * Default constructor
     */
    public BuildData(Player player, Worker worker, Map<Point,List<BuildingType>> data, List<Point> dataOrder ) {
        super( player, worker);
        this.data = data;
        this.dataOrder = dataOrder;

    }


    /**
     * @return
     */
    public List<Point> getDataOrder() {
        return dataOrder;
    }

    /**
     * @return
     */
    public Map<Point, List<BuildingType>> getData() {
        return data;
    }

}