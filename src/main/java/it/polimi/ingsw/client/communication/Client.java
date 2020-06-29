package it.polimi.ingsw.client.communication;

import it.polimi.ingsw.common.utils.observe.ClientObserver;
import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

import java.io.Serializable;

public interface Client {

    /**
     * Starts the socket client on a new thread
     * @param address the ip of the server
     * @param port the port of the server
     * @param asDemon start the new thread a s a demon
     */
    void asyncStart(String address, int port, boolean asDemon);

    /**
     * Starts the client connection via socket
     * @param address the ip of the server
     * @param port the port of the server
     */
    void start(String address, int port);

    /**
     * Used to send a serializable packet to hte server
     * @param packet the packet to be sent
     */
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
