package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.enums.BuildingType;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketBuild {
    private final String playerNickname;
    private final String workerID;
    private final Map<Point, List<BuildingType>> builds;

    PacketBuild(String playerNickname, String workerID, Map<Point, List<BuildingType>> builds){
        this.playerNickname = playerNickname;
        this.workerID = workerID;
        this.builds = new HashMap<>();
        for(Point pos : builds.keySet()){
            this.builds.put(new Point(pos), new ArrayList<>(builds.get(pos)));
        }
    }

    public String getPlayerNickname() { return this.playerNickname; }

    public String getWorkerID() { return this.workerID; }

    public Map<Point, List<BuildingType>> getBuilds() { return this.builds; }
}
