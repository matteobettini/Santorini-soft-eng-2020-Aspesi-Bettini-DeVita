package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.CLI.Strategies.ActionStrategy;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.model.enums.ActionType;
import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketMove;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.*;
import java.util.List;

public class NormalStrategy implements ActionStrategy {
    @Override
    public void handleAction(PacketDoAction packetDoAction, PacketPossibleMoves packetPossibleMoves, PacketPossibleBuilds packetPossibleBuilds){
        ViewModel viewModel = ViewModel.getInstance();
        Client client = viewModel.getClient();
        String player = viewModel.getPlayerName();
        List<String> workersID = viewModel.getIds().get(player);
        Board board = viewModel.getBoard();
        GraphicalBoard graphicalBoard = viewModel.getGraphicalBoard();
        GraphicalMatchMenu graphicalMatchMenu = viewModel.getGraphicalMatchMenu();
        CharStream stream = viewModel.getStream();

        if(packetDoAction.getActionType() == ActionType.MOVE){
            if(packetPossibleMoves == null){
                List<Point> playerMove = new LinkedList<>();
                PacketMove packetMove = new PacketMove(viewModel.getPlayerName(), workersID.get(0), playerMove);
                client.send(packetMove);
                return;
            }
            if(!packetPossibleMoves.getPossibleMoves().isEmpty()){
                Map<String, Set<Point>> possibleMoves = packetPossibleMoves.getPossibleMoves();
                Integer workerChoice;
                Set<Point> possiblePositions;

                //FIX HERE BASED ON THE LAST USED WORKER
                do{
                    System.out.print("Which worker do you want to use (1 || 2): ");
                    workerChoice = InputUtilities.getInt();
                    if (workerChoice == null) return;
                    possiblePositions = possibleMoves.get(workersID.get(workerChoice - 1));
                }while(workerChoice != 1 && workerChoice != 2 && possiblePositions.isEmpty());

                graphicalBoard.setPossibleActions(possiblePositions);
                GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
                graphicalOcean.draw();
                graphicalBoard.draw();
                graphicalMatchMenu.draw();
                stream.print(System.out);
                stream.reset();
                Integer choice;

                //FIX HERE BASED ON THE LAST USED WORKER
                do{
                    System.out.println("Do you want to make a choice (1), select another worker(2) or confirm the current actions(3)? ");
                    choice = InputUtilities.getInt();
                    if (choice == null) return;
                }while(choice != 1 && choice != 2 && choice != 3);

                if(choice == 2){
                    handleAction(packetDoAction, packetPossibleMoves, null);
                    return;
                }
                else if(choice == 3){
                    PacketPossibleMoves packetPossibleStop = new PacketPossibleMoves(player, new HashMap<>());
                    handleAction(packetDoAction, packetPossibleStop, null);
                }

                Point helper;
                do{
                    System.out.println("Choose your worker" + (workerChoice) + "'s position:");
                    System.out.print("X: ");
                    Integer cordX = InputUtilities.getInt();
                    if(cordX == null) return;
                    System.out.print("Y: ");
                    String cordY = InputUtilities.getLine();
                    if (cordY == null) return;
                    char y = cordY.charAt(0);
                    helper = board.getPoint(cordX, y);
                }while(!possiblePositions.contains(helper));

                //SET LAST UPDATES TO THE BOARD, DRAW THE GRAPHICAL ONE AND CALL GET POSSIBLE AGAIN

            }
            else{
                //CONFIRM OR RESTART THE ACTION
            }





        }
        else if(packetDoAction.getActionType() == ActionType.BUILD){

        }
        else if(packetDoAction.getActionType() == ActionType.MOVE_BUILD){

        }
    }
}
