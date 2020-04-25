package it.polimi.ingsw.view;

import it.polimi.ingsw.ServerConnectionUtils;
import it.polimi.ingsw.observe.Observable;
import it.polimi.ingsw.packets.ConnectionMessages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;

public class ConnectionToClient extends Observable<Object> implements Runnable{

    private final ServerConnectionUtils server;

    private final Socket socket;
    private String clientNickname;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private boolean active;

    private int desiredNumOfPlayers;
    private boolean desiredHardcore;


    private Thread timer;

    public ConnectionToClient(Socket socket, ServerConnectionUtils server)  {
        this.server = server;
        this.socket = socket;
        this.active = true;
        this.desiredNumOfPlayers = -1;
        this.desiredHardcore = false;
        this.clientNickname = null;
    }

    private void startTimer(int milliseconds){
        timer = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
                System.out.println("Conection [" + getClientNickname() + "]: timer is ended");
                closeRoutine();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timer.start();
    }

    private void startTimer1Min(){
        startTimer(60000);
    }
    private void startTimer2Min(){
        startTimer(120000);
    }

    private void stopTimer(){
        timer.interrupt();
    }

    private void internalSend(Object packet) throws IOException{
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in send");
            throw e;
        }
    }

    public void send(Object packet, boolean withTimer){
        try {
            if(withTimer)
                startTimer2Min();
            internalSend(packet);
        }catch (IOException e){
            closeRoutineFull();
        }
    }

    public String getClientNickname() {
        return clientNickname;
    }

    public void askForDesiredPlayersAndGamemode(){

        try {
            do {
                internalSend(ConnectionMessages.INSERT_NUMBER_OF_PLAYERS);
                startTimer1Min();
                System.out.println("Conection [" + getClientNickname() + "]: asking num of players");
                desiredNumOfPlayers = is.readInt();
                System.out.println("Conection [" + getClientNickname() + "]: got num of players, is: " + desiredNumOfPlayers);
                stopTimer();
            } while (desiredNumOfPlayers != 2 && desiredNumOfPlayers != 3);

            System.out.println("Conection [" + getClientNickname() + "]: asking gamemode");
            internalSend(ConnectionMessages.IS_IT_HARDCORE);
            startTimer1Min();
            desiredHardcore = is.readBoolean();
            System.out.println("Conection [" + getClientNickname() + "]: got gamemode, is: " + desiredHardcore);
            stopTimer();

        } catch (IOException | InputMismatchException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in ask desires");
            closeRoutineFull();
        }

    }

    private void askNickname() throws IOException{
        try {
            System.out.println("Conection: asking nickname");
            internalSend(ConnectionMessages.INSERT_NICKNAME);
            startTimer1Min();
            clientNickname = is.readUTF();
            System.out.println("Conection: nickname acquired: " + clientNickname);
            stopTimer();
        } catch (IOException | InputMismatchException e){
            System.err.println("Conection [" + getClientNickname() + "]: error in ask nick");
            throw e;
        }
    }

    public void askNicknameAgain(){
        try {
            System.out.println("Conection [" + getClientNickname() + "]: asking nick again");
            internalSend(ConnectionMessages.INVALID_NICKNAME);
            startTimer1Min();
            clientNickname = is.readUTF();
            System.out.println("Conection [" + getClientNickname() + "]: got nick again : " + clientNickname);
            stopTimer();
        } catch (IOException | InputMismatchException e){
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
    }

    public boolean isActive() {
        return active;
    }


    public int getDesiredNumOfPlayers() {
        return desiredNumOfPlayers;
    }

    public boolean isDesiredHardcore() {
        return desiredHardcore;
    }
}
