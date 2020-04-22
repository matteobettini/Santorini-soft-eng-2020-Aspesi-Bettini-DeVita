package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Model;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.ConnectionMessages;
import it.polimi.ingsw.packets.InvalidPacketException;
import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketMove;
import it.polimi.ingsw.view.VirtualView;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class Controller {

    private final List<VirtualView> virtualViews;
    private final Model model;

    public Controller(List<VirtualView> virtualViews, Model model) {

        this.model = model;
        this.virtualViews = virtualViews;

        for(VirtualView virtualView : this.virtualViews){
            virtualView.addPacketMoveObserver((packetMove) ->{
                try{
                    if(packetMove.isSimulate())
                        model.getPossibeMoves(virtualView.getClientNickname(), packetMove);
                    else
                        model.makeMove(virtualView.getClientNickname(), packetMove);
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketBuildObserver((packetBuild) ->{
                try{
                    if(packetBuild.isSimulate())
                        model.getPossibleBuilds(virtualView.getClientNickname(), packetBuild);
                    else
                        model.makeBuild(virtualView.getClientNickname(), packetBuild);
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketCardsFromClientdObserver((packetCardsFromClient) ->{
                try{
                    model.setSelectedCards(virtualView.getClientNickname(), packetCardsFromClient.getChosenCards());
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketStartPlayerObserver((packetStartPlayer) ->{
                try{
                    model.setStartPlayer(virtualView.getClientNickname(), packetStartPlayer.getStartPlayer());
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketWorkersPositionsObserver((packetWorkersPositions) ->{
                try{
                    model.setWorkersPositions(virtualView.getClientNickname(), packetWorkersPositions.getWorkersPositions());
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
        }
    }



}
