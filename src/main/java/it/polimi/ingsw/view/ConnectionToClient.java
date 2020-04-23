package it.polimi.ingsw.view;

import it.polimi.ingsw.ServerConnectionUtils;
import it.polimi.ingsw.observe.Observable;
import it.polimi.ingsw.packets.ConnectionMessages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionToClient extends Observable<Object> implements Runnable{

    private final ServerConnectionUtils server;

    private final Socket socket;
    private String clientNickname;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private boolean active;
    private boolean inMatch;

    private int desiredNumOfPlayers;
    private boolean desiredHardcore;


    private Thread timer;

    public ConnectionToClient(Socket socket, ServerConnectionUtils server)  {
        this.server = server;
        this.socket = socket;
        this.active = true;
        this.inMatch = false;
        this.desiredNumOfPlayers = -1;
        this.desiredHardcore = false;
        this.clientNickname = null;
    }

    public void startTimer(){
        timer = new Thread(() -> {
            try {
                Thread.sleep(120000);
                System.out.println("Conection [" + getClientNickname() + "]: timer is ended");
                closeRoutine();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timer.start();
    }

    private void stopTimer(){
        timer.interrupt();
    }

    public void send(Object packet) throws IOException{
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in send");
            throw e;
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
                System.out.println("Conection [" + getClientNickname() + "]: asking num of players");
                desiredNumOfPlayers = is.readInt();
                System.out.println("Conection [" + getClientNickname() + "]: got num of players, is: " + desiredNumOfPlayers);
                stopTimer();
            } while (desiredNumOfPlayers != 2 && desiredNumOfPlayers != 3);

            System.out.println("Conection [" + getClientNickname() + "]: asking gamemode");
            send(ConnectionMessages.IS_IT_HARDCORE);
            startTimer();
            desiredHardcore = is.readBoolean();
            System.out.println("Conection [" + getClientNickname() + "]: got gamemode, is: " + desiredHardcore);
            stopTimer();

        } catch (IOException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in ask desires");
            closeRoutineFull();
        }

    }

    private void askNickname() throws IOException{
        try {
            System.out.println("Conection: asking nickname");
            send(ConnectionMessages.INSERT_NICKNAME);
            startTimer();
            clientNickname = is.readUTF();
            System.out.println("Conection: nickname acquired: " + clientNickname);
            stopTimer();
        } catch (IOException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in ask nick");
            throw e;
        }
    }

    public void askNicknameAgain(){
        try {
            System.out.println("Conection [" + getClientNickname() + "]: asking nick again");
            send(ConnectionMessages.INVALID_NICKNAME);
            startTimer();
            clientNickname = is.readUTF();
            System.out.println("Conection [" + getClientNickname() + "]: got nick again : " + clientNickname);
            stopTimer();
        } catch (IOException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in ask nick again");
            closeRoutineFull();
        }
    }


    @Override
    public void run() {
        try{
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());

            askNickname();

            server.lobby(this);

            while(isActive()){
                Object packetFromClient = is.readObject();
                stopTimer();
                notify(packetFromClient);
            }

        }catch (IOException | ClassNotFoundException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in run {" + e.getMessage() + "}");
            closeRoutineFull();
        }
    }

    public void setInMatch() {
        assert !inMatch;
        inMatch = true;
    }

    public synchronized void closeRoutineFull(){

        System.err.println("Conection [" + getClientNickname() + "]: closing socket");
        active = false;
        server.deregister(this);

        closeRoutine();
    }

    public synchronized void closeRoutine(){

        try {
            os.writeObject(ConnectionMessages.CONNECTION_CLOSED);
            os.flush();
        }catch (IOException ignored){ }

        try {
            is.close();
        }catch (IOException ignored){}
        try {
            os.close();
        }catch (IOException ignored){}
        try{
            socket.close();
        }catch (IOException ignored){ }

        active = false;
        inMatch = false;
    }

    public boolean isActive() {
        return active;
    }

    public boolean inMatch() {
        return inMatch;
    }

    public int getDesiredNumOfPlayers() {
        return desiredNumOfPlayers;
    }

    public boolean isDesiredHardcore() {
        return desiredHardcore;
    }
}
