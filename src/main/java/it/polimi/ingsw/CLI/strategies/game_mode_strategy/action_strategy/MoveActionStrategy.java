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

public class MoveActionStrategy implements ActionStrategy{

    private List<Point> currentPositions;
    private String lastUsedWorker; //null if the action has just arrived

    /**
     * This method is the constructor for the normal move strategy.
     * lastUsedWorker is the String containing the id of the last used Worker during the move.
     * currentPositions contains the positions reached during the player turn.
     */
    public MoveActionStrategy(){
        this.currentPositions = new LinkedList<>();
        this.lastUsedWorker = null;
    }

    /**
     * This method will guide the player trough the choice of his moves.
     * Firstly, if the lastUsedWorker is null, the player is queried on its preference based on the possible workers in the packet.
     * After the display of the possible position of the moves through the graphical board and the command line,
     * the player has three different choices:
     * - make a choice and then ask a new packet possible moves till an empty one is received.
     * - restart the entire process again.
     * - confirm the performed moves.
     * @param packetPossibleMoves is the object containing the workers' ids and their possible moves.
     * @return true if the entire action is restarted, false if the action is confirmed or another possible moves packet is requested.
     */
    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        MatchData matchData = MatchData.getInstance();
        Client client = matchData.getClient();

        //ELEMENT USED TO DISPLAY CHANGES
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE MOVES
        boolean confirmActionForbidden = false; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER


        //THIS IF IS ACCESSED WHEN THE PLAYER HAS NOT ALREADY CHOOSE THE WORKER
        if(lastUsedWorker == null){

            //IF THE PLAYER HAS NOT CHOSEN A WORKER HE CAN'T CONFIRM AN EMPTY MOVE
            confirmActionForbidden = true;

            List<String> possibleWorkers = new ArrayList<>();

            for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
                if(!packetPossibleMoves.getPossibleMoves().get(worker).isEmpty()) possibleWorkers.add(worker);
            }

            if(possibleWorkers.size() == 1) restartForbidden = true;

            lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers);
            if(lastUsedWorker == null) return false;
        }


        //POSSIBLE POSITIONS CONTAINS THE POSITIONS THAT THE PLAYER CAN CHOOSE AT THIS TIME DURING THE MOVE
        List<Point> possiblePositions = new ArrayList<>(packetPossibleMoves.getPossibleMoves().get(lastUsedWorker));

        graphicalBoard.setPossibleActions(possiblePositions);

        OutputUtilities.printMatch();

        if(currentPositions.isEmpty()) System.out.println("Make your move!");

        graphicalBoard.resetPossibleActions();

        //IF THERE ARE NO POSSIBLE POSITIONS THE PLAYER CAN'T MAKE A CHOICE
        if(possiblePositions.isEmpty()) makeChoiceForbidden = true;

        Integer choice = InputUtilities.getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);
        if(choice == -1) return false;

        switch(choice){
            case 1:
                //FIRST WE GET THE PLAYER CHOICE
                Point chosenPosition = InputUtilities.getChosenPosition(possiblePositions, lastUsedWorker);
                if(chosenPosition ==  null) return false;
                //THE CHOSEN POSITION IS ADDED TO CURRENT POSITIONS THAT WILL FORM THE PACKET CONFIRMATION
                currentPositions.add(chosenPosition);

                Integer workerNumber = matchData.getWorkerNumber(lastUsedWorker);

                //WE DISPLAY CHANGES TO THE PLAYER WITHOUT MAKING ASSUMPTIONS ABOUT HIS GOD'S POWERS
                graphicalBoard.removeWorker(matchData.getPlayerName(), workerNumber);
                graphicalBoard.getCell(chosenPosition).setWorker(lastUsedWorker);

                //WE THEN ASK FOR ANOTHER GET POSSIBLE MOVES
                PacketMove packetMove = new PacketMove(matchData.getPlayerName(), lastUsedWorker, true, currentPositions);
                client.send(packetMove);
                break;
            case 2:
                //WE RESET CHANGES TO THE GRAPHICAL BOARD, THE CHECKPOINT IS THE BOARD OBJECT IN THE MATCHDATA
                matchData.makeGraphicalBoardEqualToBoard();
                OutputUtilities.printMatch();
                return true;
            case 3:
                //IN CASE OF PLAYER'S CONFIRMATION WE SEND A PACKET THAT WON'T SIMULATE AND WE ARE SURE THAT IS CORRECT BECAUSE WE CHECKED POSSIBLE MOVES EVERY TIME
                PacketMove packetConfirmation = new PacketMove(matchData.getPlayerName(),lastUsedWorker, false, currentPositions);
                client.send(packetConfirmation);
                break;
        }

        return false;
    }

    /**
     * In case a packet of possible builds is received during the move turn it will be ignored.
     * @param packetPossibleBuilds is the object containing the workers' ids and their possible builds.
     * @return false
     */
    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) { return false; }
}
