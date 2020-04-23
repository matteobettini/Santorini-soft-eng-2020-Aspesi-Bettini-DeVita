package it.polimi.ingsw.packets;

import java.awt.*;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class PacketPossibleMoves implements Serializable {

    private static final long serialVersionUID = 5416786792918029639L;
    private final String to;
    private final Map<String, Set<Point>> possibleMoves;

    public PacketPossibleMoves(String to, Map<String, Set<Point>> possibleMoves) {
        this.to = to;
        this.possibleMoves = possibleMoves;
    }

    public String getTo() {
        return to;
    }

    public Map<String, Set<Point>>  getPossibleMoves() {
        return possibleMoves;
    }
}
