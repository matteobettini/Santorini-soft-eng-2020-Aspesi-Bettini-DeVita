package it.polimi.ingsw;

import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.packets.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class SampleClient {

    private Socket socket;

    private String clientNickname;

    private ObjectOutputStream os;
    private ObjectInputStream is;
    private final BufferedReader input;
    
    private Object lastPacketReceived;

    private final ExecutorService executor;
    private final ReentrantLock lockReceive = new ReentrantLock(true);
    private final AtomicBoolean started = new AtomicBoolean(false);


    public static void main(String[] args) {

        new SampleClient().start("127.0.0.1", 4567);
    }

    public SampleClient(){

        this.executor = Executors.newCachedThreadPool();
        this.input = new BufferedReader(new InputStreamReader(System.in));
    }

    public void asyncStart(String address, int port){
        if(!started.get()) {
            new Thread(()->start(address, port)).start();
        }
    }

    public void start(String address, int port){
        if(started.compareAndSet(false, true)) {

            try {
                this.socket = new Socket();
                this.socket.connect(new InetSocketAddress(address, port), 3000);
                System.out.println("Connection established to server at: " + address + " port: " + port);

                os = new ObjectOutputStream(socket.getOutputStream());
                is = new ObjectInputStream(socket.getInputStream());

            } catch (IOException e) {
                manageClosure("Impossible to establish connection");
                return;
            }

            notifyConnectionStatusObservers(new ConnectionStatus(false, null));

            try{
                while (true) {
                    Object packetFromServer = is.readObject();
                    if(packetFromServer instanceof ConnectionMessages) {
                        ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                        if (messageFromServer == ConnectionMessages.MATCH_INTERRUPTED || messageFromServer == ConnectionMessages.TIMER_ENDED || messageFromServer == ConnectionMessages.CONNECTION_CLOSED) {
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


    
    private void manageIncomingPacket(Object packetFromServer) {
        lockReceive.lock();
        try {
            if (packetFromServer instanceof ConnectionMessages) {
                ConnectionMessages messageFromServer = (ConnectionMessages) packetFromServer;
                if (messageFromServer == ConnectionMessages.INSERT_NICKNAME || messageFromServer == ConnectionMessages.INVALID_NICKNAME || messageFromServer == ConnectionMessages.TAKEN_NICKNAME) {
                    System.out.println("\n" + messageFromServer.getMessage());
                    String name = getLine();
                    if(name == null)
                        return;
                    clientNickname = name;
                    PacketNickname packetNickname = new PacketNickname(name);
                    send(packetNickname);
                } else if (messageFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE) {
                    System.out.println("\n" + messageFromServer.getMessage());
                    Integer numOfPlayers = getInt();
                    if (numOfPlayers == null)
                        return;
                    Boolean hardcore = getBoolean();
                    if (hardcore == null)
                        return;
                    PacketNumOfPlayersAndGamemode packetNumOfPlayersAndGamemode = new PacketNumOfPlayersAndGamemode(numOfPlayers, hardcore);
                    send(packetNumOfPlayersAndGamemode);
                }else if (messageFromServer == ConnectionMessages.INVALID_PACKET) {
                    System.out.println("[FROM SERVER]: INVALID PACKET");
                    assert (lastPacketReceived != null);
                    manageIncomingPacket(lastPacketReceived);
                }
            } else if (packetFromServer instanceof PacketMatchStarted) {
                PacketMatchStarted packetMatchStarted = (PacketMatchStarted) packetFromServer;
                System.out.println("\nMatch started!!!\nPlayers: " + packetMatchStarted.getPlayers() + "\nHardcore: " + packetMatchStarted.isHardcore());
            } else if (packetFromServer instanceof PacketCardsFromServer) {
                PacketCardsFromServer packetCardsFromServer = (PacketCardsFromServer) packetFromServer;
                if(!packetCardsFromServer.getTo().equals(clientNickname))
                    return;
                System.out.println("\nChoose your cards!\nHere are all the cards: " + packetCardsFromServer.getAvailableCards() + "\nChoose: " + packetCardsFromServer.getNumberToChoose());
                String chosenCards = getLine();
                if(chosenCards == null)
                    return;
                List<String> chosenCardsList = Arrays.asList(chosenCards.split("\\s*,\\s*"));
                PacketCardsFromClient packetCardsFromClient = new PacketCardsFromClient(chosenCardsList);
                send(packetCardsFromClient);
            } else if (packetFromServer instanceof PacketSetup) {
                PacketSetup packetSetup = (PacketSetup) packetFromServer;
                System.out.println("\nHere is the setup!\nHere are all the cards: " + packetSetup.getCards() + "\n" + packetSetup.getIds() + "\n" + packetSetup.getColors());
            } else if (packetFromServer instanceof PacketDoAction) {
                PacketDoAction packetDoAction = (PacketDoAction) packetFromServer;
                if(!packetDoAction.getTo().equals(clientNickname))
                    return;
                System.out.println("\nDo this action: " + packetDoAction.getActionType());
                if (packetDoAction.getActionType() == ActionType.CHOOSE_START_PLAYER) {
                    System.out.println("\n" + "Choose a start player by writing his name");
                    String startPlayer = getLine();
                    if(startPlayer == null)
                        return;
                    PacketStartPlayer packetStartPlayer = new PacketStartPlayer(startPlayer);
                    send(packetStartPlayer);
                } else if (packetDoAction.getActionType() == ActionType.SET_WORKERS_POSITION) {
                    System.out.println("\n" + "Select your workers positions");
                    System.out.println("\n" + "DEMO ENDS HERE");
                }
            }
            if(packetFromServer instanceof PacketDoAction || packetFromServer instanceof PacketCardsFromServer || packetFromServer == ConnectionMessages.INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE)
                lastPacketReceived = packetFromServer;

        }finally {
            lockReceive.unlock();
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
        manageClosure(ConnectionMessages.CONNECTION_CLOSED.getMessage());
    }

    private void manageClosure(String reasonOfClosure){
        executor.shutdownNow();
        notifyConnectionStatusObservers(new ConnectionStatus(true, reasonOfClosure));
        closeRoutine();
    }


    public void closeRoutine(){

        try {
            input.close();
        } catch (IOException ignored) { }
        try {
            is.close();
        }catch (IOException ignored){}
        try {
            os.close();
        }catch (IOException ignored){}
        try{
            socket.close();
        }catch (IOException ignored){ }

        started.set(false);

    }

    private String getLine(){
        String name = null;
        try {
            while (!input.ready()){
                Thread.sleep(200);
            }
            name = input.readLine();
        }catch (InterruptedException | IOException e){
            return null;
        }
        return name;
    }

    private Integer getInt(){
        String numString;
        Integer num = null;
        boolean fin = false;

        try {
            do {
                while (!input.ready()) {
                    Thread.sleep(200);
                }
                numString = input.readLine();
                try {
                    num = Integer.parseInt(numString);
                    fin = true;
                } catch (NumberFormatException e) {
                    System.out.println("Retry");
                }
            }while(!fin);

        }catch (InterruptedException | IOException e){
            return null;
        }
        return num;
    }

    private Boolean getBoolean(){
        String boolString;
        Boolean bool = null;
        boolean fin = false;
        try {
            do {
                while (!input.ready()) {
                    Thread.sleep(200);
                }
                boolString = input.readLine();
                try {
                    if(!boolString.equals("true") && !boolString.equals("false"))
                        throw new NumberFormatException();
                    bool = Boolean.parseBoolean(boolString);
                    fin = true;
                } catch (NumberFormatException e) {
                    System.out.println("Retry");
                }
            }while(!fin);

        }catch (InterruptedException | IOException e){
            return null;
        }

        return bool;
    }


    public void notifyConnectionStatusObservers(ConnectionStatus p){
        System.out.println("Connection closed: [" + p.isClosed() + "], reason: [" + p.getReasonOfClosure() +"]");
    }



}

