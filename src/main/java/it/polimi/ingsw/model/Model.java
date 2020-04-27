package it.polimi.ingsw.model;

import it.polimi.ingsw.packets.InvalidPacketException;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Interface that contains the methods exposed by the Model.
 */
public interface Model{

    void makeMove(String senderID, PacketMove packetMove) throws InvalidPacketException;

    void makeBuild(String senderID, PacketBuild packetBuild) throws InvalidPacketException;

    void getPossibeMoves(String senderID, PacketMove packetMove);

    void getPossibleBuilds(String senderID, PacketBuild packetBuild);

    void setSelectedCards(String senderID, List<String> selectedCards) throws InvalidPacketException;

    void setStartPlayer(String senderID, String startPlayer) throws InvalidPacketException;

    void setWorkersPositions(String senderID, Map<String,Point> workersPositions) throws InvalidPacketException;





}