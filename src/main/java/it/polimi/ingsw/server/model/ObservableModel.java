package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

public interface ObservableModel {

    /**
     * Used to add the observer
     * @param observer the observer
     */
    void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> observer);
    /**
     * Used to add the observer
     * @param observer the observer
     */
    void addPacketDoActionObserver(Observer<PacketDoAction> observer);
    /**
     * Used to add the observer
     * @param observer the observer
     */
    void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> observer);
    /**
     * Used to add the observer
     * @param observer the observer
     */
    void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> observer);
    /**
     * Used to add the observer
     * @param observer the observer
     */
    void addPacketSetupObserver(Observer<PacketSetup> observer);
    /**
     * Used to add the observer
     * @param observer the observer
     */
    void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> observer);
    /**
     * Used to add the handler for the game end event
     * @param gameFinishedHandler the handler
     */
    void setGameFinishedHandler(Observer<String> gameFinishedHandler);

}
