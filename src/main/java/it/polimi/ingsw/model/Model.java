package it.polimi.ingsw.model;

import it.polimi.ingsw.cardReader.CardFile;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;
import jdk.jfr.EventType;

import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Interface that contains the methods exposed by the Model.
 */
public interface Model{

    /**
     * This method try to perform a move action with the info of the action performed by the Player.
     * @param m is the packet containing the data of the performed move action.
     */
    public void tryMove(PacketMove m);

    /**
     * This method try to perform a build action with the info of the action performed by the Player.
     * @param b is the packet containing the data of the performed build action.
     */
    public void tryBuild(PacketBuild b);

    /**
     * Getter that returns the List of allowed moves the given Worker can perform.
     * @param workerID is the ID of the Worker to check.
     * @return a List of Points where the Worker can move to.
     */
    public List<Point> getAllowedMoves(String workerID);

    /**
     * Getter of all the possible action the given Worker could perform.
     * @param workerID is the ID of the Worker to check.
     * @return a List of EventType.
     */
    public List<EventType> getPossibleWorkerActions(String workerID);

    /**
     * This method performs a cloning of the Board.
     * @return the cloned Board.
     */
    public Board getClonedBoard();

    /**
     * Getter that returns the List of subscribed Players to the match.
     * @return a List of cloned Players.
     */
    public List<Player> getClonedPlayers();

    /**
     * This method receives as input the CardFiles and store them into the Model. In particular
     * it associates the Player with the chosen GodCard.
     * @param defaultStrategy is the CardFile that contains the default strategy.
     * @param playerCardAssociation is a Map that associates a Player to the chosen GodCard.
     */
    public void setGameCards(CardFile defaultStrategy, Map<String, CardFile> playerCardAssociation);

    /**
     * This method checks if a given Player is contained in one of the current matches.
     * @param nickname is a String that contains the nickname of the Player to check.
     * @return true if the given Player is contained, false otherwise.
     */
    public boolean containsPlayer(String nickname);

    /**
     * Setter that set the given Player as one of the losers.
     * @param nickname is the nickname of the Player that has to be set as a loser.
     */
    public void setLoser(String nickname);

    /**
     * Getter that returns the Player that has won.
     * @return an instance of Player.
     */
    public Player getWinners();

    /**
     * Getter that returns all the the Losers.
     * @return a List of Players.
     */
    public List<Player> getLosers();

}