package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.packets.InvalidPacketException;
import it.polimi.ingsw.common.packets.PacketBuild;
import it.polimi.ingsw.common.packets.PacketMove;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Interface that contains the methods exposed by the Model.
 */
public interface Model{

    /**
     * Used by a client to propose a move action
     * @param senderID the requesting client
     * @param packetMove the proposed move
     * @throws InvalidPacketException thrown if the proposing client is the one I am waiting, but the move is invalid or against the rules
     */
    void makeMove(String senderID, PacketMove packetMove) throws InvalidPacketException;
    /**
     * Used by a client to propose a build action
     * @param senderID the requesting client
     * @param packetBuild the proposed build
     * @throws InvalidPacketException thrown if the proposing client is the one I am waiting, but the build is invalid or against the rules
     */
    void makeBuild(String senderID, PacketBuild packetBuild) throws InvalidPacketException;
    /**
     * This method is used for the normal game mode,
     * a client always asks for the possible moves to make the player choose from them only.
     * If the request is correct and valid the server answers with the requested possible moves
     * @param senderID the asking client
     * @param packetMove the context of the request
     */
    void getPossibleMoves(String senderID, PacketMove packetMove);
    /**
     * This method is used for the normal game mode,
     * a client always asks for the possible builds to make the player choose from them only.
     * If the request is correct and valid the server answers with the requested possible builds
     * @param senderID the asking client
     * @param packetBuild the context of the request
     */
    void getPossibleBuilds(String senderID, PacketBuild packetBuild);
    /**
     * This function is used when a client provides the server with some chosen cards,
     * it checks that the server is indeed waiting for that choice and eventually saves the
     * chosen cards and proceeds to ask the cards to the next player or ends the setup
     * and asks the challenger to choose the starting player
     * @param senderID the client providing the cards
     * @param selectedCards the chosen cards
     * @throws InvalidPacketException if the sender is who I am waiting for, but makes some mistakes in the choice, I notify him by throwing this exception
     */
    void setSelectedCards(String senderID, List<String> selectedCards) throws InvalidPacketException;

    /**
     * This function is supposed to be called when the challenger provides the choice
     * regarding the starting player, it checks the choice and eventually asks the chosen starting player to place his workers
     * @param senderID the client providing the choice
     * @param startPlayer the chosen starting player nickname
     * @throws InvalidPacketException if the sender is who I am waiting for, but makes some mistakes in the choice, I notify him by throwing this exception
     */
    void setStartPlayer(String senderID, String startPlayer) throws InvalidPacketException;
    /**
     * This function is supposed to be called when a player provides the chosen workers positions,
     * it checks the choice and places the workers on the board,
     * afterwards either asks the next player to place his workers and sends info of current workers' placements,
     * or, if the setup is completed sets the relative phase and ends
     *
     * @param senderID the client providing the choice
     * @param workersPositions the proposed workers positions
     * @throws InvalidPacketException if the sender is who I am waiting for, but makes some mistakes in the choice, I notify him by throwing this exception
     */
    void setWorkersPositions(String senderID, Map<String,Point> workersPositions) throws InvalidPacketException;





}