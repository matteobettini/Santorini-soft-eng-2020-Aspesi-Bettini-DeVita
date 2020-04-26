package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.ConcreteModel;
import it.polimi.ingsw.packets.ConnectionMessages;
import it.polimi.ingsw.packets.PacketMatchStarted;
import it.polimi.ingsw.view.ConnectionToClient;
import it.polimi.ingsw.view.VirtualView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Match {

    private final int id;

    private final Map<ConnectionToClient, VirtualView>  clients;
    private final Controller controller;
    private final ConcreteModel model;


    /**
     * This is the match constructor
     * After initialization, it sends a "match started" message containing all the player and
     * the gamemode to all the players
     * It creates the model, controller and virtual views involved in the match and keeps track of them
     *
     * @param clientConnections
     * @param isHardcore
     * @param id
     */
    public Match(List<ConnectionToClient> clientConnections, boolean isHardcore, int id) {
        assert(clientConnections != null);
        this.id = id;
        this.clients = new HashMap<>();
        List<String> players = clientConnections.stream().map(ConnectionToClient::getClientNickname).collect(Collectors.toList());
        this.model = new ConcreteModel(players, isHardcore);

        for(ConnectionToClient c : clientConnections){
            c.send(new PacketMatchStarted(players, isHardcore), false);
            System.out.println("Match: sending started to: " + c.getClientNickname());
            VirtualView virtualView = new VirtualView(c, model);
            this.clients.put(c, virtualView);
        }

        this.controller = new Controller(new ArrayList<>(clients.values()), model);
    }

    /**
     * Method used to start the match (start the model)
     */
    public void start(){
        model.start();
    }

    /**
     * Returns true if the given connection is playing in the match
     * @param connectionToClient the connection i want the info about
     * @return true if it is playing in the match
     */
    public boolean contains(ConnectionToClient connectionToClient){
        return clients.containsKey(connectionToClient);
    }

    /**
     * This method is used when a client closes and de-registers
     * The match sends a "match ended" message to all the other clients and
     * closes them
     * @param connectionToClient the client causing the match shutdown
     */
    public void notifyEnd(ConnectionToClient connectionToClient){
        assert(clients.containsKey(connectionToClient));
        for(ConnectionToClient c : clients.keySet()){
            if(!c.equals(connectionToClient)) {
                c.send(ConnectionMessages.MATCH_ENDED, false);
                c.closeRoutine();
            }
        }
    }

    /**
     * Returns the id of the match
     * @return the id of the match
     */
    public int getId() {
        return id;
    }

}
