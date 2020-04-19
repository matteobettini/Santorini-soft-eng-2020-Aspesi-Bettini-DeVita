package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.enums.BuildingType;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PacketPossibleBuilds {

    private final String to;
    private final Map<String, Map<Point, List<BuildingType>>> possibleBuilds;

    public PacketPossibleBuilds(String to, Map<String, Map<Point, List<BuildingType>>> possibleBuilds) {
        this.to = to;
        this.possibleBuilds = possibleBuilds;
    }

    public String getTo() {
        return to;
    }

    public Map<String, Map<Point, List<BuildingType>>> getPossibleBuilds() {
        return possibleBuilds;
    }
}
