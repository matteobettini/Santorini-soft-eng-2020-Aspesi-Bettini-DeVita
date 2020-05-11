package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.GraphicalBoard;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketMove;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HardcoreStrategy implements GameModeStrategy {

    private static final String POSITIONS_REGEXP = "^(([A-E]|[a-e])[1-5])$";
    private static final Pattern POSITION_PATTERN = Pattern.compile(POSITIONS_REGEXP);

    private static final String BUILDINGS_REGEXP = "^(([A-E]|[a-e])[1-5][ ][1-4])$";
    private static final Pattern BUILDINGS_PATTERN = Pattern.compile(BUILDINGS_REGEXP);

    private PacketDoAction lastAction;
    private String lastUsedWorker;

    public HardcoreStrategy(){
        this.lastUsedWorker = null;
    }

    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry){
        //TODO : SOLVE THE PROBLEM WHERE THERE IS AN INVALID MOVE/BUILD AND WE DON'T KNOW IF THE PLAYER CAN CHOOSE HIS WORKER OR NOT
        lastAction = packetDoAction;
        switch (packetDoAction.getActionType()){
            case MOVE:
                handleMove();
                break;
            case BUILD:
                if(isRetry) System.out.println("Not a valid build! Try again...");
                handleBuild();
                break;
            case MOVE_BUILD:
                if(isRetry) System.out.println("Not a valid move or build! Try again...");
                Integer choice;
                do{
                    System.out.print("Do you want to make a move(1) or a build(2): ");
                    choice = InputUtilities.getInt("Not a valid choice, choose an action: ");
                    if (choice == null) return;
                }while(choice != 1 && choice != 2);

                if(choice == 1) handleMove();
                else handleBuild();

                break;
        }
    }

    private void handleMove(){
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
        if(lastUsedWorker == null){

            System.out.println("Make your move!");

            List<String> possibleWorkers = workersID.stream().filter(board::canMove).collect(Collectors.toList());

            lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers);
            if(lastUsedWorker == null) return;
        }

        List<Point> currentChosenPositions = new ArrayList<>();

        Integer choice;

        Map<String, Point> workersToRestore = new HashMap<>();

        do{
            if(currentChosenPositions.isEmpty()){
                confirmActionForbidden = true;
                matchData.printMatch();
            }
            else confirmActionForbidden = false;

            choice = InputUtilities.getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);

            if(choice == -1) return;

            for(String worker : workersToRestore.keySet()) graphicalBoard.getCell(workersToRestore.get(worker)).setWorker(worker);

            switch(choice){
                case 1:
                    //FIRST WE GET THE PLAYER CHOICE
                    Point chosenPosition = getChosenPosition(board.getWorkerPosition(lastUsedWorker), board, currentChosenPositions);
                    if(chosenPosition ==  null){
                        System.out.println("You can't move anymore!");
                        makeChoiceForbidden = true;
                        break;
                    }
                    //THE CHOSEN POSITION IS ADDED TO CURRENT POSITIONS THAT WILL FORM THE PACKET CONFIRMATION
                    currentChosenPositions.add(chosenPosition);

                    if(board.getCell(chosenPosition).getWorker() != null) workersToRestore.put(board.getCell(chosenPosition).getWorker(), chosenPosition);

                    Integer workerNumber = Character.getNumericValue(lastUsedWorker.charAt(lastUsedWorker.length() - 1));

                    //WE DISPLAY CHANGES TO THE PLAYER WITHOUT MAKING ASSUMPTIONS ABOUT HIS GOD'S POWERS
                    graphicalBoard.removeWorker(matchData.getPlayerName(), workerNumber);
                    graphicalBoard.getCell(chosenPosition).setWorker(lastUsedWorker);
                    matchData.printMatch();
                    break;
                case 2:
                    //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                    matchData.makeGraphicalBoardEqualToBoard();
                    //WE CALL THIS METHOD AGAIN BECAUSE THE PROCESS SHOULD RESTART FROM THE BEGINNING
                    lastUsedWorker = null;
                    if(lastAction.getActionType() == ActionType.MOVE_BUILD) handleAction(lastAction, false);
                    else handleMove();
                    return;
            }
        }while(choice != 3);

        PacketMove packetConfirmation = new PacketMove(matchData.getPlayerName(),lastUsedWorker, false, currentChosenPositions);
        matchData.makeGraphicalBoardEqualToBoard();
        client.send(packetConfirmation);

    }

    private void handleBuild(){

    }

    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves){ }

    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) { }

    private Point getChosenPosition(Point workerPosition, Board board, List<Point> currentChosenPositions){

        StringBuilder positionsBuilder = new StringBuilder();
        int workerNumber = Character.getNumericValue(lastUsedWorker.charAt(lastUsedWorker.length() - 1));

        Point lastWorkerPosition = currentChosenPositions.isEmpty() ? workerPosition : currentChosenPositions.get(currentChosenPositions.size() - 1);

        List<Point> availablePositions = board.getAdjacentPoints(lastWorkerPosition).stream().filter(p -> board.canMove(lastUsedWorker, lastWorkerPosition)).collect(Collectors.toList());

        if(availablePositions.isEmpty()) return null;
        //WE FIRST DISPLAY THE POSSIBLE POSITIONS
        for(Point position : availablePositions){
            positionsBuilder.append("- ").append(board.getCoordinates(position)).append("\n");
        }

        System.out.println("Available positions: ");
        System.out.println(positionsBuilder.toString());

        //THE PLAYER CAN NOW CHOOSE HIS WORKER'S NEXT POSITION
        String point;
        Point chosenPosition;
        boolean error = false;
        boolean suggestion = true;
        do{
            if(error) System.out.println("Invalid position for worker" + (workerNumber) + ", retry");

            do{
                if(suggestion)  System.out.print("Choose your next worker" + (workerNumber) + "'s position (ex A1, B2...): ");
                else System.out.print("Choose your next worker" + (workerNumber) + "'s position: ");
                suggestion = false;
                point = InputUtilities.getLine();
                if(point == null) return null;
            }while(!POSITION_PATTERN.matcher(point).matches());

            chosenPosition = board.getPoint(Character.getNumericValue(point.charAt(1)), Character.toUpperCase(point.charAt(0)));
            assert board.getCell(chosenPosition) == null;
            error = !availablePositions.contains(chosenPosition);
        }while(error);

        return chosenPosition;
    }
}
