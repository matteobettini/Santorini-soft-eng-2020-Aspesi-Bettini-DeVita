package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.cardReader.enums.TriggerType;

import java.awt.*;
import java.util.Map;
import java.util.Set;

public class PacketPossiblePoints {

    private final String to;
    private final Map<String, Set<Point>> possiblePoints;

    public PacketPossiblePoints(String to, Map<String, Set<Point>> possiblePoints) {
        this.to = to;
        this.possiblePoints = possiblePoints;
    }

    public String getTo() {
        return to;
    }

    public Map<String, Set<Point>>  getPossiblePoints() {
        return possiblePoints;
    }
}
