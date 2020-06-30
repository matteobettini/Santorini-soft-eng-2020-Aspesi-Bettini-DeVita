package it.polimi.ingsw.server.communication;

import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;
import it.polimi.ingsw.common.utils.observe.Observable;
import it.polimi.ingsw.server.ServerLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class manages the connection to a client using a socket
 */
public class ConnectionToClient extends Observable<Object> implements Runnable{

    private static final String NICKNAME_REGEXP = "^([a-zA-Z0-9._\\-]{1,20})$";
    private static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEXP);
    public static final int TIMER_SHORT = 60000;
    public static final int TIMER_LONG = 240000;
    public static final int PING_PERIOD = 5000;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int TIMEOUT_BEFORE_CLOSE = 5000;

    private Observer<ConnectionToClient> nickNameChosenHandler;
    private Observer<ConnectionToClient> gameDesiresHandler;
    private Observer<ConnectionToClient> closureHandler;

    private final Socket socket;
    private ObjectOutputStream os;
    private ObjectInputStream is;

    private final AtomicBoolean active = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private boolean inMatch;

    private boolean nickAsked;
    private boolean desiresAsked;

    private String clientNickname;
    private int desiredNumOfPlayers;
    private boolean desiredHardcore;

    private final ReentrantLock sendLock = new ReentrantLock(true);

    private Thread timer;
    private final Thread pinger;

    private final Logger serverLogger = Logger.getLogger(ServerLogger.LOGGER_NAME);

    /**
     * The constructor of the connection
     * saves the client socket in a local variable,
     * saves also a reference to the server connection utils
     * @param socket the client socket it manages
     */
    public ConnectionToClient(Socket socket)  {
        this.socket = socket;
        this.inMatch = false;
        this.desiredNumOfPlayers = -1;
        this.desiredHardcore = false;
        this.clientNickname = null;
        this.nickAsked = false;
        this.desiresAsked = false;

        this.pinger = new Thread(() -> {
            while(active.get()) {
                try {
                    Thread.sleep(PING_PERIOD);
                    internalSend(ConnectionMessages.PING);
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e){
                    closeRoutine(false);
                }
            }
        });
    }

    /**
     * Starts a timer which in case it ends closes the connection
     * @param milliseconds the duration of the timer in milliseconds
     */
    private void startTimer(int milliseconds){
        timer = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
                serverLogger.info("[" + (getClientNickname() != null ? getClientNickname() : socket.getInetAddress().getHostAddress()) + "]: timer is ended");
                closeRoutine(true);
            } catch (InterruptedException ignored) { }
        });
        timer.start();
    }

    private void startTimerShorter(){ startTimer(TIMER_SHORT); }
    private void startTimerLonger(){ startTimer(TIMER_LONG); }

    /**
     * Stops an eventually running timer
     */
    public void stopTimer(){
        if(timer != null && timer.isAlive()) {
            timer.interrupt();
            timer = null;
        }
    }

    /**
     * The function used to send serializable objects to the client
     * @param packet the packet to send
     * @throws IOException in case of errors throws this exception
     */
    private void internalSend(Serializable packet) throws IOException{
        sendLock.lock();
        try {
            os.writeObject(packet);
            os.flush();
            os.reset();
        }finally {
            sendLock.unlock();
        }
    }

    /**
     * Method for sending ONLY serialized objects to the client
     * Upon failure sending the information, closes the connection
     * @param packet the serialized object
     * @param withTimer a flag to activate a longer timer that closes the connection if it doesn't receive an answer
     *                  within the prescribed time limit
     */
    public void send(Serializable packet, boolean withTimer){
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
     * and if it wants to play in hardcore mode or not, also starts a shorter timer
     * Upon failure retrieving the information, closes the connection
     */
    public void askForDesiredPlayersAndGamemode(){

        try {
           serverLogger.info("[" + getClientNickname() + "]: asking num of players and gamemode");

            desiresAsked = true;
            internalSend(ConnectionMessages.INSERT_NUMBER_OF_PLAYERS_AND_GAMEMODE);
            startTimerShorter();

        } catch (IOException e){
            closeRoutineFull();
        }

    }

    /**
     * Method used to ask the client his nickname
     * Upon failure retrieving the information, closes the connection, also starts a shorter timer
     * @throws IOException in case of errors throws this exception
     */
    private void askNickname() throws IOException{

        serverLogger.info("[" + socket.getInetAddress().getHostAddress() + "]: asking nickname");

        nickAsked = true;
        internalSend(ConnectionMessages.INSERT_NICKNAME);
        startTimerShorter();

    }

    /**
     * Method use by the server to ask the client his nickname again
     * because it has already been selected by another player
     * Upon failure retrieving the information, closes the connection,
     * also starts a shorter timer
     */
    public void askNicknameAgain(){
        try{
            serverLogger.info("[" + (clientNickname != null ? clientNickname : socket.getInetAddress().getHostAddress()) + "]: chosen nick already exists, reasking");

            nickAsked = true;
            internalSend(ConnectionMessages.TAKEN_NICKNAME);
            startTimerShorter();
        } catch (IOException e){
            closeRoutineFull();
        }
    }

    /**
     * This is the run method used by the server to start the client
     * After creating the object streams to the client,
     * it asks the desired nickname
     * It loops continuing to listen for incoming objects from the connection
     * and notifies the handlers according to the objects received
     * Upon failure of any kind it closes the connection
     */
    @Override
    public void run() {
        try{
            this.socket.setSoTimeout(CONNECTION_TIMEOUT);

            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());

            active.set(true);

            pinger.start();

            askNickname();

            while(active.get()){

                Object packetFromClient = is.readObject();

                if(!(packetFromClient == ConnectionMessages.PING)) {
                    if (inMatch)
                        handlePacketInMatch(packetFromClient);
                    else
                        handlePacketInSetup(packetFromClient);
                }
            }


        }catch (IOException | ClassNotFoundException e){
            serverLogger.log(Level.INFO, "[" + (clientNickname != null ? clientNickname : socket.getInetAddress().getHostAddress()) + "]: connection channel deactivated", e.getCause());
        }finally {
            closeRoutineFull();
        }
    }

    private void handlePacketInSetup(Object packetFromClient) throws IOException {

        if (packetFromClient instanceof PacketNickname && nickAsked) {
            stopTimer();
            serverLogger.info("[" + (clientNickname != null ? clientNickname : socket.getInetAddress().getHostAddress()) + "]: received nick");
            PacketNickname packetNickname = (PacketNickname) packetFromClient;
            if (!isNickValid(packetNickname.getNickname())){
                serverLogger.info("[" + (clientNickname != null ? clientNickname : socket.getInetAddress().getHostAddress()) + "]: invalid nickname");
                internalSend(ConnectionMessages.INVALID_NICKNAME);
                startTimerShorter();
            } else {
                nickAsked = false;
                clientNickname = packetNickname.getNickname();
                nickNameChosenHandler.update(this);
            }
        } else if (packetFromClient instanceof PacketNumOfPlayersAndGamemode && desiresAsked) {
            stopTimer();
            serverLogger.info("[" + (clientNickname != null ? clientNickname : socket.getInetAddress().getHostAddress()) + "]: received game desires");
            PacketNumOfPlayersAndGamemode packetNumOfPlayersAndGamemode = (PacketNumOfPlayersAndGamemode) packetFromClient;
            if(packetNumOfPlayersAndGamemode.getDesiredNumOfPlayers() != 2 && packetNumOfPlayersAndGamemode.getDesiredNumOfPlayers() != 3) {
                serverLogger.info("[" + (clientNickname != null ? clientNickname : socket.getInetAddress().getHostAddress()) + "]: invalid game desires");
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
     * additionally handles the full de-registration of the connection according to its state (in lobby or in match)
     */
    public synchronized void closeRoutineFull(){

        stopTimer();
        active.set(false);
        closureHandler.update(this);

        closeRoutine(false);

    }

    /**
     * This method starts the closeRoutine method on a new thread
     */
    public void asyncCloseRoutine(){
        new Thread(() -> closeRoutine(false)).start();
    }

    /**
     * This method tries to send a connection ended message and after waiting for a short time
     * tries to close the streams and the socket.
     * It is used from the timer, the pinger and the match not coupled with close routine full as
     * full de-registration is not required in these cases
     */
    private synchronized void closeRoutine(boolean timerEnded) {

        if(closed.compareAndSet(false, true)) {

            serverLogger.info("[" + (clientNickname != null ? clientNickname : socket.getInetAddress().getHostAddress()) + "]: socket is closing");

            active.set(false);
            stopTimer();

            if (timerEnded) {
                try {
                    internalSend(ConnectionMessages.TIMER_ENDED);
                } catch (IOException ignored) { }
            }

            try {
                internalSend(ConnectionMessages.CONNECTION_CLOSED);
            } catch (IOException ignored) { }

            try {
                Thread.sleep(TIMEOUT_BEFORE_CLOSE);
            } catch (InterruptedException ignored) { }

            try {
                is.close();
            } catch (IOException ignored) { }
            try {
                os.close();
            } catch (IOException ignored) { }
            try {
                socket.close();
            } catch (IOException ignored) { }
        }

    }

    /**
     * Returns the active status of the connection
     * @return the active flag
     */
    public boolean isActive() {
        return active.get();
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


