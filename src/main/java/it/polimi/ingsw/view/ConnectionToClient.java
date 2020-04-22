package it.polimi.ingsw.view;

import it.polimi.ingsw.Match;
import it.polimi.ingsw.Server;
import it.polimi.ingsw.observe.Observable;
import it.polimi.ingsw.packets.ConnectionMessages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionToClient extends Observable<Object> implements Runnable{

    private Match match;
    private Server server;

    private final Socket socket;
    private String clientNickname;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private boolean active;
    private boolean inMatch;

    private int desiredNumOfPlayers;
    private boolean desiredHardcore;


    private final Thread timer = new Thread(() -> {
        try {
            Thread.sleep(120000);
            closeRoutine();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    });

    public ConnectionToClient(Socket socket, Server server)  {
        this.server = server;
        this.socket = socket;
        this.active = true;
        this.inMatch = false;
        this.desiredNumOfPlayers = -1;
        this.desiredHardcore = false;
        this.clientNickname = null;
    }

    public void startTimer(){
        timer.start();
    }

    public void stopTimer(){
        timer.interrupt();
    }

    public void send(Object packet){
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            System.err.println("Errror when sending from server, message: " + e.getMessage());
            closeRoutine();
        }
    }

    public String getClientNickname() {
        return clientNickname;
    }

    public void askForDesiredPlayersAndGamemode(){

        try {
            do {
                send(ConnectionMessages.INSERT_NUMBER_OF_PLAYERS);
                startTimer();
                desiredNumOfPlayers = is.readInt();
                stopTimer();
            } while (desiredNumOfPlayers != 2 && desiredNumOfPlayers != 3);

            send(ConnectionMessages.IS_IT_HARDCORE);
            startTimer();
            desiredHardcore = is.readBoolean();
            stopTimer();

        } catch (IOException e){
            System.err.println("Connection closed from client");
            closeRoutine();
        }

    }

    public void askMickname(){
        try {
            send(ConnectionMessages.INSERT_USERNAME);
            startTimer();
            clientNickname = is.readUTF();
            stopTimer();
        } catch (IOException e){
            System.err.println("Connection closed from client");
            closeRoutine();
        }
    }


    @Override
    public void run() {
        try{
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());

            match = server.lobby(this);
            inMatch = true;

            while(isActive()){
                Object packetFromClient = is.readObject();
                notify(packetFromClient);
            }

        }catch (IOException | ClassNotFoundException e){
            System.err.println("Connection closed from client, with message: " + e.getMessage());
        } finally {
            closeRoutine();
        }
    }

    public void closeRoutine(){
        if(inMatch){

        }else{
            if(socket.isConnected())
                send(ConnectionMessages.CONNECTION_CLOSED);
            try {
                socket.close();
            }catch (IOException e){
                System.err.println("Cannot close socket");
            }
            server.deregister(this);
        }
        active = false;
        inMatch = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isInMatch() {
        return inMatch;
    }

    public int getDesiredNumOfPlayers() {
        return desiredNumOfPlayers;
    }

    public boolean isDesiredHardcore() {
        return desiredHardcore;
    }
}
