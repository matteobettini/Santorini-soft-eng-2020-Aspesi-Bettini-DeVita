package it.polimi.ingsw;

import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ClientImpl implements Client {


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
    private Thread packetReceiver;

    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream is;

    private Object lastPacketReceived;

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean isRetry = new AtomicBoolean(false);
    private final AtomicBoolean end = new AtomicBoolean(false);



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


    }
    @Override
    public void asyncStart(String address, int port){
        if(!started.get()) {
            new Thread(()->start(address, port)).start();
        }
    }
    @Override
    public void start(String address, int port){
        if(started.compareAndSet(false, true)) {

            try {

                this.socket = new Socket();
                this.socket.connect(new InetSocketAddress(address, port), 3000);

                os = new ObjectOutputStream(socket.getOutputStream());
                is = new ObjectInputStream(socket.getInputStream());

            } catch (IOException e) {
                manageClosure("Impossible to establish connection");
                return;
            }

            notifyConnectionStatusObservers(new ConnectionStatus(false, null));

            packetReceiver = new Thread(this::manageIncomingPacket);
            packetReceiver.start();

            boolean end = false;

            try{
                while (true) {
                    Object packetFromServer = is.readObject();
                    if(packetFromServer instanceof ConnectionMessages) {
                        ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                        if (messageFromServer == ConnectionMessages.MATCH_INTERRUPTED || messageFromServer == ConnectionMessages.TIMER_ENDED || messageFromServer == ConnectionMessages.CONNECTION_CLOSED) {
                            packetReceiver.interrupt();
                            notifyConnectionStatusObservers(new ConnectionStatus(true, messageFromServer.getMessage()));
                            break;
                        }else if(messageFromServer == ConnectionMessages.MATCH_FINISHED){
                            end = true;
                        }
                    }
                    incomingPackets.add(packetFromServer);
                    if(end)
                        break;
                }


            } catch (IOException | ClassNotFoundException e) {
                packetReceiver.interrupt();
                notifyConnectionStatusObservers(new ConnectionStatus(true, ConnectionMessages.CONNECTION_CLOSED.getMessage()));
            } finally {
                closeRoutine();
            }
        }
    }


    private void manageIncomingPacket(){

        while(!end.get()) {

            Object packetFromServer = null;
            try {
                packetFromServer = incomingPackets.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (packetFromServer instanceof ConnectionMessages) {
                ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                if (messageFromServer == ConnectionMessages.INSERT_NICKNAME) {
                    notifyInsertNickRequestObserver(messageFromServer.getMessage(), false);
                } else if (messageFromServer == ConnectionMessages.INVALID_NICKNAME || messageFromServer == ConnectionMessages.TAKEN_NICKNAME) {
                    notifyInsertNickRequestObserver(messageFromServer.getMessage(), true);
                } else if (messageFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE) {
                    notifyinsertNumOfPlayersAndGamemodeRequestObservers(messageFromServer.getMessage(), isRetry.get());
                    isRetry.set(false);
                } else if (messageFromServer == ConnectionMessages.INVALID_PACKET) {
                    System.out.println("[FROM SERVER]: INVALID PACKET");
                    assert (lastPacketReceived != null);
                    isRetry.set(true);
                    incomingPackets.addFirst(lastPacketReceived);
                } else if (messageFromServer == ConnectionMessages.MATCH_FINISHED) {
                    end.set(true);
                    notifyConnectionStatusObservers(new ConnectionStatus(true, messageFromServer.getMessage()));
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
                lastPacketReceived = packetFromServer;

        }
    }

    @Override
    public void send(Object packet){
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            manageClosure();
        }
    }

    private void manageClosure(){
        manageClosure(ConnectionMessages.CONNECTION_CLOSED.getMessage());
    }

    private void manageClosure(String reasonOfClosure){
        packetReceiver.interrupt();
        notifyConnectionStatusObservers(new ConnectionStatus(true, reasonOfClosure));
        closeRoutine();
    }


    private void closeRoutine(){

        try {
            is.close();
        }catch (Exception ignored){}
        try {
            os.close();
        }catch (Exception ignored){}
        try{
            socket.close();
        }catch (Exception ignored){ }

        started.set(false);

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
    public void notifyinsertNumOfPlayersAndGamemodeRequestObservers(String p, boolean isRetry){
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
