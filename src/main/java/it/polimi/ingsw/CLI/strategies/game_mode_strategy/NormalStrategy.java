package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy.ActionStrategy;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy.BuildActionStrategy;
import it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy.MoveActionStrategy;
import it.polimi.ingsw.packets.*;

public class NormalStrategy implements GameModeStrategy{

    private ActionStrategy actionStrategy;
    private PacketDoAction lastAction;

    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry) {
        MatchData matchData = MatchData.getInstance();
        lastAction = packetDoAction;
        switch (packetDoAction.getActionType()){
            case MOVE:
                if(isRetry) System.out.println("Retry your move!");
                actionStrategy = new MoveActionStrategy();
                PacketMove packetMove = new PacketMove(matchData.getPlayerName());
                matchData.getClient().send(packetMove);
                break;
            case BUILD:
                if(isRetry) System.out.println("Retry your build!");
                actionStrategy = new BuildActionStrategy();
                break;
            case MOVE_BUILD:
                if(isRetry) System.out.println("Retry your move or your build!");
                //SCEGLI, SETTA LA STRATEGY E POI INVIA IL PACKET
                break;
        }
    }

    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves) {
        if(actionStrategy.handleMoveAction(packetPossibleMoves)){
            handleAction(lastAction, false);
        }
    }

    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) {
        if(actionStrategy.handleBuildAction(packetPossibleBuilds)){
            handleAction(lastAction, false);
        }
    }
}

/*public class NormalStrategy implements GameModeStrategy {

    private PacketMove currentPacketMove;
    private PacketDoAction lastAction;
    private PacketPossibleMoves lastPossibleMoves;
    Integer lastUsedWorker; //null if the action has just arrived

    @Override
    public void handleAction(PacketDoAction packetDoAction){
        ViewModel viewModel = ViewModel.getInstance();
        Client client = viewModel.getClient();
        String player = viewModel.getPlayerName();
        List<String> workersID = viewModel.getIds().get(player);
        Board board = viewModel.getBoard();
        GraphicalBoard graphicalBoard = viewModel.getGraphicalBoard();
        CharStream stream = viewModel.getStream();
        GraphicalMatchMenu graphicalMatchMenu = new GraphicalMatchMenu(stream);


        if(packetDoAction.getActionType() == ActionType.MOVE){
            if(viewModel.getCurrentActionState() == ActionState.IDLE){
                lastAction = packetDoAction;
                List<Point> playerMove = new LinkedList<>();
                //ASK GET POSSIBLE MOVES
                PacketMove packetMove = new PacketMove(viewModel.getPlayerName(), workersID.get(0), playerMove);
                client.send(packetMove);
                return;
            }
            else if(viewModel.getCurrentActionState() == ActionState.POSSIBLE_RECEIVED){

                List<String> possibleWorkers = new ArrayList<>(lastPossibleMoves.getPossibleMoves().keySet());

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
                            if (workerChoice == null) return;
                        }
                    }while(!availableWorkers.contains(workerChoice - 1));
                }

                lastUsedWorker = workersID.indexOf(possibleWorkers.get(workerChoice));
                viewModel.setCurrentActionState(ActionState.IN_ACTION);
            }

            if(viewModel.getCurrentActionState() == ActionState.IN_ACTION){
                List<Point> possiblePositions = new ArrayList<>(lastPossibleMoves.getPossibleMoves().get(workersID.get(lastUsedWorker)));
                graphicalBoard.setPossibleActions(possiblePositions);
                GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
                graphicalOcean.draw();
                graphicalBoard.draw();
                graphicalMatchMenu.draw();
                stream.print(System.out);
                stream.reset();

                Integer choice;

                do{
                    System.out.println("Do you want to make a choice (1), restart the selection(2) or confirm the current actions(3)? ");
                    choice = InputUtilities.getInt();
                    if (choice == null) return;
                }while(choice != 1 && choice != 2 && choice != 3);
            }
        }

        if(packetDoAction.getActionType() == ActionType.MOVE){
            if(lastPossibleMoves == null){
                lastAction = packetDoAction;
                List<Point> playerMove = new LinkedList<>();
                PacketMove packetMove = new PacketMove(viewModel.getPlayerName(), workersID.get(0), playerMove);
                client.send(packetMove);
                return;
            }
            if(!lastPossibleMoves.getPossibleMoves().isEmpty()){
                Map<String, Set<Point>> possibleMoves = lastPossibleMoves.getPossibleMoves();
                Set<Point> possiblePositions;

                String selectedWorker;

                if(lastUsedWorker == null){
                    Integer workerChoice;
                    do{
                        System.out.print("Which worker do you want to use (1 || 2): ");
                        workerChoice = InputUtilities.getInt();
                        if (workerChoice == null) return;
                        selectedWorker = workersID.get(workerChoice - 1);
                        possiblePositions = possibleMoves.get(selectedWorker);
                    }while(workerChoice != 1 && workerChoice != 2 && possiblePositions.isEmpty());
                }
                else{
                    selectedWorker = workersID.get(lastUsedWorker - 1);
                }

                possiblePositions = possibleMoves.get(selectedWorker);
                graphicalBoard.setPossibleActions(possiblePositions);
                GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
                graphicalOcean.draw();
                graphicalBoard.draw();
                graphicalMatchMenu.draw();
                stream.print(System.out);
                stream.reset();

                Integer choice;

                do{
                    System.out.println("Do you want to make a choice (1), restart the selection(2) or confirm the current actions(3)? ");
                    choice = InputUtilities.getInt();
                    if (choice == null) return;
                }while(choice != 1 && choice != 2 && choice != 3);

                if(choice == 2){
                    //RESET THE GRAPHICAL BOARD
                    viewModel.makeGraphicalBoardEqualToBoard();
                    handlePossibleMoves(null);
                    return;
                }
                else if(choice == 3){
                    PacketPossibleMoves packetPossibleStop = new PacketPossibleMoves(player, new HashMap<>());
                    handlePossibleMoves(packetPossibleStop);
                    return;
                }

                Point helper;
                do{
                    System.out.println("Choose your next position with Worker" + lastUsedWorker);
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
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves){
        ViewModel viewModel = ViewModel.getInstance();
        viewModel.setCurrentActionState(ActionState.POSSIBLE_RECEIVED);
        lastPossibleMoves = packetPossibleMoves;
        handleAction(lastAction);
    }
}*/
