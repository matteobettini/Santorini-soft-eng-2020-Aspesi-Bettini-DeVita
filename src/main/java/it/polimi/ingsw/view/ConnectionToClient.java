package it.polimi.ingsw.view;

import it.polimi.ingsw.server.ServerConnectionUtils;
import it.polimi.ingsw.observe.Observable;
import it.polimi.ingsw.packets.ConnectionMessages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;

public class ConnectionToClient extends Observable<Object> implements Runnable{
    private static final int MAX_NICK_LENGTH = 20;

    private final ServerConnectionUtils server;

    private final Socket socket;
    private String clientNickname;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private boolean active;

    private int desiredNumOfPlayers;
    private boolean desiredHardcore;


    private Thread timer;

    /**
     * The constructor of the connection
     * saves the client socket in a local variable
     * saves also a reference to the server connection utils
     * @param socket the client socket it manages
     * @param server a pointer to the server interface
     */
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
                System.out.println("Connection [" + getClientNickname() + "]: timer is ended");
                closeRoutine(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        timer.start();
    }

    private void startTimerShorter(){
        startTimer(30000);
    }
    private void startTimerLonger(){
        startTimer(40000);
    }

    private void stopTimer(){
        if(timer != null && timer.isAlive()) {
            timer.interrupt();
            timer = null;
        }
    }

    private void internalSend(Object packet) throws IOException{
        try {
            os.writeObject(packet);
            os.flush();
        }catch (IOException e){
            System.err.println("Connection [" + getClientNickname() + "]: error in send");
            throw e;
        }
    }

    /**
     * Method for sending ONLY serialized objects to the client
     * Upon failure sending the information, closes the connection
     * @param packet the serialized object
     * @param withTimer a flag to activate a timer that closes the connection if it doesn't receive an answer
     *                  within the prescribed time limit
     */
    public void send(Object packet, boolean withTimer){
        try {
            if(withTimer)
                startTimerLonger();
            internalSend(packet);
        }catch (IOException e){
            closeRoutineFull();
        }
    }

    /**
     * Returns the client nickname
     * @return the client nickname, null if it hasn't been chosen yet
     */
    public String getClientNickname() {
        return clientNickname;
    }


    /**
     * Method use by the server to ask the client the desired number of players in a match
     * and if it wants to play in hardcore mode or not
     * Upon failure retrieving the information, closes the connection
     */
    public void askForDesiredPlayersAndGamemode(){

        try {
            do {
                internalSend(ConnectionMessages.INSERT_NUMBER_OF_PLAYERS);
                startTimerShorter();
                System.out.println("Connection [" + getClientNickname() + "]: asking num of players");
                desiredNumOfPlayers = is.readInt();
                System.out.println("Connection [" + getClientNickname() + "]: got num of players, is: " + desiredNumOfPlayers);
                stopTimer();
            } while (desiredNumOfPlayers != 2 && desiredNumOfPlayers != 3);

            System.out.println("Connection [" + getClientNickname() + "]: asking gamemode");
            internalSend(ConnectionMessages.IS_IT_HARDCORE);
            startTimerShorter();
            desiredHardcore = is.readBoolean();
            System.out.println("Connection [" + getClientNickname() + "]: got gamemode, is: " + desiredHardcore);
            stopTimer();

        } catch (IOException | InputMismatchException e){
            System.err.println("Connection [" + getClientNickname() + "]: error in ask desires");
            closeRoutineFull();
        }

    }

    /**
     * Method used to ask the client his nickname
     * Upon failure retrieving the information, closes the connection
     */
    private void askNickname() throws IOException{
        try {
            do {
                System.out.println("Connection: asking nickname");
                internalSend(ConnectionMessages.INSERT_NICKNAME);
                startTimerShorter();
                clientNickname = is.readUTF();
                System.out.println("Connection: nickname acquired: " + clientNickname);
                stopTimer();
            }while(clientNickname == null ||  clientNickname.length() < 1 || clientNickname.length() > MAX_NICK_LENGTH || clientNickname.contains("\n") || clientNickname.contains(" "));
        } catch (IOException | InputMismatchException e){
            System.err.println("Connection [" + getClientNickname() + "]: error in ask nick");
            throw e;
        }
    }

    /**
     * Method use by the server to ask the client his nickname again
     * because it has already been selected by another player
     * Upon failure retrieving the information, closes the connection
     */
    public void askNicknameAgain(){
        try {
            do {
                System.out.println("Connection [" + getClientNickname() + "]: asking nick again");
                internalSend(ConnectionMessages.INSERT_NICKNAME);
                startTimerShorter();
                clientNickname = is.readUTF();
                System.out.println("Connection [" + getClientNickname() + "]: got nick again : " + clientNickname);
                stopTimer();
            }while(clientNickname == null ||  clientNickname.length() < 1 || clientNickname.length() > MAX_NICK_LENGTH || clientNickname.contains("\n") || clientNickname.contains(" "));
        } catch (IOException | InputMismatchException e){
            System.err.println("Connection [" + getClientNickname() + "]: error in ask nick again");
            closeRoutineFull();
        }
    }

    /**
     * This is the run method used by the server to start the client
     * After creating the object streams to the client,
     * it asks the desired nickname
     * Then it calls the lobby method to put the client in the waiting list (lobby) on the server
     * If the client is inserted successfully and/or a match is created
     * it loops continuing to listen for incoming objects from the connection
     * Upon failure of any kind it closes the connection
     */
    @Override
    public void run() {
        try{
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());

            askNickname();

            server.lobby(this);

            while(active){
                System.out.println("Connection [" + getClientNickname() + "]: I'M WAITING FOR AN OBJECT");
                Object packetFromClient = is.readObject();
                stopTimer();
                notify(packetFromClient);
            }
            System.err.println("Connection [" + getClientNickname() + "]: error i'm inactive");

        }catch (IOException | ClassNotFoundException e){
            //System.out.println("Connection [" + getClientNickname() + "]: exception in run {" + e.getMessage() + "}");
            closeRoutineFull();
        }
    }


    /**
     * This method calls the closeRoutine method,
     * additionally it calls the deregister method on the server
     * which handles the full de-registration of the connection according to its state
     */
    public synchronized void closeRoutineFull(){

        System.err.println("Connection [" + getClientNickname() + "]: closing socket");
        stopTimer();
        active = false;
        server.deregister(this);

        closeRoutine();

    }

    /**
     * This method tries to send a connection ended message and
     * then tries to close the streams and the socket
     * It is used from the timer and the match when full
     * de-registration is not required
     */
    public synchronized void closeRoutine(){
        closeRoutine(false);
    }


    private synchronized void closeRoutine(boolean timerEnded){

        active = false;
        stopTimer();
        try {
            if(timerEnded){
                os.writeObject(ConnectionMessages.TIMER_ENDED);
                os.flush();
            }
        }catch (IOException ignored){ }

        try{
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

    }

    /**
     * Returns the active status of the connection
     * @return the active flag
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the desired number of players of the client
     * @return the desired number of players
     */
    public int getDesiredNumOfPlayers() {
        return desiredNumOfPlayers;
    }

    /**
     * Returns the desired number gamemode of the client
     * @return true if hardcore is desired
     */
    public boolean isDesiredHardcore() {
        return desiredHardcore;
    }

}
