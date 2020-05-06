package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.enums.BuildingType;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketPossibleBuilds implements Serializable {

    private static final long serialVersionUID = 6144421728412629857L;
    private final String to;
    private final Map<String, Map<Point, List<BuildingType>>> possibleBuilds;

    public PacketPossibleBuilds(String to, Map<String, Map<Point, List<BuildingType>>> possibleBuilds) {
        this.to = to;
        this.possibleBuilds = new HashMap<>();
        for(String s : possibleBuilds.keySet()){
            Map<Point, List<BuildingType>> internalMap = new HashMap<>();
            this.possibleBuilds.put(s,internalMap);
            for(Point p : possibleBuilds.get(s).keySet()){
                List<BuildingType> internalList = new ArrayList<>(possibleBuilds.get(s).get(p));
                internalMap.put(p,internalList);
            }
        }
    }

    public String getTo() {
        return to;
    }

    public Map<String, Map<Point, List<BuildingType>>> getPossibleBuilds() {
        return possibleBuilds;
    }
}
