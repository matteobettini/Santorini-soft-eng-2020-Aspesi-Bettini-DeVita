package it.polimi.ingsw.model;

import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;

public interface ObservableModel {

    void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> observer);
    void addPacketDoActionObserver(Observer<PacketDoAction> observer);
    void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> observer);
    void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> observer);
    void addPacketSetupObserver(Observer<PacketSetup> observer);
    void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> observer);
    void setGameFinishedHandler(Observer<String> gameFinishedHandler);

}
