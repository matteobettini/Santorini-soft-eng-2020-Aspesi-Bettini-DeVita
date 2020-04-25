package it.polimi.ingsw.view;

import it.polimi.ingsw.model.ObservableModel;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class VirtualView implements Observer<Object> {

    private final ConnectionToClient connectionToClient;

    private final List<Observer<PacketMove>> packetMoveObservers;
    private final List<Observer<PacketBuild>> packetBuildObservers;
    private final List<Observer<PacketCardsFromClient>> packetCardsFromClientObservers;
    private final List<Observer<PacketStartPlayer>> packetStartPlayerObservers;
    private final List<Observer<PacketWorkersPositions>> packetWorkersPositionsObservers;

    public VirtualView(ConnectionToClient connectionToClient, ObservableModel model){
        this.connectionToClient = connectionToClient;

        this.packetBuildObservers = new ArrayList<>();
        this.packetMoveObservers = new ArrayList<>();
        this.packetCardsFromClientObservers = new ArrayList<>();
        this.packetStartPlayerObservers = new ArrayList<>();
        this.packetWorkersPositionsObservers = new ArrayList<>();

        connectionToClient.addObserver(this);

        model.addPacketCardsFromServerObserver((packetCardsFromServer) -> {
            if(packetCardsFromServer.getTo().equals(connectionToClient.getClientNickname())) {
                connectionToClient.send(packetCardsFromServer, true);
            }
        });
        model.addPacketDoActionObserver((packetDoAction) -> {
            if (packetDoAction.getTo().equals(connectionToClient.getClientNickname())) {
                connectionToClient.send(packetDoAction, true);
            }
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
        model.addPacketSetupObserver((packetSetup) -> {
                connectionToClient.send(packetSetup, false);
        });
        model.addPacketUpdateBoardObserver((packetUpdateBoard) -> {
            connectionToClient.send(packetUpdateBoard, false);
            if(packetUpdateBoard.getPlayerWonID() != null)
                connectionToClient.closeRoutineFull();
        });

    }


    @Override
    public void update(Object packetFromClient) {
        if(packetFromClient instanceof PacketMove){
            notifyPacketMoveObservers((PacketMove) packetFromClient);
        }else if(packetFromClient instanceof PacketBuild) {
            notifyPacketBuildObservers((PacketBuild) packetFromClient);
        }else if(packetFromClient instanceof PacketStartPlayer) {
            notifyPacketStartPlayerObservers((PacketStartPlayer) packetFromClient);
        }else if(packetFromClient instanceof PacketCardsFromClient) {
            notifyPacketCardsFromClientObservers((PacketCardsFromClient) packetFromClient);
        }else if(packetFromClient instanceof PacketWorkersPositions){
            notifyPacketWorkersPositionsObservers((PacketWorkersPositions) packetFromClient);
        }else{
            sendInvalidPacketMessage();
        }
    }

    public void sendInvalidPacketMessage(){
        connectionToClient.send(ConnectionMessages.INVALID_PACKET, true);
    }

    public String getClientNickname(){
        return connectionToClient.getClientNickname();
    }

    public void addPacketMoveObserver(Observer<PacketMove> o){
        this.packetMoveObservers.add(o);
    }
    public void addPacketBuildObserver(Observer<PacketBuild> o){
        this.packetBuildObservers.add(o);
    }
    public void addPacketCardsFromClientdObserver(Observer<PacketCardsFromClient> o){
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
