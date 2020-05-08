package it.polimi.ingsw.view;

import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.PacketNickname;
import it.polimi.ingsw.packets.PacketNumOfPlayersAndGamemode;
import it.polimi.ingsw.observe.Observable;
import it.polimi.ingsw.packets.ConnectionMessages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.regex.Pattern;

public class ConnectionToClient extends Observable<Object> implements Runnable{

    private static final String NICKNAME_REGEXP = "[a-zA-Z0-9._\\-]{1,20}";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);

    private Observer<ConnectionToClient> nickNameChosenHandler;
    private Observer<ConnectionToClient> gameDesiresHandler;
    private Observer<ConnectionToClient> closureHandler;

    private final Socket socket;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private boolean active;
    private boolean inMatch;

    private boolean nickAsked;
    private boolean desiresAsked;

    private String clientNickname;
    private int desiredNumOfPlayers;
    private boolean desiredHardcore;

    private Thread timer;

    /**
     * The constructor of the connection
     * saves the client socket in a local variable
     * saves also a reference to the server connection utils
     * @param socket the client socket it manages
     */
    public ConnectionToClient(Socket socket)  {
        this.socket = socket;
        this.active = true;
        this.inMatch = false;
        this.desiredNumOfPlayers = -1;
        this.desiredHardcore = false;
        this.clientNickname = null;
        this.nickAsked = false;
        this.desiresAsked = false;

    }

    private void startTimer(int milliseconds){
        System.out.println("Connection [" + getClientNickname() + "]: timer is started");
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

    private void startTimerShorter(){ startTimer(30000); }
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
     * Method use by the server to ask the client the desired number of players in a match
     * and if it wants to play in hardcore mode or not
     * Upon failure retrieving the information, closes the connection
     */
    public void askForDesiredPlayersAndGamemode(){

        try {
            System.out.println("Connection [" + getClientNickname() + "]: asking num of players and gamemode");

            desiresAsked = true;
            internalSend(ConnectionMessages.INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE);
            startTimerShorter();

        } catch (IOException e){
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
            System.out.println("Connection: asking nickname");

            nickAsked = true;
            internalSend(ConnectionMessages.INSERT_NICKNAME);
            startTimerShorter();
        } catch (IOException e){
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
        try{
            System.out.println("Connection: asking nickname again");

            nickAsked = true;
            internalSend(ConnectionMessages.TAKEN_NICKNAME);
            startTimerShorter();
        } catch (IOException e){
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

            while(active){
                System.out.println("Connection [" + getClientNickname() + "]: I'M WAITING FOR AN OBJECT");
                Object packetFromClient = is.readObject();
                stopTimer();

                if(inMatch)
                    handlePacketInMatch(packetFromClient);
                else
                    handlePacketInSetup(packetFromClient);

            }
            System.err.println("Connection [" + getClientNickname() + "]: error i'm inactive");

        }catch (IOException | ClassNotFoundException e){
            //System.out.println("Connection [" + getClientNickname() + "]: exception in run {" + e.getMessage() + "}");
            closeRoutineFull();
        }
    }
    private void handlePacketInSetup(Object packetFromClient) throws IOException {

        if (packetFromClient instanceof PacketNickname && nickAsked) {
            System.out.println("Connection [" + getClientNickname() + "]: received nick");
            PacketNickname packetNickname = (PacketNickname) packetFromClient;
            if (!isNickValid(packetNickname.getNickname())){
                internalSend(ConnectionMessages.INVALID_NICKNAME);
                startTimerShorter();
            } else {
                nickAsked = false;
                clientNickname = packetNickname.getNickname();
                nickNameChosenHandler.update(this);
            }
        } else if (packetFromClient instanceof PacketNumOfPlayersAndGamemode && desiresAsked) {
            System.out.println("Connection [" + getClientNickname() + "]: received desires");
            PacketNumOfPlayersAndGamemode packetNumOfPlayersAndGamemode = (PacketNumOfPlayersAndGamemode) packetFromClient;
            if(packetNumOfPlayersAndGamemode.getDesiredNumOfPlayers() != 2 && packetNumOfPlayersAndGamemode.getDesiredNumOfPlayers() != 3) {
                internalSend(ConnectionMessages.INVALID_PACKET);
                startTimerShorter();
            } else{
                desiredHardcore = packetNumOfPlayersAndGamemode.isDesiredHardcore();
                desiredNumOfPlayers = packetNumOfPlayersAndGamemode.getDesiredNumOfPlayers();
                desiresAsked = false;
                gameDesiresHandler.update(this);
            }
        }
    }

    private void handlePacketInMatch(Object packetFromClient){
        notify(packetFromClient);
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
        closureHandler.update(this);

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

    private boolean isNickValid(String clientNickname){
        return clientNickname != null && NICKNAME_PATTERN.matcher(clientNickname).matches();
    }

    /**
     * Returns the client nickname
     * @return the client nickname, null if it hasn't been chosen yet
     */
    public String getClientNickname() {
        return clientNickname;
    }

    public void setNickNameChosenHandler(Observer<ConnectionToClient> nickNameChosenHandler) {
        this.nickNameChosenHandler = nickNameChosenHandler;
    }

    public void setGameDesiresHandler(Observer<ConnectionToClient> gameDesiresHandler) {
        this.gameDesiresHandler = gameDesiresHandler;
    }

    public void setClosureHandler(Observer<ConnectionToClient> closureHandler) {
        this.closureHandler = closureHandler;
    }

    public void setInMatch(boolean inMatch) {
        this.inMatch = inMatch;
    }

}


