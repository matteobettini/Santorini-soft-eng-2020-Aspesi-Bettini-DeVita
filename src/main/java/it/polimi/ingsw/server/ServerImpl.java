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
import java.util.stream.Collectors;

public class ServerImpl implements Server {

    private final int port;
    private ServerSocket serverSocket;
    private final ExecutorService executor;

    private List<ConnectionToClient> waitingClients;
    private final List<Match> activeMatches;

    ReentrantLock lockLobby = new ReentrantLock(true);
    ReentrantLock lockMatches = new ReentrantLock(true);

    private int currMatchSize;
    private boolean currMatchHardcore;
    private int currMatchID;

    private ServerPhase serverPhase;
    private ConnectionToClient whoIAmCurrentlyWaiting;

    /**
     * The constructor initializes the variables
     */
    public ServerImpl(int port) {
        this.executor = Executors.newCachedThreadPool();
        this.waitingClients = new LinkedList<>();
        this.activeMatches = new ArrayList<>();
        this.currMatchSize = -1;
        this.currMatchHardcore = false;
        this.currMatchID = 1;
        this.port = port;
        this.serverPhase = ServerPhase.EMPTY_LOBBY;
        this.whoIAmCurrentlyWaiting = null;
    }

    /**
     * This method creates a server socket
     * and then loops continuing to accept incoming connections
     * and assigning them to a thread in the thread pool
     *
     * @throws IOException when it occurs during server shutdown
     */
    public void startServer() throws IOException {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("Server: server is ready");
            while (true) {
                System.out.println("=======================");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server: received connection");
                ConnectionToClient clientConnection = new ConnectionToClient(clientSocket);

                clientConnection.setNickNameChosenHandler(this::handleNickChosen);
                clientConnection.setGameDesiresHandler(this::handleDesires);
                clientConnection.setClosureHandler(this::deregister);

                executor.submit(clientConnection);
            }
        } catch (IOException e) {
            System.err.println("Sever: server socket has problems, message: " + e.getMessage());
            serverSocket.close();
        }
    }

    private void handleNickChosen(ConnectionToClient connection) {

        if (serverPhase == ServerPhase.WAITING_USERNAME_REINSERTION && whoIAmCurrentlyWaiting.equals(connection)) {
            if (alreadyTaken(connection))
                connection.askNicknameAgain();
            else {
                setNextLobbyPhase();
            }
        } else {

            lockLobby.lock();
            try {
                if(!waitingClients.contains(connection)) {
                    waitingClients.add(connection);
                    if(serverPhase != ServerPhase.WAITING_DESIRES && serverPhase != ServerPhase.WAITING_USERNAME_REINSERTION)
                        setNextLobbyPhase();
                }
            } finally {
                lockLobby.unlock();
            }

        }
    }

    private void handleDesires(ConnectionToClient connection) {
        if (serverPhase == ServerPhase.WAITING_DESIRES && whoIAmCurrentlyWaiting.equals(connection)) {
            lockLobby.lock();
            try {
                currMatchSize = connection.getDesiredNumOfPlayers();
                currMatchHardcore = connection.isDesiredHardcore();

                setNextLobbyPhase();

            } finally {
                lockLobby.unlock();
            }
        }
    }


    private void setNextLobbyPhase() {
        lockLobby.lock();
        try {

            whoIAmCurrentlyWaiting = null;

            for(int i = 1; i < currMatchSize && i < waitingClients.size(); i++){
                if(alreadyTaken(waitingClients.get(i))){
                    serverPhase = ServerPhase.WAITING_USERNAME_REINSERTION;
                    whoIAmCurrentlyWaiting = waitingClients.get(i);
                    whoIAmCurrentlyWaiting.askNicknameAgain();
                    return;
                }
            }

            if (waitingClients.size() == 0) {
                serverPhase = ServerPhase.EMPTY_LOBBY;
                currMatchSize = -1;
                currMatchHardcore = false;
            } else if (currMatchSize == -1) {
                serverPhase = ServerPhase.WAITING_DESIRES;
                whoIAmCurrentlyWaiting = waitingClients.get(0);
                whoIAmCurrentlyWaiting.askForDesiredPlayersAndGamemode();
            } else if (waitingClients.size() >= currMatchSize) {
                System.out.println("Server: creating match ");
                createMatch();
            } else{
                serverPhase = ServerPhase.FILLING_LOBBY;
            }

        } finally {
            lockLobby.unlock();
        }
    }


    private void createMatch() {
        lockLobby.lock();
        try {

            Match match = new Match(waitingClients.subList(0, currMatchSize), currMatchHardcore, currMatchID);
            match.setClosureHandler(this::deregisterMatch);

            lockMatches.lock();
            try {
                activeMatches.add(match);
            } finally {
                lockMatches.unlock();
            }

            currMatchID++;

            waitingClients = waitingClients.stream().filter(x -> waitingClients.indexOf(x) >= currMatchSize).collect(Collectors.toList());

            currMatchSize = -1;
            currMatchHardcore = false;

            setNextLobbyPhase();

            match.start();

        } finally {
            lockLobby.unlock();
        }
    }

    /**
     * This methods de-registers the selected client from the server
     * First it looks in the existing matches if the client is present and if it is it sends the termination
     * signal to that match and removes it from the active matches
     * Then it looks if the client is in the lobby and, if it is,
     * the client is removed from the lobby
     *
     * @param connectionToClient the client to be de-registered
     */
    private void deregister(ConnectionToClient connectionToClient) {
        lockLobby.lock();
        try {
            if (waitingClients.contains(connectionToClient)) {
                waitingClients.remove(connectionToClient);
                if(whoIAmCurrentlyWaiting == null || whoIAmCurrentlyWaiting.equals(connectionToClient))
                    setNextLobbyPhase();
            }
        } finally {
            lockLobby.unlock();
        }
    }

    private void deregisterMatch(Match match) {
        lockMatches.lock();
        try {
            assert activeMatches.contains(match);
            activeMatches.remove(match);
        } finally {
            lockMatches.unlock();
        }
    }

    private boolean alreadyTaken(ConnectionToClient connection) {
        lockLobby.lock();
        try {
            if (waitingClients.size() > 1) {
                for (ConnectionToClient waitingClient : waitingClients) {
                    if (waitingClient.equals(connection))
                        return false;
                    else if (waitingClient.getClientNickname().equals(connection.getClientNickname()))
                        return true;
                }
            }
            return false;
        } finally {
            lockLobby.unlock();
        }
    }

}
