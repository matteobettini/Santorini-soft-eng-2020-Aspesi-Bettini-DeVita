package it.polimi.ingsw.server.model;


import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Contains a purpose of build to be validated by the model and eventually applied to the game.
 * Contains info about build order, build points and list of buildings for each point
 */
class BuildData extends TurnData {

    private final Map<Point, List<BuildingType>> data;
    private final List<Point> dataOrder;

    BuildData(Player player, Worker worker, Map<Point,List<BuildingType>> data, List<Point> dataOrder ) {
        super( player, worker);
        assert (data != null && dataOrder != null);
        this.data = data;
        this.dataOrder = dataOrder;
    }

    /**
     * Gets build points in order. First point is where the first building was placed.
     * @return List of building points.
     */
    public List<Point> getDataOrder() {
        return dataOrder;
    }

    /**
     * Gets build data associated to each point. For the point order, refer to getDataOrder()
     * @return Data of each point
     */
    public Map<Point, List<BuildingType>> getData() {
        return data;
    }

}