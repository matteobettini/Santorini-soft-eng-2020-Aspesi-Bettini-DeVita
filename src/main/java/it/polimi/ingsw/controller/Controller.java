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

    /**
     * This is the constructor of the controller: for each given virtual view
     * the controller subscribes to all the possible packets coming from that view
     * and forwards them to the Model
     * Eventually, upon rejection of the packet by the model, the controller notifies
     * the failure back the virtual view
     * @param virtualViews the list of the virtual views in the match
     * @param model the model interface
     */
    public Controller(List<VirtualView> virtualViews, Model model) {

        this.model = model;
        this.virtualViews = virtualViews;

        for(VirtualView virtualView : this.virtualViews){
            virtualView.addPacketMoveObserver((packetMove) ->{
                try{
                    if(packetMove.isSimulate())
                        this.model.getPossibeMoves(virtualView.getClientNickname(), packetMove);
                    else
                        this.model.makeMove(virtualView.getClientNickname(), packetMove);
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketBuildObserver((packetBuild) ->{
                try{
                    if(packetBuild.isSimulate())
                        this.model.getPossibleBuilds(virtualView.getClientNickname(), packetBuild);
                    else
                        this.model.makeBuild(virtualView.getClientNickname(), packetBuild);
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketCardsFromClientObserver((packetCardsFromClient) ->{
                try{
                    this.model.setSelectedCards(virtualView.getClientNickname(), packetCardsFromClient.getChosenCards());
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketStartPlayerObserver((packetStartPlayer) ->{
                try{
                    this.model.setStartPlayer(virtualView.getClientNickname(), packetStartPlayer.getStartPlayer());
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
            virtualView.addPacketWorkersPositionsObserver((packetWorkersPositions) ->{
                try{
                    this.model.setWorkersPositions(virtualView.getClientNickname(), packetWorkersPositions.getWorkersPositions());
                } catch (InvalidPacketException invalidPacketException){
                    virtualView.sendInvalidPacketMessage();
                }
            });
        }
    }



}
