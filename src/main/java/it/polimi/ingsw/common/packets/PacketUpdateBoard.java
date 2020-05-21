package it.polimi.ingsw.common.packets;

import it.polimi.ingsw.common.enums.BuildingType;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketUpdateBoard implements Serializable {

    private static final long serialVersionUID = 1431886725229125334L;
    private final Map<String, Point> workersPositions;
    private final Map<Point, List<BuildingType>> newBuildings;
    private final String playerLostID;
    private final String playerWonID;

    public PacketUpdateBoard(Map<String, Point> workersPositions, Map<Point, List<BuildingType>> newBuildings, String playerLostID, String playerWonID) {

        if(workersPositions != null) {
            this.workersPositions = new HashMap<>();
            for (String s : workersPositions.keySet()) {
                this.workersPositions.put(s, workersPositions.get(s));
            }
        }else{
            this.workersPositions = null;
        }


        if(newBuildings != null) {
            this.newBuildings = new HashMap<>();
            for (Point p : newBuildings.keySet()) {
                List<BuildingType> internalList = new ArrayList<>(newBuildings.get(p));
                this.newBuildings.put(p, internalList);
            }
        }else {
            this.newBuildings = null;
        }


        this.playerLostID = playerLostID;
        this.playerWonID = playerWonID;
    }

    public PacketUpdateBoard(String playerID, boolean winner){
        this.workersPositions = null;
        this.newBuildings = null;
        if(winner){
            this.playerWonID = playerID;
            this.playerLostID = null;
        }
        else{
            this.playerLostID = playerID;
            this.playerWonID = null;
        }
    }

    public PacketUpdateBoard(Map<String, Point> workersPositions, String playerWonID){
        this(workersPositions, null, null, playerWonID);
    }

    public PacketUpdateBoard(Map<String, Point> workersPositions){
        this(workersPositions, null, null, null);
    }

    public PacketUpdateBoard(String playerWonID, Map<Point, List<BuildingType>> newBuildings){
        this(null, newBuildings, null, playerWonID);
    }

    public Map<String, Point> getWorkersPositions() {
        return workersPositions;
    }

    public Map<Point, List<BuildingType>> getNewBuildings() {
        return newBuildings;
    }

    public String getPlayerLostID() {
        return playerLostID;
    }

    public String getPlayerWonID() {
        return playerWonID;
    }
}
