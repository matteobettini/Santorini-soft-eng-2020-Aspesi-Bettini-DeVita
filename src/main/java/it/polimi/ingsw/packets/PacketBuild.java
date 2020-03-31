package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the info about a build action performed by a Player.
 */
public class PacketBuild {
    /**
     * This is the nickname of the Player who performed the action.
     */
    private final String playerNickname;
    /**
     * This is the ID of Worker that is used by the Player in his action.
     */
    private final String workerID;
    /**
     * This is a map that associates the Point where the Player has built to a List of BuildingType that he used on that Point.
     */
    private final Map<Point, List<BuildingType>> builds;

    PacketBuild(String playerNickname, String workerID, Map<Point, List<BuildingType>> builds){
        this.playerNickname = playerNickname;
        this.workerID = workerID;
        this.builds = new HashMap<>();
        for(Point pos : builds.keySet()){
            this.builds.put(new Point(pos), new ArrayList<>(builds.get(pos)));
        }
    }

    /**
     *
     * @return
     */
    public String getPlayerNickname() { return this.playerNickname; }

    /**
     *
     * @return
     */
    public String getWorkerID() { return this.workerID; }

    /**
     *
     * @return
     */
    public Map<Point, List<BuildingType>> getBuilds() { return this.builds; }
}
