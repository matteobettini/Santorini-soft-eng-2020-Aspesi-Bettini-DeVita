package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.packets.PacketMove;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MoveActionStrategy implements ActionStrategy{
    private static final String POSITIONS_REGEXP = "(([A-E]|[a-e])[1-5])";
    private static final Pattern POSITION_PATTERN = Pattern.compile(POSITIONS_REGEXP);

    private List<Point> currentPositions;
    private Integer lastUsedWorker; //null if the action has just arrived

    public MoveActionStrategy(){
        this.currentPositions = new LinkedList<>();
        this.lastUsedWorker = null;
    }

    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        MatchData matchData = MatchData.getInstance();
        Client client = matchData.getClient();
        String player = matchData.getPlayerName();
        List<String> workersID = matchData.getIds().get(player);

        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE MOVES
        boolean confirmActionForbidden = false; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER

        //THIS IF IS ACCESSED WHEN THE PLAYER HAS NOT ALREADY CHOOSE THE WORKER
        if(lastUsedWorker == null){

            //IF THE PLAYER HAS NOT CHOSEN A WORKER HE CAN'T CONFIRM AN EMPTY MOVE
            confirmActionForbidden = true;

            List<String> possibleWorkers = new ArrayList<>(packetPossibleMoves.getPossibleMoves().keySet());

            lastUsedWorker = getWorkerChoice(possibleWorkers, workersID);
            if(lastUsedWorker == null) return false;
        }

        //POSSIBLE POSITIONS CONTAINS THE POSITIONS THAT THE PLAYER CAN CHOOSE AT THIS TIME DURING THE MOVE
        List<Point> possiblePositions = new ArrayList<>(packetPossibleMoves.getPossibleMoves().get(workersID.get(lastUsedWorker)));
        graphicalBoard.setPossibleActions(possiblePositions);

        matchData.printMatch();

        graphicalBoard.resetPossibleActions();

        //IF THERE ARE NO POSSIBLE POSITIONS THE PLAYER CAN'T MAKE A CHOICE
        if(possiblePositions.isEmpty()) makeChoiceForbidden = true;

        Integer choice = getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);
        if(choice == -1) return false;

        switch(choice){
            case 1:
                //FIRST WE GET THE PLAYER CHOICE
                Point chosenPosition = getChosenPosition(possiblePositions, board);
                if(chosenPosition ==  null) return false;
                //THE CHOSEN POSITION IS ADDED TO CURRENT POSITIONS THAT WILL FORM THE PACKET CONFIRMATION
                currentPositions.add(chosenPosition);

                //WE DISPLAY CHANGES TO THE PLAYER WITHOUT MAKING ASSUMPTIONS ABOUT HIS GOD'S POWERS
                graphicalBoard.removeWorker(matchData.getPlayerName(), lastUsedWorker + 1);
                graphicalBoard.getCell(chosenPosition).setWorker(workersID.get(lastUsedWorker));

                //WE THEN ASK FOR ANOTHER GET POSSIBLE MOVES
                PacketMove packetMove = new PacketMove(matchData.getPlayerName(), workersID.get(lastUsedWorker), true, currentPositions);
                client.send(packetMove);
                break;
            case 2:
                //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                matchData.makeGraphicalBoardEqualToBoard();
                return true;
            case 3:
                //IN CASE OF PLAYER'S CONFIRMATION WE SEND A PACKET THAT WON'T SIMULATE AND WE ARE SURE THAT IS CORRECT BECAUSE WE CHECKED POSSIBLE MOVES EVERY TIME
                PacketMove packetConfirmation = new PacketMove(matchData.getPlayerName(), workersID.get(lastUsedWorker), false, currentPositions);
                client.send(packetConfirmation);
                break;
        }

        return false;
    }

    private Integer getActionChoice(boolean makeChoiceForbidden, boolean restartForbidden, boolean confirmActionForbidden){
        String choiceMessage;
        List<Integer> mapChoices = new ArrayList<>();

        if(makeChoiceForbidden && restartForbidden && confirmActionForbidden) return -1; //IMPOSSIBLE CONFIGURATION
        else if(makeChoiceForbidden && restartForbidden) return 3;
        else if(makeChoiceForbidden && confirmActionForbidden) return 2;
        else if(restartForbidden && confirmActionForbidden) return 1;
        else if(makeChoiceForbidden){
            choiceMessage = "Do you want to restart the selection(1) or confirm the current actions(2)? ";
            mapChoices.add(2);
            mapChoices.add(3);

        }
        else if(restartForbidden){
            choiceMessage = "Do you want to make a choice(1) or confirm the current actions(2)? ";
            mapChoices.add(1);
            mapChoices.add(3);

        }
        else if(confirmActionForbidden){
            choiceMessage = "Do you want to make a choice (1), restart the selection(2)? ";
            mapChoices.add(1);
            mapChoices.add(2);

        }
        else{
            choiceMessage = "Do you want to make a choice(1), restart the selection(2) or confirm the current actions(3)? ";
            mapChoices.add(1);
            mapChoices.add(2);
            mapChoices.add(3);
        }

        Integer choice;
        do{
            System.out.print(choiceMessage);
            choice = InputUtilities.getInt("Not a valid action number, retry\nChoose an action: ");
            if (choice == null) return -1;
        }while(choice <= 0 || choice > mapChoices.size());

        return mapChoices.get(choice - 1);
    }

    private Integer getWorkerChoice(List<String> possibleWorkers, List<String> workersID){
        //FIRST WE ORDER THE LIST OF POSSIBLE WORKERS BASED ON THE ASSUMPTION THAT WORKERS' IDS ARE IN LEXICOGRAPHICAL ORDER
        possibleWorkers = possibleWorkers.stream().sorted().collect(Collectors.toList());

        Integer workerChoice = 1; //THIS IS THE DEFAULT CHOICE

        //IN CASE THERE ARE MULTIPLE CHOICES THE PLAYER CAN CHOOSE THE DESIRED WORKER
        if(possibleWorkers.size() > 1){
            List<Integer> availableWorkers = possibleWorkers.stream().map(workersID::indexOf).sorted().collect(Collectors.toList());
            do{
                System.out.print("Choose one Worker between ");
                int end = availableWorkers.size();
                int count = 0;

                //DISPLAY THE POSSIBLE CHOICES
                for(Integer index : availableWorkers){
                    count++;
                    if(count < end) System.out.print((index + 1) + ", ");
                    else System.out.print((index + 1)  + ": ");
                }

                //ASK THE CHOICE
                workerChoice = InputUtilities.getInt("Not a valid worker number, retry\nWorker number: ");
                if (workerChoice == null) return null;
            }while(!availableWorkers.contains(workerChoice - 1));
        }

        return workersID.indexOf(possibleWorkers.get(workerChoice - 1));
    }

    private Point getChosenPosition(List<Point> possiblePositions, Board board){

        StringBuilder positionsBuilder = new StringBuilder();

        //WE FIRST DISPLAY THE POSSIBLE POSITIONS EVEN IF THEY ARE ALREADY DISPLAYED GRAPHICALLY
        for(Point position : possiblePositions){
            positionsBuilder.append("- ").append(board.getCoordinates(position)).append("\n");
        }

        System.out.println("Available positions: ");
        System.out.println(positionsBuilder.toString());

        //THE PLAYER CAN NOW CHOOSE HIS WORKER'S NEXT POSITION
        String point;
        Point chosenPosition;
        boolean error = false;
        int count = 0;
        do{
            if(error) System.out.println("Invalid position for worker " + (lastUsedWorker + 1) + ", retry");

            do{
                count++;
                if(count > 1) System.out.print("Choose your next worker" + (lastUsedWorker + 1) + "'s position: ");
                else System.out.print("Choose your next worker" + (lastUsedWorker + 1) + "'s position (ex A1, B2...): ");
                point = InputUtilities.getLine();
                if(point == null) return null;
            }while(!POSITION_PATTERN.matcher(point).matches());

            chosenPosition = board.getPoint(Character.getNumericValue(point.charAt(1)), Character.toUpperCase(point.charAt(0)));
            if(board.getCell(chosenPosition) == null || !possiblePositions.contains(chosenPosition)) error = true;

        }while(board.getCell(chosenPosition) == null || !possiblePositions.contains(chosenPosition));

        return chosenPosition;
    }

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) { return false; }
}
