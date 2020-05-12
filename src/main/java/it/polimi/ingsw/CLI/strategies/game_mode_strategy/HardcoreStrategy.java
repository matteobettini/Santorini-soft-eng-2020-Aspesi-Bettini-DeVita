package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.CLI.strategies.DefaultUpdateBoardStrategy;
import it.polimi.ingsw.CLI.strategies.UpdateBoardStrategy;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HardcoreStrategy implements GameModeStrategy, UpdateBoardStrategy {

    private PacketDoAction lastAction;
    private String lastUsedWorker;
    private UpdateBoardStrategy updateBoardStrategy;
    private String lastConfirmedWorker;

    public HardcoreStrategy() {
        this.updateBoardStrategy = new DefaultUpdateBoardStrategy();
        this.lastUsedWorker = null;
        this.lastConfirmedWorker = null;
    }

    @Override
    public void handleUpdateBoard(PacketUpdateBoard packetUpdateBoard) {
        if (lastConfirmedWorker == null) lastConfirmedWorker = lastUsedWorker;

        updateBoardStrategy.handleUpdateBoard(packetUpdateBoard);
    }

    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry) {
        MatchData matchData = MatchData.getInstance();

        if (!packetDoAction.getTo().equals(matchData.getPlayerName())) {
            OutputUtilities.displayOthersActions(packetDoAction.getActionType(), packetDoAction.getTo());
            lastConfirmedWorker = null;
            return;
        }

        lastAction = packetDoAction;

        switch (packetDoAction.getActionType()) {
            case MOVE:
                handleMove(isRetry);
                break;
            case BUILD:
                handleBuild(isRetry);
                break;
            case MOVE_BUILD:
                if (isRetry) System.out.println("Not a valid move or build! Try again...");
                Integer choice;
                do {
                    System.out.print("Do you want to make a move(1) or a build(2): ");
                    choice = InputUtilities.getInt("Not a valid choice, choose an action: ");
                    if (choice == null) return;
                } while (choice != 1 && choice != 2);

                if (choice == 1) handleMove(false);
                else handleBuild(false);

                break;
        }
    }

    private void handleMove(boolean isRetry) {
        MatchData matchData = MatchData.getInstance();
        Client client = matchData.getClient();
        String player = matchData.getPlayerName();
        List<String> workersID = matchData.getIds().get(player);

        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE MOVES
        boolean confirmActionForbidden; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER

        //THIS IF IS ACCESSED WHEN THE PLAYER HAS NOT ALREADY CHOSEN THE WORKER
        if (lastConfirmedWorker == null) {

            List<String> possibleWorkers = workersID.stream().filter(board::canMove).collect(Collectors.toList());

            lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers);
            if (lastUsedWorker == null) return;
        } else{
            restartForbidden = true; //IF THE WORKER IS ALREADY CHOSEN, IN THE FIRST CHOICE THE PLAYER CAN'T RESTART
            lastUsedWorker = lastConfirmedWorker;
        }

        List<Point> currentChosenPositions = new ArrayList<>();

        Integer choice;

        Map<String, Point> workersToRestore = new HashMap<>();

        do {
            OutputUtilities.printMatch();

            if (currentChosenPositions.isEmpty()) {
                if (isRetry) System.out.println("Not a valid move! Try again...");
                else System.out.println("Make your move!");
            }

            confirmActionForbidden = currentChosenPositions.isEmpty();

            choice = InputUtilities.getActionChoice(makeChoiceForbidden, restartForbidden, confirmActionForbidden);

            if (choice == -1) return;

            for (String worker : workersToRestore.keySet())
                graphicalBoard.getCell(workersToRestore.get(worker)).setWorker(worker);

            switch (choice) {
                case 1:
                    //FIRST WE GET THE PLAYER CHOICE
                    Point lastWorkerPosition = currentChosenPositions.isEmpty() ? board.getWorkerPosition(lastUsedWorker) : currentChosenPositions.get(currentChosenPositions.size() - 1);

                    List<Point> availablePositions = board.getAdjacentPoints(lastWorkerPosition).stream().filter(p -> board.canMove(lastUsedWorker, lastWorkerPosition)).collect(Collectors.toList());

                    if(availablePositions.isEmpty()){
                        System.out.println("You can't move anymore!");
                        makeChoiceForbidden = true;
                        break;
                    }

                    Point chosenPosition = InputUtilities.getChosenPosition(availablePositions, board, lastUsedWorker);

                    if(chosenPosition == null) return;

                    //THE CHOSEN POSITION IS ADDED TO CURRENT POSITIONS THAT WILL FORM THE PACKET CONFIRMATION
                    currentChosenPositions.add(chosenPosition);

                    if (board.getCell(chosenPosition).getWorker() != null)
                        workersToRestore.put(board.getCell(chosenPosition).getWorker(), chosenPosition);

                    Integer workerNumber = matchData.getWorkerNumber(lastUsedWorker);

                    //WE DISPLAY CHANGES TO THE PLAYER WITHOUT MAKING ASSUMPTIONS ABOUT HIS GOD'S POWERS
                    graphicalBoard.removeWorker(matchData.getPlayerName(), workerNumber);
                    graphicalBoard.getCell(chosenPosition).setWorker(lastUsedWorker);
                    break;
                case 2:
                    //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                    matchData.makeGraphicalBoardEqualToBoard();
                    //WE CALL THIS METHOD AGAIN BECAUSE THE PROCESS SHOULD RESTART FROM THE BEGINNING
                    handleAction(lastAction, false);
                    return;
            }

            restartForbidden = false;
        } while (choice != 3);

        PacketMove packetConfirmation = new PacketMove(matchData.getPlayerName(), lastUsedWorker, false, currentChosenPositions);
        matchData.makeGraphicalBoardEqualToBoard();
        client.send(packetConfirmation);

    }

    private void handleBuild(boolean isRetry) {
        MatchData matchData = MatchData.getInstance();
        Client client = matchData.getClient();
        String player = matchData.getPlayerName();
        List<String> workersID = matchData.getIds().get(player);

        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE BUILDS
        boolean confirmActionForbidden; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER

        //THIS IF IS ACCESSED WHEN THE PLAYER HAS NOT ALREADY CHOSEN THE WORKER
        if (lastConfirmedWorker == null) {

            List<String> possibleWorkers = workersID.stream().filter(board::canBuild).collect(Collectors.toList());

            lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers);
            if (lastUsedWorker == null) return;
        } else{
            restartForbidden = true; //IF THE WORKER IS ALREADY CHOSEN, IN THE FIRST CHOICE THE PLAYER CAN'T RESTART
            lastUsedWorker = lastConfirmedWorker;
        }

        Map<Point, List<BuildingType>> currentBuilds = new HashMap<>();
        ArrayList<Point> currentDataOrder = new ArrayList<>();

        Integer choice;

        do {
            OutputUtilities.printMatch();

            if (currentDataOrder.isEmpty()) {
                if (isRetry) System.out.println("Not a valid build! Try again...");
                else System.out.println("Make your build!");
            }

            confirmActionForbidden = currentDataOrder.isEmpty();

            choice = InputUtilities.getActionChoice(makeChoiceForbidden, restartForbidden, confirmActionForbidden);

            if (choice == -1) return;

            switch (choice) {
                case 1:
                    //FIRST WE GET THE PLAYER CHOICE

                    Map<Point, List<BuildingType>> possibleBuildingsInPoints = board.getPossibleBuildings(lastUsedWorker, currentBuilds);

                    if (possibleBuildingsInPoints.isEmpty()) {
                        System.out.println("You can't build anymore!");
                        makeChoiceForbidden = true;
                        break;
                    }

                    boolean getChoice = InputUtilities.getChosenBuildingsInPoint(possibleBuildingsInPoints, board, lastUsedWorker, currentDataOrder, currentBuilds);

                    if(!getChoice) return;


                    for (Point position : currentBuilds.keySet())
                        graphicalBoard.getCell(position).addBuildings(currentBuilds.get(position));

                    break;
                case 2:
                    //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                    matchData.makeGraphicalBoardEqualToBoard();
                    //WE CALL THIS METHOD AGAIN BECAUSE THE PROCESS SHOULD RESTART FROM THE BEGINNING
                    handleAction(lastAction, false);
                    return;
            }

            restartForbidden = false;
        } while (choice != 3);

        PacketBuild packetBuildConfirmation = new PacketBuild(player, lastUsedWorker, false, currentBuilds, currentDataOrder);
        matchData.makeGraphicalBoardEqualToBoard();
        client.send(packetBuildConfirmation);

    }

    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves) { }

    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) { }

}
