package it.polimi.ingsw;

import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;

public interface Client {

    void asyncStart(String address, int port);
    void start(String address, int port);

    void send(Object packet);

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
