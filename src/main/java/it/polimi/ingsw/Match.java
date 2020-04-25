package it.polimi.ingsw;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.ConcreteModel;
import it.polimi.ingsw.packets.ConnectionMessages;
import it.polimi.ingsw.packets.PacketMatchStarted;
import it.polimi.ingsw.view.ConnectionToClient;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Match {

    private final int id;
    private boolean active;

    private final Map<ConnectionToClient, VirtualView>  clients;
    private final Controller controller;
    private final ConcreteModel model;

    public Match(List<ConnectionToClient> clientConnections, boolean isHardcore, int id) {
        assert(clientConnections != null);
        this.active = false;
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

    public void start(){
        this.active = true;
        model.start();
    }

    public boolean contains(ConnectionToClient connectionToClient){
        return clients.containsKey(connectionToClient);
    }

    public void notifyEnd(ConnectionToClient connectionToClient){
        assert(clients.containsKey(connectionToClient));
        for(ConnectionToClient c : clients.keySet()){
            if(!c.equals(connectionToClient)) {
                c.send(ConnectionMessages.MATCH_ENDED, false);
                c.closeRoutine();
            }
        }
    }


    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }
}
