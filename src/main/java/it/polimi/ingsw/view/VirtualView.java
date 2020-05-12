package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ObservableModel;
import it.polimi.ingsw.utils.observe.Observer;
import it.polimi.ingsw.packets.*;

import java.util.ArrayList;
import java.util.List;


public class VirtualView implements Observer<Object> {

    private final ConnectionToClient connectionToClient;

    private final List<Observer<PacketMove>> packetMoveObservers;
    private final List<Observer<PacketBuild>> packetBuildObservers;
    private final List<Observer<PacketCardsFromClient>> packetCardsFromClientObservers;
    private final List<Observer<PacketStartPlayer>> packetStartPlayerObservers;
    private final List<Observer<PacketWorkersPositions>> packetWorkersPositionsObservers;


    /**
     * This is the constructor for the Virtual View
     * The virtual view subscribes to all the events notified
     * by the model and sends messages to its connection to client
     * accordingly to filtering and sending policies
     * @param connectionToClient the connection to client associated to the virtual view (the association is 1:1)
     * @param model the observable model to which it subscribes
     */
    public VirtualView(ConnectionToClient connectionToClient, ObservableModel model){
        this.connectionToClient = connectionToClient;

        this.packetBuildObservers = new ArrayList<>();
        this.packetMoveObservers = new ArrayList<>();
        this.packetCardsFromClientObservers = new ArrayList<>();
        this.packetStartPlayerObservers = new ArrayList<>();
        this.packetWorkersPositionsObservers = new ArrayList<>();

        connectionToClient.addObserver(this);

        model.addPacketCardsFromServerObserver((packetCardsFromServer) -> {
            boolean withTimer = false;
            if(packetCardsFromServer.getTo().equals(connectionToClient.getClientNickname()))
                withTimer = true;
            connectionToClient.send(packetCardsFromServer, withTimer);

        });
        model.addPacketDoActionObserver((packetDoAction) -> {
            boolean withTimer = false;
            if(packetDoAction.getTo().equals(connectionToClient.getClientNickname()))
                withTimer = true;
            connectionToClient.send(packetDoAction, withTimer);

        });
        model.addPacketPossibleBuildsObserver((packetPossibleBuilds)-> {
            if (packetPossibleBuilds.getTo().equals(connectionToClient.getClientNickname())) {
                connectionToClient.send(packetPossibleBuilds, false);
            }
        });
        model.addPacketPossibleMovesObserver((packetPossibleMoves)-> {
            if (packetPossibleMoves.getTo().equals(connectionToClient.getClientNickname()))
                connectionToClient.send(packetPossibleMoves, false);
        });
        model.addPacketSetupObserver(packetSetup -> connectionToClient.send(packetSetup, false));
        model.addPacketUpdateBoardObserver(packetUpdateBoard -> connectionToClient.send(packetUpdateBoard, false));

    }

    /**
     * Update method called upon arrival of a packet to the connection to client associated
     * @param packetFromClient the packet arriving from the connection
     */
    @Override
    public void update(Object packetFromClient) {
        if(packetFromClient instanceof PacketMove){
            PacketMove packetMove = (PacketMove) packetFromClient;
            if(!packetMove.isSimulate())
                connectionToClient.stopTimer();
            notifyPacketMoveObservers(packetMove);
        }else if(packetFromClient instanceof PacketBuild) {
            PacketBuild packetBuild = (PacketBuild) packetFromClient;
            if(!packetBuild.isSimulate())
                connectionToClient.stopTimer();
            notifyPacketBuildObservers(packetBuild);
        }else if(packetFromClient instanceof PacketStartPlayer) {
            connectionToClient.stopTimer();
            notifyPacketStartPlayerObservers((PacketStartPlayer) packetFromClient);
        }else if(packetFromClient instanceof PacketCardsFromClient) {
            connectionToClient.stopTimer();
            notifyPacketCardsFromClientObservers((PacketCardsFromClient) packetFromClient);
        }else if(packetFromClient instanceof PacketWorkersPositions){
            connectionToClient.stopTimer();
            notifyPacketWorkersPositionsObservers((PacketWorkersPositions) packetFromClient);
        }else{
            assert false;
            sendInvalidPacketMessage();
        }
    }

    /**
     * This method sends an invalid packet answer to the client
     */
    public void sendInvalidPacketMessage(){
        connectionToClient.send(ConnectionMessages.INVALID_PACKET, true);
    }

    /**
     * Returns the associated client nickname
     * @return  the client nickname
     */
    public String getClientNickname(){
        return connectionToClient.getClientNickname();
    }

    public void addPacketMoveObserver(Observer<PacketMove> o){
        this.packetMoveObservers.add(o);
    }
    public void addPacketBuildObserver(Observer<PacketBuild> o){
        this.packetBuildObservers.add(o);
    }
    public void addPacketCardsFromClientObserver(Observer<PacketCardsFromClient> o){
        this.packetCardsFromClientObservers.add(o);
    }
    public void addPacketStartPlayerObserver(Observer<PacketStartPlayer> o){
        this.packetStartPlayerObservers.add(o);
    }
    public void addPacketWorkersPositionsObserver(Observer<PacketWorkersPositions> o){
        this.packetWorkersPositionsObservers.add(o);
    }

    public void notifyPacketMoveObservers(PacketMove p){
        for(Observer<PacketMove> o : packetMoveObservers){
            o.update(p);
        }
    }
    public void notifyPacketBuildObservers(PacketBuild p){
        for(Observer<PacketBuild> o : packetBuildObservers){
            o.update(p);
        }
    }
    public void notifyPacketCardsFromClientObservers(PacketCardsFromClient p){
        for(Observer<PacketCardsFromClient> o : packetCardsFromClientObservers){
            o.update(p);
        }
    }
    public void notifyPacketStartPlayerObservers(PacketStartPlayer p){
        for(Observer<PacketStartPlayer> o : packetStartPlayerObservers){
            o.update(p);
        }
    }
    public void notifyPacketWorkersPositionsObservers(PacketWorkersPositions p){
        for(Observer<PacketWorkersPositions> o : packetWorkersPositionsObservers){
            o.update(p);
        }
    }
}
