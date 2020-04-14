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
            stillInGamePlayers.clear();
            return;
        }

        incrementActivePlayerIndex();

        currPlayer = stillInGamePlayers.get(activePlayerIndex);
        currWorker = null;

        askNextPacket();
    }

    private void askNextPacket(){

        if(stillInGamePlayers.size() < 2) {
            setNextPlayer();
            return;
        }

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
                assert currWorker != null;
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
                assert currWorker != null;
                if (!model.canBuild(currPlayer, currWorker)) {
                    makePlayerLoose();
                    return;
                }
                break;
            case BUILT:
                assert currWorker != null;
                setNextPlayer();
                return;
        }

        PacketDoAction packetDoAction = new PacketDoAction(currPlayer.getNickname(), nextPossibleAction);
        notifyPacketDoActionObservers(packetDoAction);
    }

    public void consumePacketMove(String senderID, PacketMove packetMove) throws InvalidPacketException{

        if(!senderID.equals(currPlayer.getNickname()))
            return;

        if(!packetMove.getPlayerNickname().equals(currPlayer.getNickname()))
            throw new InvalidPacketException();

        if(currWorker != null)
            if(!packetMove.getWorkerID().equals(currWorker.getID()))
                throw new InvalidPacketException();


        if(!currPossibleActions.contains(TriggerType.MOVE))
            throw new InvalidPacketException();


        String winner = null;

        Map<String, Point> workersPosition = null;
        MoveData moveData = model.packetMoveToMoveData(packetMove);
        try{

            if(!model.makeMove(moveData))
                throw new InvalidPacketException();

            currWorker = moveData.getWorker();
            workersPosition = new HashMap<>();
            for(Player p : stillInGamePlayers)
                for(Worker w : p.getWorkers())
                    workersPosition.put(w.getID(),w.getPosition());

        } catch (PlayerWonSignal playerWonSignal) {
            assert playerWonSignal.getPlayer().equals(currPlayer);

            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();

            currWorker = moveData.getWorker();
            workersPosition = new HashMap<>();
            for(Player p : stillInGamePlayers)
                for(Worker w : p.getWorkers())
                    workersPosition.put(w.getID(),w.getPosition());

            stillInGamePlayers.clear();

        } catch (PlayerLostSignal playerLostSignal) {
            assert playerLostSignal.getPlayer().equals(currPlayer);
            makePlayerLoose();
            return;
        }

        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(workersPosition,null,null,winner);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);

        assert stillInGamePlayers.size() != 1;
        if(stillInGamePlayers.size() > 0)
            askNextPacket();
    }

    public void consumePacketBuild(String senderID, PacketBuild packetBuild) throws InvalidPacketException{

        if(!senderID.equals(currPlayer.getNickname()))
            return;

        if(!packetBuild.getPlayerNickname().equals(currPlayer.getNickname()))
            throw new InvalidPacketException();

        if(currWorker != null)
            if(!packetBuild.getWorkerID().equals(currWorker.getID()))
                throw new InvalidPacketException();


        if(!currPossibleActions.contains(TriggerType.BUILD))
            throw new InvalidPacketException();

        String winner = null;

        Map<Point, List<BuildingType>> newBuildings = null;
        BuildData buildData = model.packetBuildToBuildData(packetBuild);
        try{
            if(!model.makeBuild(buildData))
                throw new InvalidPacketException();

            newBuildings = new HashMap<>(buildData.getData());
            currWorker = buildData.getWorker();

        } catch (PlayerWonSignal playerWonSignal) {
            assert playerWonSignal.getPlayer().equals(currPlayer);

            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();

            newBuildings = new HashMap<>(buildData.getData());
            currWorker = buildData.getWorker();

            stillInGamePlayers.clear();
        } catch (PlayerLostSignal playerLostSignal) {
            assert playerLostSignal.getPlayer().equals(currPlayer);
            makePlayerLoose();
            return;
        }


        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(null,newBuildings,null,winner);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);

        assert stillInGamePlayers.size() != 1;
        if(stillInGamePlayers.size() > 0)
            askNextPacket();
    }

    private void incrementActivePlayerIndex() {
        if (activePlayerIndex >= stillInGamePlayers.size() - 1)
            activePlayerIndex = 0;
        else
            ++activePlayerIndex;
    }


    private void makePlayerLoose(){

        model.addLoser(currPlayer);

        if(activePlayerIndex == 0)
            activePlayerIndex = stillInGamePlayers.size()-1;
        else
            activePlayerIndex--;
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
