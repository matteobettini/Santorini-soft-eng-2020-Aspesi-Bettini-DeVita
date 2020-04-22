package it.polimi.ingsw.packets;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PacketWorkersPositions {

    private final Map<String, Point> workersPositions;

    public PacketWorkersPositions(Map<String, Point> workersPositions) {
        assert (workersPositions != null);
        this.workersPositions = new HashMap<>();
        for(String s : workersPositions.keySet()){
            assert(s != null && workersPositions.get(s) != null);
            this.workersPositions.put(s,new Point(workersPositions.get(s)));
        }
    }

    public Map<String, Point> getWorkersPositions() {
        return workersPositions;
    }
}
