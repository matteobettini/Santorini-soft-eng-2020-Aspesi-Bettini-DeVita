package it.polimi.ingsw.model;

import it.polimi.ingsw.cardReader.CardFile;
import it.polimi.ingsw.packets.PacketBuild;
import jdk.jfr.EventType;

import java.awt.*;
import java.util.List;
import java.util.Map;

public interface Model{

    public void tryMove(PacketBuild m);

    public void tryBuild(PacketBuild m);

    public java.util.List<Point> getAllowedMoves(String workerID);

    public java.util.List<EventType> getPossibleWorkerActions(String workerID);

    public Board getClonedBoard();

    public java.util.List<Player> getClonedPlayers();

    public void setGameCards(CardFile defaultStrategy, Map<String, CardFile> playerCardAssociation);

    public boolean containsPlayer(String nickname);

    public void setLoser(String nickname);

    public java.util.List<Player> getWinners();

    public List<Player> getLosers();

}