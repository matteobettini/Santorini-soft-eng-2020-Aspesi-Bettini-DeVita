package it.polimi.ingsw.model;

import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;

import java.lang.reflect.WildcardType;
import java.util.List;

/**
 * This class contains all the actual instances of the Model data.
 * It is protected from the external of the package and it handles the entry points of the Model.
 */
public abstract class InternalModel {

    /**
     * Getter that returns the Board used in the match.
     * @return an instance of Board.
     */
    public abstract Board getBoard();

    /**
     * Getter that returns the List of subscribed Players to the match.
     * @return a List of instances of Players.
     */
    public abstract List<Player> getPlayers();

    /**
     * Getter that returns the Player given his nickname.
     * @param playerNick is a String that contains the Player nickname.
     * @return the Player that has playerNick as nickname.
     */
    public abstract Player getPlayerByNick(String playerNick);

    /**
     * Getter that returns the Worker given its ID.
     * @param workerID is the ID of the Worker.
     * @return the Worker that has workerID as ID.
     */
    public abstract Worker getWorkerByID(String workerID);

    /**
     * This is a method that converts the packetMove received through the connection
     * into an instance of MoveData.
     * @param packetMove is the packet containing the data of the move action performed by
     * the Player in his turn.
     * @return a MoveData obtained by the conversion of a packetMove.
     */
    public abstract MoveData packetMoveToMoveData(PacketMove packetMove);

    /**
     * This is a method that converts the packetBuild received through the connection
     * into an instance of BuildData.
     * @param packetBuild is the packet containing the data of the build action performed by
     * the Player in his turn.
     * @return a BuildData obtained by the conversion of a packetBuild.
     */
    public abstract BuildData packetBuildToBuildData(PacketBuild packetBuild);



}
