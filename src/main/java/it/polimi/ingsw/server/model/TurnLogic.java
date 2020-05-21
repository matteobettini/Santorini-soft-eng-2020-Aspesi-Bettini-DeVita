package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.cards.enums.TriggerType;
import it.polimi.ingsw.common.enums.ActionType;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.server.model.enums.PlayerState;
import it.polimi.ingsw.server.model.exceptions.PlayerLostSignal;
import it.polimi.ingsw.server.model.exceptions.PlayerWonSignal;
import it.polimi.ingsw.common.utils.observe.Observer;
import it.polimi.ingsw.common.packets.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class TurnLogic {

    private List<Player> stillInGamePlayers;
    private final InternalModel model;
    private Player currPlayer;
    private Set<TriggerType> currPossibleActions;
    private Worker currWorker;
    private int activePlayerIndex;

    private final List<Observer<PacketDoAction>> packetDoActionObservers;
    private final List<Observer<PacketUpdateBoard>> packetUpdateBoardObservers;
    private final List<Observer<PacketPossibleMoves>> packetPossibleMovesObservers;
    private final List<Observer<PacketPossibleBuilds>> packetPossibleBuildsObservers;

    private Observer<String> gameFinishedHandler;

    TurnLogic(InternalModel model) {
        super();
        this.model = model;
        this.currPossibleActions = new HashSet<>();

        this.packetDoActionObservers = new ArrayList<>();
        this.packetUpdateBoardObservers = new ArrayList<>();
        this.packetPossibleBuildsObservers = new ArrayList<>();
        this.packetPossibleMovesObservers = new ArrayList<>();
    }

    public void start() {
        stillInGamePlayers = new ArrayList<>(model.getPlayers());
        currPlayer = stillInGamePlayers.get(0);
        currWorker = null;
        activePlayerIndex = 0;
        askNextPacket();
    }

    private void setNextPlayer() {

        if (stillInGamePlayers.size() == 0)
            return;

        if (stillInGamePlayers.size() == 1) {
            String winner = stillInGamePlayers.get(0).getNickname();
            PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(winner, true);
            notifyPacketUpdateBoardObservers(packetUpdateBoard);
            model.setWinner(stillInGamePlayers.get(0));
            stillInGamePlayers.clear();
            if (gameFinishedHandler != null)
                gameFinishedHandler.update(winner);
            return;
        }

        incrementActivePlayerIndex();

        currPlayer = stillInGamePlayers.get(activePlayerIndex);
        currWorker = null;
        currPlayer.setPlayerState(PlayerState.TURN_STARTED);
        currPlayer.clearFlags();
        currPossibleActions.clear();

        askNextPacket();
    }

    private void askNextPacket() {

        if (stillInGamePlayers.size() < 2) {
            setNextPlayer();
            return;
        }

        ActionType nextPossibleAction = null;
        currPossibleActions = currPlayer.getPossibleActions();
        if (currPossibleActions.contains(TriggerType.BUILD) && currPossibleActions.contains(TriggerType.MOVE))
            nextPossibleAction = ActionType.MOVE_BUILD;
        else if (currPossibleActions.contains(TriggerType.BUILD))
            nextPossibleAction = ActionType.BUILD;
        else if (currPossibleActions.contains(TriggerType.MOVE))
            nextPossibleAction = ActionType.MOVE;

        assert (currPlayer.getState() == PlayerState.BUILT || nextPossibleAction != null);

        switch (currPlayer.getState()) {
            case FIRST_BUILT:
                assert currWorker != null;
                if (!model.canMove(currPlayer, currWorker)) {
                    makePlayerLoose();
                    return;
                }
                break;
            case TURN_STARTED:
                if (!model.canMove(currPlayer)) {
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
        //System.out.println("Sending do action to " + currPlayer.getNickname());
        PacketDoAction packetDoAction = new PacketDoAction(currPlayer.getNickname(), nextPossibleAction);
        notifyPacketDoActionObservers(packetDoAction);

    }

    public void consumePacketMove(String senderID, PacketMove packetMove) throws InvalidPacketException {

        if (!senderID.equals(currPlayer.getNickname()))
            return;

        if (!packetMove.getPlayerNickname().equals(currPlayer.getNickname()))
            throw new InvalidPacketException();

        if (currWorker != null)
            if (!packetMove.getWorkerID().equals(currWorker.getID()))
                throw new InvalidPacketException();

        if (!currPossibleActions.contains(TriggerType.MOVE))
            throw new InvalidPacketException();


        String winner = null;

        Map<String, Point> workersPosition;
        MoveData moveData = model.packetMoveToMoveData(packetMove);
        try {

            if (!model.makeMove(moveData))
                throw new InvalidPacketException();

            currWorker = moveData.getWorker();
            workersPosition = new HashMap<>();
            for (Player p : stillInGamePlayers)
                for (Worker w : p.getWorkers())
                    workersPosition.put(w.getID(), w.getPosition());

        } catch (PlayerWonSignal playerWonSignal) {

            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();

            currWorker = moveData.getWorker();
            workersPosition = new HashMap<>();
            for (Player p : stillInGamePlayers)
                for (Worker w : p.getWorkers())
                    workersPosition.put(w.getID(), w.getPosition());

            stillInGamePlayers.clear();

        } catch (PlayerLostSignal playerLostSignal) {
            makePlayerLoose();
            return;
        }

        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(workersPosition, winner);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);

        if(winner != null && gameFinishedHandler != null)
            gameFinishedHandler.update(winner);

        assert stillInGamePlayers.size() != 1;
        if (stillInGamePlayers.size() > 0)
            askNextPacket();
    }

    public void consumePacketBuild(String senderID, PacketBuild packetBuild) throws InvalidPacketException {

        if (!senderID.equals(currPlayer.getNickname()))
            return;

        if (!packetBuild.getPlayerNickname().equals(currPlayer.getNickname()))
            throw new InvalidPacketException();

        if (currWorker != null)
            if (!packetBuild.getWorkerID().equals(currWorker.getID()))
                throw new InvalidPacketException();


        if (!currPossibleActions.contains(TriggerType.BUILD))
            throw new InvalidPacketException();

        String winner = null;

        Map<Point, List<BuildingType>> newBuildings;
        BuildData buildData = model.packetBuildToBuildData(packetBuild);
        try {
            if (!model.makeBuild(buildData))
                throw new InvalidPacketException();

            newBuildings = new HashMap<>(buildData.getData());
            currWorker = buildData.getWorker();

        } catch (PlayerWonSignal playerWonSignal) {

            model.setWinner(currPlayer);
            winner = currPlayer.getNickname();

            newBuildings = new HashMap<>(buildData.getData());
            currWorker = buildData.getWorker();

            stillInGamePlayers.clear();
        } catch (PlayerLostSignal playerLostSignal) {
            makePlayerLoose();
            return;
        }


        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(winner, newBuildings);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);

        if(winner != null && gameFinishedHandler != null)
            gameFinishedHandler.update(winner);

        assert stillInGamePlayers.size() != 1;
        if (stillInGamePlayers.size() > 0)
            askNextPacket();
    }

    public void getPossibleMoves(String senderID, PacketMove packetMove) {
        assert (packetMove.isSimulate());

        if (!senderID.equals(currPlayer.getNickname()) || !packetMove.getPlayerNickname().equals(currPlayer.getNickname()))
            return;

        if (!currPossibleActions.contains(TriggerType.MOVE))
            return;

        boolean wantsInfoForAllWorkers = false;

        if(packetMove.getWorkerID() == null)
            wantsInfoForAllWorkers = true;
        else{
            if(currPlayer.getWorkers().stream().noneMatch(x -> x.getID().equals(packetMove.getWorkerID())))
                return;
        }

        if (currWorker != null) {
            if (packetMove.getWorkerID() != null && !packetMove.getWorkerID().equals(currWorker.getID()))
                return;
        }

        Worker myWorker;
        if(currWorker != null)
            myWorker = currWorker;
        else if(wantsInfoForAllWorkers)
            myWorker = currPlayer.getWorkers().get(0);
        else
            myWorker = model.getWorkerByID(packetMove.getWorkerID());

        List<Worker> myOtherWorkers = currPlayer.getWorkers().stream().filter(x -> !x.getID().equals(myWorker.getID())).collect(Collectors.toList());
        assert myOtherWorkers.size() > 0;

        Map<String, Set<Point>> possibleMoves = new HashMap<>();

        Set<Point> possiblePointsMyWorker = new HashSet<>();
        possibleMoves.put(myWorker.getID(), possiblePointsMyWorker);

        for(Worker myOtherWorker : myOtherWorkers){
            Set<Point> possiblePointsOtherWorker = new HashSet<>();
            possibleMoves.put(myOtherWorker.getID(), possiblePointsOtherWorker);
        }

        if (!wantsInfoForAllWorkers && packetMove.getMove() != null && !packetMove.getMove().isEmpty()) {
            MoveData moveData;
            try {
                moveData = model.packetMoveToMoveData(packetMove);
            } catch (InvalidPacketException e) {
                sendPossibleMoves(currPlayer.getNickname(), possibleMoves);
                return;
            }

            possiblePointsMyWorker.addAll(model.getPossibleMoves(moveData));

        } else {
            possiblePointsMyWorker.addAll(model.getPossibleMoves(currPlayer, myWorker));
            if (wantsInfoForAllWorkers && currWorker == null) {
                for(Worker myOtherWorker : myOtherWorkers){
                    Set<Point> possiblePointsOtherWorker = possibleMoves.get(myOtherWorker.getID());
                    possiblePointsOtherWorker.addAll(model.getPossibleMoves(currPlayer, myOtherWorker));
                }
            }
        }

        sendPossibleMoves(currPlayer.getNickname(), possibleMoves);
    }

    public void getPossibleBuilds(String senderID, PacketBuild packetBuild) {
        assert (packetBuild.isSimulate());

        if (!senderID.equals(currPlayer.getNickname()) || !packetBuild.getPlayerNickname().equals(currPlayer.getNickname()))
            return;

        if (!currPossibleActions.contains(TriggerType.BUILD))
            return;

        boolean wantsInfoForAllWorkers = false;

        if(packetBuild.getWorkerID() == null)
            wantsInfoForAllWorkers = true;
        else{
            if(currPlayer.getWorkers().stream().noneMatch(x -> x.getID().equals(packetBuild.getWorkerID())))
                return;
        }

        if (currWorker != null) {
            if (packetBuild.getWorkerID() != null && !packetBuild.getWorkerID().equals(currWorker.getID()))
                return;
        }

        Worker myWorker;
        if(currWorker != null)
            myWorker = currWorker;
        else if(wantsInfoForAllWorkers)
            myWorker = currPlayer.getWorkers().get(0);
        else
            myWorker = model.getWorkerByID(packetBuild.getWorkerID());

        List<Worker> myOtherWorkers = currPlayer.getWorkers().stream().filter(x -> !x.getID().equals(myWorker.getID())).collect(Collectors.toList());
        assert myOtherWorkers.size() > 0;

        Map<String, Map<Point, List<BuildingType>>> possibleBuilds = new HashMap<>();

        Map<Point, List<BuildingType>> possibleBuildsMyWorker = new HashMap<>();
        possibleBuilds.put(myWorker.getID(), possibleBuildsMyWorker);

        for(Worker myOtherWorker : myOtherWorkers){
            Map<Point, List<BuildingType>> possibleBuildsMyOtherWorker = new HashMap<>();
            possibleBuilds.put(myOtherWorker.getID(),possibleBuildsMyOtherWorker);
        }

        if (!wantsInfoForAllWorkers && packetBuild.getBuilds() != null && !packetBuild.getBuilds().isEmpty()) {
            BuildData buildData;
            try {
                buildData = model.packetBuildToBuildData(packetBuild);
            } catch (InvalidPacketException e) {
                sendPossibleBuilds(currPlayer.getNickname(), possibleBuilds);
                return;
            }
            possibleBuildsMyWorker.putAll(model.getPossibleBuilds(buildData));

        } else {
            possibleBuildsMyWorker.putAll(model.getPossibleBuilds(currPlayer, myWorker));
            if (wantsInfoForAllWorkers && currWorker == null) {
                for(Worker myOtherWorker : myOtherWorkers){
                    Map<Point, List<BuildingType>> possibleBuildsMyOtherWorker = possibleBuilds.get(myOtherWorker.getID());
                    possibleBuildsMyOtherWorker.putAll(model.getPossibleBuilds(currPlayer, myOtherWorker));
                }
            }
        }
        sendPossibleBuilds(currPlayer.getNickname(), possibleBuilds);
    }

    private void sendPossibleBuilds(String to, Map<String, Map<Point, List<BuildingType>>> possibleBuilds) {
        PacketPossibleBuilds packetPossibleBuilds = new PacketPossibleBuilds(to, possibleBuilds);
        notifyPacketPossibleBuildsObservers(packetPossibleBuilds);
    }

    private void sendPossibleMoves(String to, Map<String, Set<Point>> possibleMoves) {
        PacketPossibleMoves packetPossibleMoves = new PacketPossibleMoves(to, possibleMoves);
        notifyPacketPossibleMovesObservers(packetPossibleMoves);
    }

    private void incrementActivePlayerIndex() {
        if (activePlayerIndex >= stillInGamePlayers.size() - 1)
            activePlayerIndex = 0;
        else
            ++activePlayerIndex;
    }

    private void makePlayerLoose() {

        model.addLoser(currPlayer);

        if (activePlayerIndex == 0)
            activePlayerIndex = stillInGamePlayers.size() - 1;
        else
            activePlayerIndex--;
        stillInGamePlayers.remove(currPlayer);

        PacketUpdateBoard packetUpdateBoard = new PacketUpdateBoard(currPlayer.getNickname(), false);
        notifyPacketUpdateBoardObservers(packetUpdateBoard);
        setNextPlayer();
    }


    public void addPacketDoActionObserver(Observer<PacketDoAction> o) {
        this.packetDoActionObservers.add(o);
    }
    public void addPacketUpdateBoardObserver(Observer<PacketUpdateBoard> o) {
        this.packetUpdateBoardObservers.add(o);
    }
    public void addPacketPossibleMovesObserver(Observer<PacketPossibleMoves> o) {
        this.packetPossibleMovesObservers.add(o);
    }
    public void addPacketPossibleBuildsObserver(Observer<PacketPossibleBuilds> o) {
        this.packetPossibleBuildsObservers.add(o);
    }
    public void notifyPacketPossibleMovesObservers(PacketPossibleMoves p){
        for(Observer<PacketPossibleMoves> o : packetPossibleMovesObservers){
            o.update(p);
        }
    }
    public void notifyPacketUpdateBoardObservers(PacketUpdateBoard p){
        for(Observer<PacketUpdateBoard> o : packetUpdateBoardObservers){
            o.update(p);
        }
    }
    public void notifyPacketPossibleBuildsObservers(PacketPossibleBuilds p){
        for(Observer<PacketPossibleBuilds> o : packetPossibleBuildsObservers){
            o.update(p);
        }
    }
    public void notifyPacketDoActionObservers(PacketDoAction p){
        for(Observer<PacketDoAction> o : packetDoActionObservers){
            o.update(p);
        }
    }

    public void setGameFinishedHandler(Observer<String> gameFinishedHandler) {
        this.gameFinishedHandler = gameFinishedHandler;
    }
}