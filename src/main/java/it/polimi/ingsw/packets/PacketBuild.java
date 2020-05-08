package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class contains the info about a build action performed by a Player.
 */
public class PacketBuild implements Serializable {
    private static final long serialVersionUID = -6803132829637177106L;
    /**
     * This is the nickname of the Player who performed the action.
     */
    private final String playerNickname;
    /**
     * This is the ID of Worker that is used by the Player in his action.
     */
    private final String workerID;

    private final boolean simulate;
    /**
     * This is a map that associates the Point where the Player has built to a List of BuildingType that he used on that Point.
     */
    private final Map<Point, List<BuildingType>> builds;

    private final List<Point> dataOrder;

    public PacketBuild(String playerNickname, String workerID, boolean simulate, Map<Point, List<BuildingType>> builds, List<Point> dataOrder){
        assert(playerNickname != null && builds != null && dataOrder != null);
        this.simulate = simulate;
        this.playerNickname = playerNickname;
        this.workerID = workerID;
        this.builds = new HashMap<>();
        for(Point pos : builds.keySet()) {
            assert (pos != null && builds.get(pos) != null);
            this.builds.put(new Point(pos), new ArrayList<>(builds.get(pos)));
        }

        this.dataOrder = new ArrayList<>();
        for(Point p : dataOrder) {
            assert (p != null);
            this.dataOrder.add(new Point(p));
        }

    }

    public PacketBuild(String playerNickname){
        this(playerNickname,null, true, new HashMap<>(), new ArrayList<>());
    }

    public PacketBuild(String playerNickname, String workerID, Map<Point, List<BuildingType>> builds, List<Point> dataOrder){
        this(playerNickname,workerID,true,builds,dataOrder);
    }

    public boolean isSimulate() {
        return simulate;
    }

    public List<Point> getDataOrder() {
        return dataOrder;
    }

    /**
     * Getter that returns the nickname of the Player.
     * @return the String containing the Player nickname.
     */
    public String getPlayerNickname() { return this.playerNickname; }

    /**
     * Getter that returns the ID of the Worker.
     * @return the String containing the ID of the Worker.
     */
    public String getWorkerID() { return this.workerID; }

    /**
     * Getter that returns the Points where the Player has built and which buildings.
     * @return a Map that associates the Points to the added buildings.
     */
    public Map<Point, List<BuildingType>> getBuilds() { return this.builds; }
}
