package it.polimi.ingsw.packets;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PacketMove {
    private final String playerNickname;
    private final String workerID;
    private final List<Point> move;

    PacketMove(String playerNickname, String workerID, List<Point> move){
        this.playerNickname = playerNickname;
        this.workerID = workerID;
        this.move = new ArrayList<>();
        for(Point p : move){
            this.move.add(new Point(p));
        }
    }

    public String getPlayerNickname() { return this.playerNickname; }

    public String getWorkerID() { return this.workerID; }

    public List<Point> getMove() { return this.move; }
}
