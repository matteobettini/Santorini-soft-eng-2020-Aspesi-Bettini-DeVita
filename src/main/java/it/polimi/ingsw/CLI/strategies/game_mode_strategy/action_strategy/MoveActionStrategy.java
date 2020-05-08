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
import java.util.stream.Collectors;

public class MoveActionStrategy implements ActionStrategy{
    private List<Point> currentPositions;
    private Integer lastUsedWorker; //null if the action has just arrived

    public MoveActionStrategy(){
        this.currentPositions = new LinkedList<>();
        this.lastUsedWorker = null;
    }

    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        ViewModel viewModel = ViewModel.getInstance();
        Client client = viewModel.getClient();
        String player = viewModel.getPlayerName();
        List<String> workersID = viewModel.getIds().get(player);
        Board board = viewModel.getBoard();
        GraphicalBoard graphicalBoard = viewModel.getGraphicalBoard();
        CharStream stream = viewModel.getStream();
        GraphicalMatchMenu graphicalMatchMenu = new GraphicalMatchMenu(stream);

        if(lastUsedWorker == null){

            List<String> possibleWorkers = new ArrayList<>(packetPossibleMoves.getPossibleMoves().keySet());

            Integer workerChoice = 0;

            if(possibleWorkers.size() > 1){
                List<Integer> availableWorkers = possibleWorkers.stream().map(workersID::indexOf).collect(Collectors.toList());
                do{
                    System.out.print("Choose one Worker between ");
                    int end = availableWorkers.size();
                    int count = 0;
                    for(Integer index : availableWorkers){
                        count++;
                        if(count < end) System.out.print((index + 1) + ", ");
                        else System.out.print((index + 1)  + ": ");
                        workerChoice = InputUtilities.getInt();
                        if (workerChoice == null) return false;
                    }
                }while(!availableWorkers.contains(workerChoice - 1));
            }

            lastUsedWorker = workersID.indexOf(possibleWorkers.get(workerChoice));
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
        boolean choice1Forbidden = false;
        if(possiblePositions.isEmpty()) choice1Forbidden = true;
        do{
            if(!choice1Forbidden) System.out.println("Do you want to make a choice (1), restart the selection(2) or confirm the current actions(3)? ");
            else System.out.println("Do you want to restart the selection(1) confirm the current actions(2)? ");
            choice = InputUtilities.getInt();
            if (choice == null) return false;
            if(choice1Forbidden){
                if(choice == 1 || choice == 2) choice++;
                else choice = 0;
            }
        }while(choice != 1 && choice != 2 && choice != 3);

        switch(choice){
            case 1:
                StringBuilder positionsBuilder = new StringBuilder();

                for(Point position : possiblePositions){
                    positionsBuilder.append("- ").append(board.getCoordinates(position)).append("\n");
                }

                System.out.println("Available positions: ");
                System.out.println(positionsBuilder.toString());

                Integer cordX;
                String cordY;
                Point chosenPosition;
                boolean error = false;
                do{
                    if(error) System.out.println("Invalid position!");
                    else System.out.println("Choose your next worker" + (lastUsedWorker + 1) + "'s position:");
                    System.out.print("X: ");
                    cordX = InputUtilities.getInt();
                    if(cordX == null) return false;
                    System.out.print("Y: ");
                    cordY = InputUtilities.getLine();
                    if (cordY == null) return false;
                    char y = cordY.charAt(0);
                    chosenPosition = board.getPoint(cordX, y);
                    if(board.getCell(chosenPosition) == null || !possiblePositions.contains(chosenPosition)) error = true;
                }while(board.getCell(chosenPosition) == null || !possiblePositions.contains(chosenPosition));

                currentPositions.add(chosenPosition);
                graphicalBoard.resetPossibleActions();
                Point previousPosition = graphicalBoard.removeWorker(viewModel.getPlayerName(), lastUsedWorker);
                //IF THERE IS A WORKER OF ANOTHER PLAYER IN THE NEXT POSITION WE SWAP IT
                if(previousPosition != null && graphicalBoard.getCell(chosenPosition).getWorker() != null){
                    String playerNameToSwap = graphicalBoard.getCell(chosenPosition).getWorker().getPlayerName();
                    Integer numberWorkerToSwap = graphicalBoard.getCell(chosenPosition).getWorker().getNumber();
                    String workerIdToSwap = viewModel.getIds().get(playerNameToSwap).get(numberWorkerToSwap);
                    graphicalBoard.getCell(previousPosition).setWorker(workerIdToSwap);
                }

                graphicalBoard.getCell(chosenPosition).setWorker(workersID.get(lastUsedWorker));

                PacketMove packetMove = new PacketMove(viewModel.getPlayerName(), workersID.get(lastUsedWorker), true, currentPositions);
                client.send(packetMove);
                break;
            case 2:
                viewModel.makeGraphicalBoardEqualToBoard();
                return true;
            case 3:
                PacketMove packetConfirmation = new PacketMove(viewModel.getPlayerName(), workersID.get(lastUsedWorker), currentPositions);
                client.send(packetConfirmation);
                break;
        }

        return false;
    }

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) { return false; }
}
