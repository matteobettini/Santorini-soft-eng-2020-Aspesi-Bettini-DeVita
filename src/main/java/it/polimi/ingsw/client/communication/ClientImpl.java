package it.polimi.ingsw.client.communication;

import it.polimi.ingsw.client.communication.enums.ConnectionState;
import it.polimi.ingsw.common.utils.observe.ClientObserver;
import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class manages the connection with the game server using a socket
 */
public class ClientImpl implements Client {

    public static final int PING_PERIOD = 5000;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int INVALID_IP_TIMEOUT = 2000;

    private final Queue<Observer<PacketSetup>> packetSetupObservers;
    private final Queue<ClientObserver<PacketCardsFromServer>> packetCardsFromServerObservers;
    private final Queue<ClientObserver<PacketDoAction>> packetDoActionObservers;
    private final Queue<Observer<PacketUpdateBoard>> packetUpdateBoardObservers;
    private final Queue<Observer<PacketPossibleMoves>> packetPossibleMovesObservers;
    private final Queue<Observer<PacketPossibleBuilds>> packetPossibleBuildsObservers;
    private final Queue<Observer<PacketMatchStarted>> packetMatchStartedObservers;
    private final Queue<ClientObserver<String>> insertNickRequestObservers;
    private final Queue<ClientObserver<String>> insertNumOfPlayersAndGamemodeRequestObservers;
    private final Queue<Observer<ConnectionStatus>> connectionStatusObservers;

    private final BlockingDeque<Object> incomingPackets;
    private final Thread packetReceiver;
    private final Thread pinger;

    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream is;

    private final ReentrantLock sendLock = new ReentrantLock(true);

    private Object lastActionRequest;

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean isRetry = new AtomicBoolean(false);
    private final AtomicBoolean ended = new AtomicBoolean(false);


