package it.polimi.ingsw.server.communication;

import it.polimi.ingsw.server.ServerLogger;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.ConcreteModel;
import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.ConnectionMessages;
import it.polimi.ingsw.common.packets.PacketMatchStarted;
import it.polimi.ingsw.server.virtualView.ConnectionToClient;
import it.polimi.ingsw.server.virtualView.VirtualView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class Match {

    private final int id;

    private Observer<Match> closureHandler;

    private final Map<ConnectionToClient, VirtualView>  clients;
    private final Controller controller;
    private final ConcreteModel model;

    private final List<String> players;
    private final boolean isHardcore;

    private final AtomicBoolean isClosing = new AtomicBoolean();

    private final Logger serverLogger = Logger.getLogger(ServerLogger.LOGGER_NAME);

    /**
     * This is the match constructor
     * Sets the handler for the end of the match and for
     * the disconnection of the clients.
     * It creates the model, controller and virtual views involved in the match and keeps track of them
     *
     * @param clientConnections the client connections in the match
     * @param isHardcore the match gamemode
     * @param id the match id
     */
    Match(List<ConnectionToClient> clientConnections, boolean isHardcore, int id) {
        assert(clientConnections != null);

        this.isClosing.set(false);
        this.id = id;
        this.clients = new HashMap<>();
        List<String> players = clientConnections.stream().map(ConnectionToClient::getClientNickname).collect(Collectors.toList());
        this.players = new ArrayList<>(players);
        this.isHardcore = isHardcore;
        this.model = new ConcreteModel(players, isHardcore);

        this.model.setGameFinishedHandler(winnerName -> {
            if(isClosing.compareAndSet(false, true)) {
                notifyEnd();
                closureHandler.update(this);
            }
        });

        for(ConnectionToClient c : clientConnections){

            VirtualView virtualView = new VirtualView(c, model);

            this.clients.put(c, virtualView);

            c.setInMatch(true);
            c.setClosureHandler((connection) -> {
                if(isClosing.compareAndSet(false, true)) {
                    serverLogger.info("[" + connection.getClientNickname() + "]: deregistering from match, MATCH ID [" + id + "]");
                    notifyBrutalEnd(connection);
                    closureHandler.update(this);
                }
            });
        }

        this.controller = new Controller(new ArrayList<>(clients.values()), model);
    }

    /**
     * Method used to start the match (start the model)
     * Sends a message to all the players with initial
     * info regarding the match
     */
    void start(){

        for(ConnectionToClient c : clients.keySet()){
            if(!isClosing.get()) {
                serverLogger.info("[" + c.getClientNickname() + "]: sending info on started match, MATCH ID: [" + id + "]");
                c.send(new PacketMatchStarted(players, isHardcore), false);
            }
        }

        model.start();
    }

    /**
     * This method is used when a client closes and de-registers
     * The match sends a "match ended" message to all the other clients and
     * closes them
     * @param connectionToClient the client causing the match shutdown
     */
    private void notifyBrutalEnd(ConnectionToClient connectionToClient){
        assert(clients.containsKey(connectionToClient));
            for(ConnectionToClient c : clients.keySet()){
                if(!c.equals(connectionToClient)) {
                    serverLogger.info("[" + c.getClientNickname() + "]: sending info of interruption of match, MATCH ID: [" + id + "]");
                    c.send(ConnectionMessages.MATCH_INTERRUPTED, false);
                    c.asyncCloseRoutine();
                }
            }
    }


    private void notifyEnd(){
        for(ConnectionToClient c : clients.keySet()){
            serverLogger.info("[" + c.getClientNickname() + "]: sending info of finished match, MATCH ID: [" + id + "]");
            c.send(ConnectionMessages.MATCH_FINISHED, false);
            c.asyncCloseRoutine();
        }
    }

    void setClosureHandler(Observer<Match> closureHandler) {
        this.closureHandler = closureHandler;
    }

    /**
     * Returns the id of the match
     * @return the id of the match
     */
    public int getId() {
        return id;
    }

}
