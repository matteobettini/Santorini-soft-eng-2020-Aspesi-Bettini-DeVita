package it.polimi.ingsw.server.model;

import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

public interface ObservableModel {

    void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> observer);
    void addPacketDoActionObserver(Observer<PacketDoAction> observer);
    void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> observer);
    void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> observer);
    void addPacketSetupObserver(Observer<PacketSetup> observer);
    void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> observer);
    void setGameFinishedHandler(Observer<String> gameFinishedHandler);

}