    /**
     * Constructs the client connection, initializing its attributes
     */
    public ClientImpl(){
        this.incomingPackets = new LinkedBlockingDeque<>();

        this.packetCardsFromServerObservers = new ConcurrentLinkedQueue<>();
        this.packetDoActionObservers = new ConcurrentLinkedQueue<>();
        this.packetPossibleBuildsObservers = new ConcurrentLinkedQueue<>();
        this.packetPossibleMovesObservers = new ConcurrentLinkedQueue<>();
        this.packetSetupObservers = new ConcurrentLinkedQueue<>();
        this.packetUpdateBoardObservers = new ConcurrentLinkedQueue<>();
        this.packetMatchStartedObservers = new ConcurrentLinkedQueue<>();
        this.insertNickRequestObservers = new ConcurrentLinkedQueue<>();
        this.insertNumOfPlayersAndGamemodeRequestObservers = new ConcurrentLinkedQueue<>();
        this.connectionStatusObservers = new ConcurrentLinkedQueue<>();

        this.packetReceiver = new Thread(this::manageIncomingPackets);
        this.pinger = new Thread(() -> {
            while(started.get() && !ended.get()) {
                try {
                    Thread.sleep(PING_PERIOD);
                    send(ConnectionMessages.PING);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
    }
    @Override
    public void asyncStart(String address, int port, boolean asDemon){
        if(!started.get()) {
            Thread thread = new Thread(()->start(address, port));
            pinger.setDaemon(true);
            packetReceiver.setDaemon(asDemon);
            thread.setDaemon(asDemon);
            thread.start();
        }
    }
    @Override
    public void start(String address, int port){
        if(started.compareAndSet(false, true)) {

            try {

                this.socket = new Socket();
                this.socket.connect(new InetSocketAddress(address, port), INVALID_IP_TIMEOUT);
                this.socket.setSoTimeout(CONNECTION_TIMEOUT);


                os = new ObjectOutputStream(socket.getOutputStream());
                is = new ObjectInputStream(socket.getInputStream());

            } catch (IOException e) {
                manageUnconnected();
                return;
            }

            notifyConnectionStatusObservers(new ConnectionStatus(ConnectionState.CONNECTED, null));

            packetReceiver.start();
            pinger.start();

            boolean end = false;
            boolean skip;

            try{
                while (!end) {
                    skip = false;
                    Object packetFromServer = is.readObject();
                    if(packetFromServer instanceof ConnectionMessages) {
                        ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                        if(messageFromServer == ConnectionMessages.PING)
                            skip = true;
                        else if (messageFromServer == ConnectionMessages.MATCH_INTERRUPTED || messageFromServer == ConnectionMessages.TIMER_ENDED || messageFromServer == ConnectionMessages.CONNECTION_CLOSED) {
                            packetReceiver.interrupt();
                            pinger.interrupt();
                            notifyConnectionStatusObservers(new ConnectionStatus(ConnectionState.CLOSURE_UNEXPECTED, messageFromServer.getMessage()));
                            break;
                        }else if(messageFromServer == ConnectionMessages.MATCH_FINISHED){
                            pinger.interrupt();
                            end = true;
                        }
                    }
                    if(!skip)
                        incomingPackets.add(packetFromServer);
                }

            } catch (IOException | ClassNotFoundException e) {
                packetReceiver.interrupt();
                pinger.interrupt();
                notifyConnectionStatusObservers(new ConnectionStatus(ConnectionState.CLOSURE_UNEXPECTED, ConnectionMessages.CONNECTION_CLOSED.getMessage()));
            } finally {
                closeRoutine();
            }
        }
    }

    /**
     * Gets the incoming packets one by one from a queue and acts accordingly notifying
     * the listeners interested in the received packet
     */
    private void manageIncomingPackets(){

        while(!ended.get()) {

            Object packetFromServer;
            try {
                packetFromServer = incomingPackets.take();
            } catch (InterruptedException e) {
                ended.set(true);
                break;
            }

            if (packetFromServer instanceof ConnectionMessages) {
                ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                if (messageFromServer == ConnectionMessages.INSERT_NICKNAME) {
                    notifyInsertNickRequestObserver(messageFromServer.getMessage(), false);
                } else if (messageFromServer == ConnectionMessages.INVALID_NICKNAME || messageFromServer == ConnectionMessages.TAKEN_NICKNAME) {
                    notifyInsertNickRequestObserver(messageFromServer.getMessage(), true);
                } else if (messageFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE) {
                    notifyInsertNumOfPlayersAndGamemodeRequestObservers(messageFromServer.getMessage(), isRetry.get());
                    isRetry.set(false);
                } else if (messageFromServer == ConnectionMessages.INVALID_PACKET) {
                    assert (lastActionRequest != null);
                    isRetry.set(true);
                    incomingPackets.addFirst(lastActionRequest);
                } else if (messageFromServer == ConnectionMessages.MATCH_FINISHED) {
                    ended.set(true);
                    notifyConnectionStatusObservers(new ConnectionStatus(ConnectionState.MATCH_ENDED, messageFromServer.getMessage()));
                }
            } else if (packetFromServer instanceof PacketMatchStarted) {
                PacketMatchStarted packetMatchStarted = (PacketMatchStarted) packetFromServer;
                notifyPacketMatchStartedObservers(packetMatchStarted);
            } else if (packetFromServer instanceof PacketCardsFromServer) {
                PacketCardsFromServer packetCardsFromServer = (PacketCardsFromServer) packetFromServer;
                notifyPacketCardsFromServerObservers(packetCardsFromServer, isRetry.get());
                isRetry.set(false);
            } else if (packetFromServer instanceof PacketSetup) {
                PacketSetup packetSetup = (PacketSetup) packetFromServer;
                notifyPacketSetupObservers(packetSetup);
            } else if (packetFromServer instanceof PacketDoAction) {
                PacketDoAction packetDoAction = (PacketDoAction) packetFromServer;
                notifyPacketDoActionObservers(packetDoAction, isRetry.get());
                isRetry.set(false);
            } else if (packetFromServer instanceof PacketUpdateBoard) {
                PacketUpdateBoard packetUpdateBoard = (PacketUpdateBoard) packetFromServer;
                notifyPacketUpdateBoardObservers(packetUpdateBoard);
            } else if (packetFromServer instanceof PacketPossibleBuilds) {
                PacketPossibleBuilds packetPossibleBuilds = (PacketPossibleBuilds) packetFromServer;
                notifyPacketPossibleBuildsObservers(packetPossibleBuilds);
            } else if (packetFromServer instanceof PacketPossibleMoves) {
                PacketPossibleMoves packetPossibleMoves = (PacketPossibleMoves) packetFromServer;
                notifyPacketPossibleMovesObservers(packetPossibleMoves);
            }

            if (packetFromServer instanceof PacketDoAction || packetFromServer instanceof PacketCardsFromServer || packetFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE)
                lastActionRequest = packetFromServer;

        }
    }

    @Override
    public void send(Serializable packet) {
        sendLock.lock();
        try {
            if (started.get() && !ended.get()) {
                try {
                    os.writeObject(packet);
                    os.flush();
                    os.reset();
                } catch (IOException e) {
                    closeRoutine();
                }
            }
        }finally {
            sendLock.unlock();
        }
    }

    /**
     * Called when the client is unable to connect to the given ip and port,
     * notifies the listeners interested in the connection status
     */
    private void manageUnconnected(){
        if(packetReceiver.isAlive())
            packetReceiver.interrupt();
        notifyConnectionStatusObservers(new ConnectionStatus(ConnectionState.UNABLE_TO_CONNECT, "Unable to connect to the server"));
        closeRoutine();
    }

    /**
     * Tries to close the socket channel
     */
    private void closeRoutine(){

        started.set(false);

        try {
            is.close();
        }catch (Exception ignored){}
        try {
            os.close();
        }catch (Exception ignored){}
        try{
            socket.close();
        }catch (Exception ignored){ }

    }


    @Override
    public void addConnectionStatusObserver(Observer<ConnectionStatus> o) {
        this.connectionStatusObservers.add(o);
    }
    @Override
    public void addInsertNickRequestObserver(ClientObserver<String> o) {
        this.insertNickRequestObservers.add(o);
    }
    @Override
    public void addInsertNumOfPlayersAndGamemodeRequestObserver(ClientObserver<String> o) {
        this.insertNumOfPlayersAndGamemodeRequestObservers.add(o);
    }
    @Override
    public void addPacketMatchStartedObserver(Observer<PacketMatchStarted> o) {
        this.packetMatchStartedObservers.add(o);
    }
    @Override
    public void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> o) {
        this.packetPossibleMovesObservers.add(o);
    }
    @Override
    public void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> o) {
        this.packetPossibleBuildsObservers.add(o);
    }
    @Override
    public void addPacketSetupObserver(Observer<PacketSetup> o){
        this.packetSetupObservers.add(o);
    }
    @Override
    public void addPacketDoActionObserver(ClientObserver<PacketDoAction> o){
        this.packetDoActionObservers.add(o);
    }
    @Override
    public void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> o){
        this.packetUpdateBoardObservers.add(o);
    }
    @Override
    public void addPacketCardsFromServerObserver(ClientObserver<PacketCardsFromServer> o){
        this.packetCardsFromServerObservers.add(o);
    }

    public void notifyConnectionStatusObservers(ConnectionStatus p){
        for(Observer<ConnectionStatus> o : connectionStatusObservers){
            o.update(p);
        }
    }
    public void notifyInsertNickRequestObserver(String p, boolean isRetry){
        for(ClientObserver<String> o : insertNickRequestObservers){
            o.update(p,isRetry);
        }
    }
    public void notifyInsertNumOfPlayersAndGamemodeRequestObservers(String p, boolean isRetry){
        for(ClientObserver<String> o : insertNumOfPlayersAndGamemodeRequestObservers){
            o.update(p,isRetry);
        }
    }

    public void notifyPacketSetupObservers(PacketSetup p){
        for(Observer<PacketSetup> o : packetSetupObservers){
            o.update(p);
        }
    }
    public void notifyPacketUpdateBoardObservers(PacketUpdateBoard p){
        for(Observer<PacketUpdateBoard> o : packetUpdateBoardObservers){
            o.update(p);
        }
    }
    public void notifyPacketCardsFromServerObservers(PacketCardsFromServer p, boolean isRetry){
        for(ClientObserver<PacketCardsFromServer> o : packetCardsFromServerObservers){
            o.update(p, isRetry);
        }
    }
    public void notifyPacketDoActionObservers(PacketDoAction p, boolean isRetry){
        for(ClientObserver<PacketDoAction> o : packetDoActionObservers){
            o.update(p, isRetry);
        }
    }
    public void notifyPacketPossibleMovesObservers(PacketPossibleMoves p){
        for(Observer<PacketPossibleMoves> o : packetPossibleMovesObservers){
            o.update(p);
        }
    }
    public void notifyPacketPossibleBuildsObservers(PacketPossibleBuilds p){
        for(Observer<PacketPossibleBuilds> o : packetPossibleBuildsObservers){
            o.update(p);
        }
    }
    public void notifyPacketMatchStartedObservers(PacketMatchStarted p){
        for(Observer<PacketMatchStarted> o : packetMatchStartedObservers){
            o.update(p);
        }
    }
}
