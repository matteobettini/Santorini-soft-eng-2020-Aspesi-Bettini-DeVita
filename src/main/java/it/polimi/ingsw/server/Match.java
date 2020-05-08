package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.ConcreteModel;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.ConnectionMessages;
import it.polimi.ingsw.packets.PacketMatchStarted;
import it.polimi.ingsw.view.ConnectionToClient;
import it.polimi.ingsw.view.VirtualView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
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


    /**
     * This is the match constructor
     * After initialization, it sends a "match started" message containing all the player and
     * the gamemode to all the players
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

        for(ConnectionToClient c : clientConnections){

            VirtualView virtualView = new VirtualView(c, model);

            virtualView.setGameFinishedHandler(winningPlayer -> {
                if(isClosing.compareAndSet(false, true)) {
                    notifyEnd();
                    closureHandler.update(this);
                }
            });
            this.clients.put(c, virtualView);

            c.setInMatch(true);
            c.setClosureHandler((connection) -> {
                if(isClosing.compareAndSet(false, true)) {
                    notifyBrutalEnd(connection);
                    closureHandler.update(this);
                }
            });
        }

        this.controller = new Controller(new ArrayList<>(clients.values()), model);
    }

    /**
     * Method used to start the match (start the model)
     */
    void start(){

        for(ConnectionToClient c : clients.keySet()){
            if(!isClosing.get()) {
                c.send(new PacketMatchStarted(players, isHardcore), false);
                System.out.println("Match: sending started to: " + c.getClientNickname());
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
                    c.send(ConnectionMessages.MATCH_INTERRUPTED, false);
                    c.closeRoutine();
                }
            }
    }


    private void notifyEnd(){
        for(ConnectionToClient c : clients.keySet()){
            c.send(ConnectionMessages.MATCH_FINISHED, false);
            c.closeRoutine();
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
