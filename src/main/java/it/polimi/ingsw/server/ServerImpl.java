package it.polimi.ingsw.server;

import it.polimi.ingsw.view.ConnectionToClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

class ServerImpl implements Server, ServerConnectionUtils {

    private static final int port = 4567;
    private ServerSocket serverSocket;
    private final ExecutorService executor;

    private final List<ConnectionToClient> waitingClients;
    private final List<Match> activeMatches;
    private final List<ConnectionToClient> toDeregister;

    ReentrantLock lockLobby = new ReentrantLock(true);
    ReentrantLock lockMatches = new ReentrantLock(true);

    private int currMatchSize;
    private boolean currMatchHardcore;
    private int currMatchID;

    /**
     * The constructor initializes the variables
     */
    public ServerImpl(){
        this.executor = Executors.newCachedThreadPool();
        this.waitingClients = new LinkedList<>();
        this.activeMatches = new ArrayList<>();
        this.toDeregister = new ArrayList<>();
        this.currMatchSize = -1;
        this.currMatchHardcore = false;
        this.currMatchID = 1;
    }

    /**
     * This method creates a server socket
     * and then loops continuing to accept incoming connections
     * and assigning them to a thread in the thread pool
     * @throws IOException when it occurs during server shutdown
     */
    public void startServer() throws IOException {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("Server: server is ready");
            while(true){
                System.out.println("=======================");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server: recieved connection");
                ConnectionToClient clientConnection = new ConnectionToClient(clientSocket, this);
                executor.submit(clientConnection);
            }
        } catch (IOException e) {
            System.err.println("Sever: server socket has problems, message: " + e.getMessage());
            serverSocket.close();
        }
    }

    /**
     * This method puts the selected client into the lobby
     * If the lobby is empty it asks the desired num of players and the gamemode
     * If the lobby is not empty it keeps asking a new username till a valid one is obtained
     * When the lobby size reaches the desired match size, a new match is created and started
     * If anything fails de-registration is handled
     * @param connection the connection to put in the lobby
     */
    public void lobby(ConnectionToClient connection){
        lockLobby.lock();
        try {
            //System.out.println("Server: Entering lobby method");

            if (waitingClients.size() == 0) {
                assert (currMatchSize == -1);
                waitingClients.add(connection);
                //System.out.println("Server: added to waiting list");
                //System.out.println("Server: Asking for size and gamemode");
                connection.askForDesiredPlayersAndGamemode();
                currMatchSize = connection.getDesiredNumOfPlayers();
                currMatchHardcore = connection.isDesiredHardcore();

            } else {
                assert ((currMatchSize == 2 && waitingClients.size() == 1) || (currMatchSize == 3 && (waitingClients.size() == 1 || waitingClients.size() == 2)));
                waitingClients.add(connection);

                while (alreadyTaken()) {
                    connection.askNicknameAgain();
                    toDeregister.clear();
                    for(ConnectionToClient c : waitingClients) {
                        if (!c.isActive()) {
                            toDeregister.add(c);
                        }
                    }
                    for(ConnectionToClient c : toDeregister){
                        deregister(c);
                    }
                }
            }

            if(waitingClients.size() == currMatchSize){
                System.out.println("Server: creating match with thread of: " + connection.getClientNickname());
                createMatch();
            }

        }finally {
            lockLobby.unlock();
        }
    }

    private void createMatch(){
        lockLobby.lock();
        try {
            Match match = new Match(waitingClients, currMatchHardcore, currMatchID);
            lockMatches.lock();
            try {
                activeMatches.add(match);
            }finally {
                lockMatches.unlock();
            }
            waitingClients.clear();
            currMatchID++;
            currMatchSize = -1;
            currMatchHardcore = false;
            match.start();
        }finally {
            lockLobby.unlock();
        }
    }

    /**
     * This methods de-registers the selected client from the server
     * First it looks in the existing matches if the client is present and if it is it sends the termination
     * signal to that match and removes it from the active matches
     * Then it looks if the client is in the lobby and, if it is,
     * the client is removed from the lobby
     * @param connectionToClient the client to be de-registered
     */
    public void deregister(ConnectionToClient connectionToClient){

        lockMatches.lock();
        try {
            for(Match match : activeMatches){
                if(match.contains(connectionToClient)){
                    match.notifyEnd(connectionToClient);
                    deregisterMatch(match);
                    return;
                }
            }
        }finally {
            lockMatches.unlock();
        }

        lockLobby.lock();
        try {
            if (waitingClients.contains(connectionToClient)) {
                waitingClients.remove(connectionToClient);
                if (waitingClients.size() == 0) {
                    currMatchSize = -1;
                    currMatchHardcore = false;
                }
            }
        }finally {
            lockLobby.unlock();
        }
    }

    private void deregisterMatch(Match match){
        lockMatches.lock();
        try {
            assert activeMatches.contains(match);
            activeMatches.remove(match);
        }finally {
            lockMatches.unlock();
        }
    }

    private boolean alreadyTaken(){
        lockLobby.lock();
        try{
            if(waitingClients.size() > 1){
                for(int i = 0; i < waitingClients.size()-1; i++ ){
                    if(waitingClients.get(i).getClientNickname().equals(waitingClients.get(waitingClients.size()-1).getClientNickname())){
                        return true;
                    }
                }
            }
            return false;
        }finally {
            lockLobby.unlock();
        }
    }
}
