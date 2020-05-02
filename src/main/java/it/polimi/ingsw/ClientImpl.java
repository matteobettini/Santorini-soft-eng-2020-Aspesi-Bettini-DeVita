package it.polimi.ingsw;

import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class ClientImpl implements Client {


    private final List<Observer<PacketSetup>> packetSetupObservers;
    private final List<Observer<PacketCardsFromServer>> packetCardsFromServerObservers;
    private final List<Observer<PacketDoAction>> packetDoActionObservers;
    private final List<Observer<PacketUpdateBoard>> packetUpdateBoardObservers;
    private final List<Observer<PacketPossibleMoves>> packetPossibleMovesObservers;
    private final List<Observer<PacketPossibleBuilds>> packetPossibleBuildsObservers;
    private final List<Observer<PacketMatchStarted>> packetMatchStartedObservers;
    private final List<Observer<String>> insertNickRequestObservers;
    private final List<Observer<String>> insertNumOfPlayersRequestObservers;
    private final List<Observer<String>> insertGamemodeRequestObservers;
    private final List<Observer<ConnectionStatus>> connectionStatusObservers;


    private Socket socket;

    private ObjectOutputStream os;
    private ObjectInputStream is;

    private Object lastPacketReceived;

    private final ExecutorService executor;
    private final ReentrantLock lockReceive = new ReentrantLock(true);
    private final AtomicBoolean started = new AtomicBoolean(false);

    ClientImpl(){
        this.packetCardsFromServerObservers = new ArrayList<>();
        this.packetDoActionObservers = new ArrayList<>();
        this.packetPossibleBuildsObservers = new ArrayList<>();
        this.packetPossibleMovesObservers = new ArrayList<>();
        this.packetSetupObservers = new ArrayList<>();
        this.packetUpdateBoardObservers = new ArrayList<>();
        this.packetMatchStartedObservers = new ArrayList<>();
        this.insertNickRequestObservers = new ArrayList<>();
        this.insertNumOfPlayersRequestObservers = new ArrayList<>();
        this.insertGamemodeRequestObservers = new ArrayList<>();
        this.connectionStatusObservers = new ArrayList<>();

        this.executor = Executors.newCachedThreadPool();

    }

    public void asyncStart(String address, int port){
        if(!started.get()) {
            new Thread(()->start(address, port)).start();
        }
    }

    public void start(String address, int port){
        if(started.compareAndSet(false, true)) {

            try {
                this.socket = new Socket(address, port);
                System.out.println("Connection established to server at: " + address + " port: " + port);

                os = new ObjectOutputStream(socket.getOutputStream());
                is = new ObjectInputStream(socket.getInputStream());

                notifyConnectionStatusObservers(new ConnectionStatus(false, null));

                while (true) {
                    Object packetFromServer = is.readObject();
                    if(packetFromServer instanceof ConnectionMessages) {
                        ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                        if (messageFromServer == ConnectionMessages.MATCH_ENDED || messageFromServer == ConnectionMessages.TIMER_ENDED || messageFromServer == ConnectionMessages.CONNECTION_CLOSED) {
                            executor.shutdownNow();
                            notifyConnectionStatusObservers(new ConnectionStatus(true, messageFromServer.getMessage()));
                            break;
                        }
                    }
                    executor.submit(new Thread(() -> manageIncomingPacket(packetFromServer)));
                }


            } catch (IOException | ClassNotFoundException e) {
                executor.shutdownNow();
                notifyConnectionStatusObservers(new ConnectionStatus(true, ConnectionMessages.CONNECTION_CLOSED.getMessage()));
            } finally {
                closeRoutine();
            }
        }
    }

    private void manageIncomingPacket(Object packetFromServer){
        lockReceive.lock();
        try {
            if (packetFromServer instanceof ConnectionMessages) {
                ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                if (messageFromServer == ConnectionMessages.INSERT_NICKNAME) {
                    notifyInsertNickRequestObserver(messageFromServer.getMessage());
                } else if (messageFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS) {
                    notifyInsertNumOfPlayersRequestObservers(messageFromServer.getMessage());
                } else if (messageFromServer == ConnectionMessages.IS_IT_HARDCORE) {
                    notifyInsertGamemodeRequestObservers(messageFromServer.getMessage());
                } else if (messageFromServer == ConnectionMessages.INVALID_PACKET) {
                    System.out.println("[FROM SERVER]: INVALID PACKET");
                    assert (lastPacketReceived != null);
                    manageIncomingPacket(lastPacketReceived);
                }
            } else if (packetFromServer instanceof PacketMatchStarted) {
                PacketMatchStarted packetMatchStarted = (PacketMatchStarted) packetFromServer;
                notifyPacketMatchStartedObservers(packetMatchStarted);
            } else if (packetFromServer instanceof PacketCardsFromServer) {
                PacketCardsFromServer packetCardsFromServer = (PacketCardsFromServer) packetFromServer;
                notifyPacketCardsFromServerObservers(packetCardsFromServer);
            } else if (packetFromServer instanceof PacketSetup) {
                PacketSetup packetSetup = (PacketSetup) packetFromServer;
                notifyPacketSetupObservers(packetSetup);
            } else if (packetFromServer instanceof PacketDoAction) {
                PacketDoAction packetDoAction = (PacketDoAction) packetFromServer;
                notifyPacketDoActionObservers(packetDoAction);
            }else if (packetFromServer instanceof PacketUpdateBoard) {
                PacketUpdateBoard packetUpdateBoard = (PacketUpdateBoard) packetFromServer;
                notifyPacketUpdateBoardObservers(packetUpdateBoard);
            } else if (packetFromServer instanceof PacketPossibleBuilds) {
                PacketPossibleBuilds packetPossibleBuilds = (PacketPossibleBuilds) packetFromServer;
                notifyPacketPossibleBuildsObservers(packetPossibleBuilds);
            }else if (packetFromServer instanceof PacketPossibleMoves) {
                PacketPossibleMoves packetPossibleMoves = (PacketPossibleMoves) packetFromServer;
                notifyPacketPossibleMovesObservers(packetPossibleMoves);
            }

            if(packetFromServer instanceof PacketDoAction || packetFromServer instanceof PacketCardsFromServer)
                lastPacketReceived = packetFromServer;

        }finally {
            lockReceive.unlock();
        }
    }


    public void sendString(String s) {
        try {
            os.writeUTF(s);
            os.flush();
        }catch (IOException e){
            manageClosure();
        }
    }


    public void sendInt(int n){
        try {
            os.writeInt(n);
            os.flush();
        }catch (IOException e){
            manageClosure();
        }
    }

    public void sendBoolean(boolean b){
        try {
            os.writeBoolean(b);
            os.flush();
        }catch (IOException e){
            manageClosure();
        }
    }

    public void send(Object packet){
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            manageClosure();
        }
    }

    private void manageClosure(){
        executor.shutdownNow();
        notifyConnectionStatusObservers(new ConnectionStatus(true, ConnectionMessages.CONNECTION_CLOSED.getMessage()));
        closeRoutine();
    }

    private void closeRoutine(){

        try {
            is.close();
        }catch (IOException ignored){}
        try {
            os.close();
        }catch (IOException ignored){}
        try{
            socket.close();
        }catch (IOException ignored){ }

        System.out.println("Socket has been closed");
    }



    @Override
    public void addConnectionStatusObserver(Observer<ConnectionStatus> o) {
        this.connectionStatusObservers.add(o);
    }
    @Override
    public void addInsertNickRequestObserver(Observer<String> o) {
        this.insertNickRequestObservers.add(o);
    }
    @Override
    public void addInsertNumOfPlayersRequestObserver(Observer<String> o) {
        this.insertNumOfPlayersRequestObservers.add(o);
    }
    @Override
    public void addInsertGamemodeRequestObserver(Observer<String> o) {
        this.insertGamemodeRequestObservers.add(o);
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
    public void addPacketDoActionObserver(Observer<PacketDoAction> o){
        this.packetDoActionObservers.add(o);
    }
    @Override
    public void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> o){
        this.packetUpdateBoardObservers.add(o);
    }
    @Override
    public void addPacketCardsFromServerObserver(Observer<PacketCardsFromServer> o){
        this.packetCardsFromServerObservers.add(o);
    }

    public void notifyConnectionStatusObservers(ConnectionStatus p){
        for(Observer<ConnectionStatus> o : connectionStatusObservers){
            o.update(p);
        }
    }
    public void notifyInsertNickRequestObserver(String p){
        for(Observer<String> o : insertNickRequestObservers){
            o.update(p);
        }
    }
    public void notifyInsertNumOfPlayersRequestObservers(String p){
        for(Observer<String> o : insertNumOfPlayersRequestObservers){
            o.update(p);
        }
    }
    public void notifyInsertGamemodeRequestObservers(String p){
        for(Observer<String> o : insertGamemodeRequestObservers){
            o.update(p);
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
    public void notifyPacketCardsFromServerObservers(PacketCardsFromServer p){
        for(Observer<PacketCardsFromServer> o : packetCardsFromServerObservers){
            o.update(p);
        }
    }
    public void notifyPacketDoActionObservers(PacketDoAction p){
        for(Observer<PacketDoAction> o : packetDoActionObservers){
            o.update(p);
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
