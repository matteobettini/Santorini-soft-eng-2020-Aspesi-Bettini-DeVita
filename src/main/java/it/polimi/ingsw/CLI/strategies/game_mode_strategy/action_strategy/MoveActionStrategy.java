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
    private static final String POSITIONS_REGEXP = "([A-E][1-5])";
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
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();
        CharStream stream = matchData.getStream();
        GraphicalMatchMenu graphicalMatchMenu = new GraphicalMatchMenu(stream);

        boolean restartForbidden = false; //CHOICE 1
        boolean makeChoiceForbidden = false; //CHOICE 2
        boolean confirmActionForbidden = false; //CHOICE 3

        if(lastUsedWorker == null){

            confirmActionForbidden = true;

            List<String> possibleWorkers = new ArrayList<>(packetPossibleMoves.getPossibleMoves().keySet());

            possibleWorkers = possibleWorkers.stream().sorted().collect(Collectors.toList());

            Integer workerChoice = 0;

            if(possibleWorkers.size() > 1){
                List<Integer> availableWorkers = possibleWorkers.stream().map(workersID::indexOf).sorted().collect(Collectors.toList());
                do{
                    System.out.print("Choose one Worker between ");
                    int end = availableWorkers.size();
                    int count = 0;
                    for(Integer index : availableWorkers){
                        count++;
                        if(count < end) System.out.print((index + 1) + ", ");
                        else System.out.print((index + 1)  + ": ");
                    }
                    workerChoice = InputUtilities.getInt("Not a valid worker number, retry\nWorker number: ");
                    if (workerChoice == null) return false;
                }while(!availableWorkers.contains(workerChoice - 1));
            }

            lastUsedWorker = workersID.indexOf(possibleWorkers.get(workerChoice - 1));
        }

        List<Point> possiblePositions = new ArrayList<>(packetPossibleMoves.getPossibleMoves().get(workersID.get(lastUsedWorker)));
        graphicalBoard.setPossibleActions(possiblePositions);
        GraphicalOcean graphicalOcean = new GraphicalOcean(stream,stream.getWidth(), stream.getHeight());
        graphicalOcean.draw();
        graphicalBoard.draw();
        graphicalMatchMenu.draw();
        stream.print(System.out);
        stream.reset();

        Integer choice;
        if(possiblePositions.isEmpty()) makeChoiceForbidden = true;

        choice = getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);
        if(choice == -1) return false;

        switch(choice){
            case 1:
                StringBuilder positionsBuilder = new StringBuilder();

                for(Point position : possiblePositions){
                    positionsBuilder.append("- ").append(board.getCoordinates(position)).append("\n");
                }

                System.out.println("Available positions: ");
                System.out.println(positionsBuilder.toString());

                String point;
                Point chosenPosition;
                boolean error = false;
                do{
                    if(error) System.out.println("Invalid position for worker " + (lastUsedWorker + 1) + ", retry");
                    int count = 0;
                    do{
                        count++;
                        if(count > 1) System.out.print("Choose your next worker" + (lastUsedWorker + 1) + "'s position: ");
                        else System.out.print("Choose your next worker" + (lastUsedWorker + 1) + "'s position (ex A1, B2...): ");
                        point = InputUtilities.getLine();
                        if(point == null) return false;
                    }while(!POSITION_PATTERN.matcher(point).matches());
                    chosenPosition = board.getPoint(Character.getNumericValue(point.charAt(1)), point.charAt(0));
                    if(board.getCell(chosenPosition) == null || !possiblePositions.contains(chosenPosition)) error = true;
                }while(board.getCell(chosenPosition) == null || !possiblePositions.contains(chosenPosition));

                currentPositions.add(chosenPosition);
                graphicalBoard.resetPossibleActions();
                Point previousPosition = graphicalBoard.removeWorker(matchData.getPlayerName(), lastUsedWorker + 1);
                //IF THERE IS A WORKER OF ANOTHER PLAYER IN THE NEXT POSITION WE SWAP IT
                if(previousPosition != null && graphicalBoard.getCell(chosenPosition).getWorker() != null){
                    String playerNameToSwap = graphicalBoard.getCell(chosenPosition).getWorker().getPlayerName();
                    Integer numberWorkerToSwap = graphicalBoard.getCell(chosenPosition).getWorker().getNumber();
                    String workerIdToSwap = matchData.getIds().get(playerNameToSwap).get(numberWorkerToSwap);
                    graphicalBoard.getCell(previousPosition).setWorker(workerIdToSwap);
                }

                graphicalBoard.getCell(chosenPosition).setWorker(workersID.get(lastUsedWorker));

                PacketMove packetMove = new PacketMove(matchData.getPlayerName(), workersID.get(lastUsedWorker), true, currentPositions);
                client.send(packetMove);
                break;
            case 2:
                matchData.makeGraphicalBoardEqualToBoard();
                return true;
            case 3:
                PacketMove packetConfirmation = new PacketMove(matchData.getPlayerName(), workersID.get(lastUsedWorker), false, currentPositions);
                client.send(packetConfirmation);
                break;
        }

        return false;
    }

    private Integer getActionChoice(boolean makeChoiceForbidden, boolean restartForbidden, boolean confirmActionForbidden){
        Integer choice;
        if(makeChoiceForbidden && restartForbidden && confirmActionForbidden) return -1; //IMPOSSIBLE CONFIGURATION
        else if(makeChoiceForbidden && restartForbidden) return 3;
        else if(makeChoiceForbidden && confirmActionForbidden) return 2;
        else if(restartForbidden && confirmActionForbidden) return 1;
        else if(makeChoiceForbidden){
            do{
                System.out.println("Do you want to restart the selection(1) or confirm the current actions(2)? ");
                choice = InputUtilities.getInt("Not a valid action number, retry\nChoose an action: ");
                if (choice == null) return -1;
            }while(choice != 1 && choice != 2);
            if(choice == 1) return 2;
            else return 3;
        }
        else if(restartForbidden){
            do{
                System.out.println("Do you want to make a choice(1) or confirm the current actions(2)? ");
                choice = InputUtilities.getInt("Not a valid action number, retry\nChoose an action: ");
                if (choice == null) return -1;
            }while(choice != 1 && choice != 2);
            if(choice == 1) return 1;
            else return 3;
        }
        else if(confirmActionForbidden){
            do{
                System.out.println("Do you want to make a choice (1), restart the selection(2)? ");
                choice = InputUtilities.getInt("Not a valid action number, retry\nChoose an action: ");
                if (choice == null) return -1;
            }while(choice != 1 && choice != 2);
            if(choice == 1) return 1;
            else return 2;
        }
        else return -1;
    }

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) { return false; }
}
