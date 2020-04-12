package it.polimi.ingsw;

import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.model.enums.PlayerState;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerLostSignal;
import it.polimi.ingsw.model.lambdaStrategy.exceptions.PlayerWonSignal;
import it.polimi.ingsw.model.turnInfo.BuildData;
import it.polimi.ingsw.model.turnInfo.MoveData;
import it.polimi.ingsw.observe.Observer;
import it.polimi.ingsw.packets.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TurnLogic {

    private final InternalModel model;
    private Player currPlayer;
    private Worker currWorker;
    private int pos;
    private final List<Observer<PacketUpdateBoard>> packetUpdateBoardObservers;
    private final List<Observer<PacketDoAction>> packetDoActionObservers;

    public TurnLogic(InternalModel model){
        this.model = model;
        this.packetDoActionObservers = new ArrayList<>();
        this.packetUpdateBoardObservers = new ArrayList<>();
    }

    public void start(){
        currPlayer = model.getPlayers().get(0);
        currWorker = null;
        pos = 0;
        askNextPacket();
    }

    private void setNextPlayer(){
        if(model.getPlayers().size() == 1){
            model.setWinner(model.getPlayers().get(0));
            return;
        }

        if(pos == model.getPlayers().size() - 1) pos = 0;
        else ++pos;

        currPlayer = model.getPlayers().get(pos);
        currWorker = null;
    }

    private void askNextPacket(){
        boolean checkMoveBuild = false;
        for(CardRule rule : currPlayer.getCard().getRules()){
            RuleEffect effect = rule.getEffect();
            if(effect.getNextState() == PlayerState.FIRST_BUILT) checkMoveBuild = true;
        }
        ActionType action = null;
        if(checkMoveBuild && currPlayer.getState().equals(PlayerState.TURN_STARTED)){
            if(model.canMove(currPlayer, currPlayer.getWorkers().get(0)) || model.canMove(currPlayer, currPlayer.getWorkers().get(1))) action = ActionType.MOVE_BUILD;
            else{
                model.addLoser(currPlayer);
                PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(null,null,currPlayer.getNickname(),null);
                notifyPacketUpdateBoardObservers(packetUpdateBoard);
                setNextPlayer();
            }
        }
        else if(currPlayer.getState().equals(PlayerState.TURN_STARTED)){
            if(model.canMove(currPlayer, currPlayer.getWorkers().get(0)) || model.canMove(currPlayer, currPlayer.getWorkers().get(1))) action = ActionType.MOVE;
            else{
                model.addLoser(currPlayer);
                PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(null,null,currPlayer.getNickname(),null);
                notifyPacketUpdateBoardObservers(packetUpdateBoard);
                setNextPlayer();
            }
        }
        else if(currPlayer.getState().equals(PlayerState.FIRST_BUILT)){
            if(model.canMove(currPlayer, currWorker)) action = ActionType.MOVE;
            else{
                model.addLoser(currPlayer);
                PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(null,null,currPlayer.getNickname(),null);
                notifyPacketUpdateBoardObservers(packetUpdateBoard);
                setNextPlayer();
            }
        }
        else if (currPlayer.getState().equals(PlayerState.MOVED)){
            if(model.canBuild(currPlayer, currWorker)) action = ActionType.BUILD;
            else{
                model.addLoser(currPlayer);
                PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(null,null,currPlayer.getNickname(),null);
                notifyPacketUpdateBoardObservers(packetUpdateBoard);
                setNextPlayer();
            }
        }
        assert(action != null);
        PacketDoAction packetDoAction = new PacketDoAction(currPlayer.getNickname(), action);
        notifyPacketDoActionObservers(packetDoAction);
    }

    public void consumePacketMove(PacketMove packetMove) throws InvalidMove{
        if(packetMove.getPlayerNickname() == null || !packetMove.getPlayerNickname().equals(currPlayer.getNickname())) askNextPacket();
        if(currPlayer.getState() == PlayerState.FIRST_BUILT && (packetMove.getWorkerID() == null || !packetMove.getWorkerID().equals(currWorker.getID()))) askNextPacket();
        if(packetMove.getWorkerID() == null) askNextPacket();
        String winner = null;
        String loser = null;
        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            if(!model.makeMove(moveData)) throw new InvalidMove();
            currWorker = moveData.getWorker();
        } catch (InvalidPacketException e) {
            askNextPacket();
        } catch (PlayerWonSignal playerWonSignal) {
            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();
        } catch (PlayerLostSignal playerLostSignal) {
            model.addLoser(currPlayer);
            loser = currPlayer.getNickname();
        }

        Map<String, Point> workersPosition = new HashMap<>();
        for(Player p : model.getPlayers()){
            for(Worker w: p.getWorkers()) workersPosition.put(w.getID(),w.getPosition());
        }

        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(workersPosition,null,loser,winner);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);
        askNextPacket();
    }

    public void consumePacketBuild(PacketBuild packetBuild) throws InvalidBuild{
        if(packetBuild.getPlayerNickname() == null || !packetBuild.getPlayerNickname().equals(currPlayer.getNickname())) askNextPacket();
        if((packetBuild.getWorkerID() == null || (currPlayer.getState() == PlayerState.MOVED) && !packetBuild.getWorkerID().equals(currWorker.getID()))) askNextPacket();
        String winner = null;
        String loser = null;
        Map<Point, List<BuildingType>> newBuildings = null;
        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            newBuildings = buildData.getData();
            if(!model.makeBuild(buildData)) throw new InvalidBuild();
        } catch (InvalidPacketException e) {
            askNextPacket();
        } catch (PlayerWonSignal playerWonSignal) {
            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();
        } catch (PlayerLostSignal playerLostSignal) {
            model.addLoser(currPlayer);
            loser = currPlayer.getNickname();
        }

        if(currPlayer.getState() == PlayerState.BUILT) setNextPlayer();
        else if(currPlayer.getState() == PlayerState.FIRST_BUILT) currWorker = model.getWorkerByID(packetBuild.getWorkerID());

        Map<String, Point> workersPosition = new HashMap<>();
        for(Player p : model.getPlayers()){
            for(Worker w: p.getWorkers()) workersPosition.put(w.getID(),w.getPosition());
        }

        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(workersPosition,newBuildings,loser,winner);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);

        askNextPacket();
    }

    public void addPacketDoActionObserver(Observer<PacketDoAction> o){
        this.packetDoActionObservers.add(o);
    }
    public void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> o){
        this.packetUpdateBoardObservers.add(o);
    }

    public void notifyPacketDoActionObservers(PacketDoAction p){
        for(Observer<PacketDoAction> observer: this.packetDoActionObservers){
            observer.update(p);
        }
    }
    public void notifyPacketUpdateBoardObservers(PacketUpdateBoard p){
        for(Observer<PacketUpdateBoard> observer: this.packetUpdateBoardObservers){
            observer.update(p);
        }
    }
}
