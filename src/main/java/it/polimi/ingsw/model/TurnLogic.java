package it.polimi.ingsw.model;

import it.polimi.ingsw.model.InternalModel;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Worker;
import it.polimi.ingsw.model.cardReader.CardRule;
import it.polimi.ingsw.model.cardReader.RuleEffect;
import it.polimi.ingsw.model.cardReader.enums.EffectType;
import it.polimi.ingsw.model.cardReader.enums.TriggerType;
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
import java.util.*;
import java.util.List;

public class TurnLogic {

    private List<Player>  stillInGamePlayers;
    private final InternalModel model;
    private Player currPlayer;
    private Set<TriggerType> currPossibleActions;
    private Worker currWorker;
    private int activePlayerIndex;
    private final List<Observer<PacketUpdateBoard>> packetUpdateBoardObservers;
    private final List<Observer<PacketDoAction>> packetDoActionObservers;

    public TurnLogic(InternalModel model){
        this.model = model;
        this.packetDoActionObservers = new ArrayList<>();
        this.packetUpdateBoardObservers = new ArrayList<>();
    }

    public void start(){
        stillInGamePlayers = new ArrayList<>(model.getPlayers());
        currPlayer = stillInGamePlayers.get(0);
        currWorker = null;
        activePlayerIndex = 0;
        askNextPacket();
    }

    private void setNextPlayer(){

        if(stillInGamePlayers.size() == 0)
            return;

        if(stillInGamePlayers.size() == 1){
            PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(null, null, null, stillInGamePlayers.get(0).getNickname());
            notifyPacketUpdateBoardObservers(packetUpdateBoard);
            model.setWinner(stillInGamePlayers.get(0));
            return;
        }

        if(activePlayerIndex == stillInGamePlayers.size() - 1)
            activePlayerIndex = 0;
        else
            ++activePlayerIndex;

        currPlayer = stillInGamePlayers.get(activePlayerIndex);
        currWorker = null;

        askNextPacket();
    }

    private void askNextPacket(){

        if(stillInGamePlayers.size() == 0)
            return;

        ActionType nextPossibleAction = null;
        currPossibleActions = currPlayer.getPossibleActions();
        if(currPossibleActions.contains(TriggerType.BUILD) && currPossibleActions.contains(TriggerType.MOVE))
            nextPossibleAction = ActionType.MOVE_BUILD;
        else if(currPossibleActions.contains(TriggerType.BUILD))
            nextPossibleAction = ActionType.BUILD;
        else if(currPossibleActions.contains(TriggerType.MOVE))
            nextPossibleAction = ActionType.MOVE;

        assert(nextPossibleAction != null);


        switch (currPlayer.getState()) {
            case FIRST_BUILT:
                if (!model.canMove(currPlayer, currWorker)) {
                    makePlayerLoose();
                    return;
                }
                break;
            case TURN_STARTED:
                if (!(model.canMove(currPlayer, currPlayer.getWorkers().get(0)) || model.canMove(currPlayer, currPlayer.getWorkers().get(1)))) {
                    makePlayerLoose();
                    return;
                }
                break;
            case MOVED:
                if (!model.canBuild(currPlayer, currWorker)) {
                    makePlayerLoose();
                    return;
                }
                break;
            case BUILT:
                setNextPlayer();
                return;
        }

        PacketDoAction packetDoAction = new PacketDoAction(currPlayer.getNickname(), nextPossibleAction);
        notifyPacketDoActionObservers(packetDoAction);
    }

    public void consumePacketMove(PacketMove packetMove) throws InvalidPacketException{

        if(!packetMove.getPlayerNickname().equals(currPlayer.getNickname()))
            return;

        if(currWorker != null)
            if(!packetMove.getWorkerID().equals(currWorker.getID()))
                throw new InvalidPacketException();

        if(!currPossibleActions.contains(TriggerType.MOVE))
            throw new InvalidPacketException();


        String winner = null;
        String loser = null;

        try{
            MoveData moveData = model.packetMoveToMoveData(packetMove);
            if(!model.makeMove(moveData))
                throw new InvalidPacketException();
            currWorker = moveData.getWorker();
        } catch (PlayerWonSignal playerWonSignal) {
            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();
            stillInGamePlayers.clear();
        } catch (PlayerLostSignal playerLostSignal) {
            model.addLoser(currPlayer);
            stillInGamePlayers.remove(currPlayer);
            loser = currPlayer.getNickname();
        }

        Map<String, Point> workersPosition = new HashMap<>();
        for(Player p : stillInGamePlayers)
            for(Worker w: p.getWorkers())
                workersPosition.put(w.getID(),w.getPosition());


        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(workersPosition,null,loser,winner);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);
        askNextPacket();
    }

    public void consumePacketBuild(PacketBuild packetBuild) throws InvalidPacketException{

        if(!packetBuild.getPlayerNickname().equals(currPlayer.getNickname()))
            return;

        if(currWorker != null)
            if(!packetBuild.getWorkerID().equals(currWorker.getID()))
                throw new InvalidPacketException();

        if(!currPossibleActions.contains(TriggerType.BUILD))
            throw new InvalidPacketException();

        String winner = null;
        String loser = null;

        Map<Point, List<BuildingType>> newBuildings = null;
        try{
            BuildData buildData = model.packetBuildToBuildData(packetBuild);
            if(!model.makeBuild(buildData))
                throw new InvalidPacketException();
            newBuildings = buildData.getData();
            currWorker = buildData.getWorker();
        } catch (PlayerWonSignal playerWonSignal) {
            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();
            stillInGamePlayers.clear();
        } catch (PlayerLostSignal playerLostSignal) {
            stillInGamePlayers.remove(currPlayer);
            model.addLoser(currPlayer);
            loser = currPlayer.getNickname();
        }

        Map<String, Point> workersPosition = new HashMap<>();
        for(Player p : stillInGamePlayers)
            for(Worker w: p.getWorkers())
                workersPosition.put(w.getID(),w.getPosition());


        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(workersPosition,newBuildings,loser,winner);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);

        askNextPacket();
    }

    private void makePlayerLoose(){
        model.addLoser(currPlayer);
        stillInGamePlayers.remove(currPlayer);
        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(null, null, currPlayer.getNickname(), null);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);
        setNextPlayer();
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
