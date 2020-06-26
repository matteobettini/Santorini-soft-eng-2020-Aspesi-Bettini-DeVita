package it.polimi.ingsw.client.communication;

import it.polimi.ingsw.common.utils.observe.ClientObserver;
import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

import java.io.Serializable;

public interface Client {

    void asyncStart(String address, int port, boolean asDemon);
    void start(String address, int port);

    void send(Serializable packet);

    void addPacketCardsFromServerObserver(ClientObserver<PacketCardsFromServer> observer);
    void addPacketDoActionObserver(ClientObserver<PacketDoAction> observer);
    void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> observer);
    void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> observer);
    void addPacketSetupObserver(Observer<PacketSetup> observer);
    void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> observer);
    void addPacketMatchStartedObserver(Observer<PacketMatchStarted> observer);
    void addInsertNickRequestObserver(ClientObserver<String> o);
    void addInsertNumOfPlayersAndGamemodeRequestObserver(ClientObserver<String> o);
    void addConnectionStatusObserver(Observer<ConnectionStatus> o);


}
