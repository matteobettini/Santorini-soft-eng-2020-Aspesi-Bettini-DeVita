package it.polimi.ingsw;

import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;

public interface Client {

    void asyncStart(String address, int port);
    void start(String address, int port);

    void sendString(String s);
    void sendInt(int n);
    void sendBoolean(boolean b);
    void send(Object packet);
    void destroy();

    void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> observer);
    void addPacketDoActionObserver(Observer<PacketDoAction> observer);
    void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> observer);
    void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> observer);
    void addPacketSetupObserver(Observer<PacketSetup> observer);
    void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> observer);
    void addPacketMatchStartedObserver(Observer<PacketMatchStarted> observer);
    void addInsertNickRequestObserver(Observer<String> o);
    void addInsertNumOfPlayersRequestObserver(Observer<String> o);
    void addInsertGamemodeRequestObserver(Observer<String> o);
    void addConnectionStatusObserver(Observer<ConnectionStatus> o);


}
