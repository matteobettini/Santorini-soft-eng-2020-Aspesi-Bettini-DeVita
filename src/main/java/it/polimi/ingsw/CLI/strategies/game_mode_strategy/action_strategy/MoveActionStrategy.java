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

    public MoveActionStrategy(){
        this.currentPositions = new LinkedList<>();
        this.lastUsedWorker = null;
    }

    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        MatchData matchData = MatchData.getInstance();
        Client client = matchData.getClient();

        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
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
                Point chosenPosition = InputUtilities.getChosenPosition(possiblePositions, board, lastUsedWorker);
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

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) { return false; }
}
