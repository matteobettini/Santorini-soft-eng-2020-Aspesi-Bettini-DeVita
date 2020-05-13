package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HardcoreStrategy implements GameModeStrategy{

    private PacketDoAction lastAction;

    /**
     * This method is the handler for the move or build actions when the hardcore mode is set.
     * It calls the different handlers based on the ActionType and manage the choice of the move or the build if permitted.
     * @param packetDoAction is the packet containing the ActionType and its receiver.
     * @param isRetry is true if the action is requested another time, false otherwise.
     */
    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry) {
        MatchData matchData = MatchData.getInstance();

        if (!packetDoAction.getTo().equals(matchData.getPlayerName())) {
            OutputUtilities.displayOthersActions(packetDoAction.getActionType(), packetDoAction.getTo());
            return;
        }

        lastAction = packetDoAction;

        switch (packetDoAction.getActionType()) {
            case MOVE:
                if (isRetry) System.out.println("Not a valid move! Try again...");
                else System.out.println("Make your move!");
                handleMove();
                break;
            case BUILD:
                if (isRetry) System.out.println("Not a valid build! Try again...");
                else System.out.println("Make your build!");
                handleBuild();
                break;
            case MOVE_BUILD:
                if (isRetry) System.out.println("Not a valid move or build! Try again...");
                Integer choice;
                do {
                    System.out.print("Do you want to make a move(1) or a build(2): ");
                    choice = InputUtilities.getInt("Not a valid choice, choose an action: ");
                    if (choice == null) return;
                } while (choice != 1 && choice != 2);

                if (choice == 1) handleMove();
                else handleBuild();

                break;
        }
    }

    /**
     * This method handles the move action. Since the hardcore mode is on, the player has the free choice on the worker and the adjacent positions
     * without a built dome on them. The player has three possible choices during his move:
     * - restart the entire action and choose everything again.
     * - make a choice and add it to the currentMoves.
     * - confirm the performed actions and send the packet move to the server.
     */
    private void handleMove() {
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


        List<String> possibleWorkers = workersID.stream().filter(board::canMove).collect(Collectors.toList());

        String lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers);

        List<Point> currentChosenPositions = new ArrayList<>();

        Integer choice;

        Map<String, Point> workersToRestore = new HashMap<>();

        do {

            Point lastWorkerPosition = currentChosenPositions.isEmpty() ? board.getWorkerPosition(lastUsedWorker) : currentChosenPositions.get(currentChosenPositions.size() - 1);

            List<Point> availablePositions = board.getAdjacentPoints(lastWorkerPosition).stream().filter(p -> board.canMove(lastUsedWorker, lastWorkerPosition)).collect(Collectors.toList());

            OutputUtilities.printMatch();

            if(availablePositions.isEmpty()){
                System.out.println("You can't move anymore!");
                makeChoiceForbidden = true;
            }

            confirmActionForbidden = currentChosenPositions.isEmpty();

            choice = InputUtilities.getActionChoice(makeChoiceForbidden, restartForbidden, confirmActionForbidden);

            if (choice == -1) return;

            for (String worker : workersToRestore.keySet())
                graphicalBoard.getCell(workersToRestore.get(worker)).setWorker(worker);

            switch (choice) {
                case 1:
                    //FIRST WE GET THE PLAYER CHOICE

                    Point chosenPosition = InputUtilities.getChosenPosition(availablePositions,lastUsedWorker);

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

        } while (choice != 3);

        PacketMove packetConfirmation = new PacketMove(matchData.getPlayerName(), lastUsedWorker, false, currentChosenPositions);
        matchData.makeGraphicalBoardEqualToBoard();
        client.send(packetConfirmation);

    }

    /**
     * This method handles the build action. Since the hardcore mode is on, the player has the free choice on the worker and the adjacent positions(only duplicates are discarded).
     * The player has three possible choices during his move:
     * - restart the entire action and choose everything again.
     * - make a choice and add it to the currentBuilds.
     * - confirm the performed actions and send the packet build to the server.
     */
    private void handleBuild() {
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


        String lastUsedWorker = InputUtilities.getWorkerChoice(workersID);

        Map<Point, List<BuildingType>> currentBuilds = new HashMap<>();
        ArrayList<Point> currentDataOrder = new ArrayList<>();

        Integer choice;

        do {

            Map<Point, List<BuildingType>> possibleBuildingsInPoints = board.getPossibleBuildings(lastUsedWorker, currentBuilds);

            OutputUtilities.printMatch();

            if (possibleBuildingsInPoints.isEmpty()) {
                System.out.println("You can't build anymore!");
                makeChoiceForbidden = true;
            }


            confirmActionForbidden = currentDataOrder.isEmpty();

            choice = InputUtilities.getActionChoice(makeChoiceForbidden, restartForbidden, confirmActionForbidden);

            if (choice == -1) return;

            switch (choice) {
                case 1:
                    //FIRST WE GET THE PLAYER CHOICE

                    boolean getChoice = InputUtilities.getChosenBuildingsInPoint(possibleBuildingsInPoints, lastUsedWorker, currentDataOrder, currentBuilds);

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

    /**
     * Since the hardcore mode is on, a packet with the possible moves is not expected and thus it's ignored.
     */
    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves) { }

    /**
     * Since the hardcore mode is on, a packet with the possible builds is not expected and thus it's ignored.
     */
    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) { }

}
