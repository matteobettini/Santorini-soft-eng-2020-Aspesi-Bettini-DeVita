package it.polimi.ingsw;

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

public class ServerImpl implements Server, ServerConnectionUtils {

    private static final int port = 4567;
    private ServerSocket serverSocket;
    private final ExecutorService executor;

    private final List<ConnectionToClient> waitingClients;
    private final List<Match> activeMatches;

    ReentrantLock lockLobby = new ReentrantLock(true);
    ReentrantLock lockMatches = new ReentrantLock(true);

    private int currMatchSize;
    private boolean currMatchHardcore;
    private int currMatchID;

    public ServerImpl(){
        this.executor = Executors.newCachedThreadPool();
        this.waitingClients = new LinkedList<>();
        this.activeMatches = new ArrayList<>();
        this.currMatchSize = -1;
        this.currMatchHardcore = false;
        this.currMatchID = 1;
    }

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
                for(ConnectionToClient c : waitingClients)
                    if(!c.isActive())
                        deregister(c);

            } else {
                assert ((currMatchSize == 2 && waitingClients.size() == 1) || (currMatchSize == 3 && (waitingClients.size() == 1 || waitingClients.size() == 2)));
                waitingClients.add(connection);

                while (alreadyTaken()) {
                    connection.askNicknameAgain();
                    for(ConnectionToClient c : waitingClients)
                        if(!c.isActive())
                            deregister(c);
                }
            }

            if(waitingClients.size() == currMatchSize){
                System.out.println("Server: creating match");
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

    public void deregister(ConnectionToClient connectionToClient){

        lockMatches.lock();
        try {
            for(Match match : activeMatches){
                if(match.contains(connectionToClient)){
                    match.notifyEnd(connectionToClient);
                    deregisterMatch(match);
                    break;
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
            if(waitingClients.size() > 0){
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
