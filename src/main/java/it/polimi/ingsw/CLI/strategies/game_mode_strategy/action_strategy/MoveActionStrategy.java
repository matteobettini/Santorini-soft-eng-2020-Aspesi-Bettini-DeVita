package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.CLI.Board;
import it.polimi.ingsw.CLI.GraphicalBoard;
import it.polimi.ingsw.CLI.InputUtilities;
import it.polimi.ingsw.CLI.MatchData;
import it.polimi.ingsw.Client;
import it.polimi.ingsw.packets.PacketMove;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

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
        String player = matchData.getPlayerName();

        //ELEMENT USED TO DISPLAY CHANGES
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();

        boolean restartForbidden = false; //FALSE IF THE PLAYER CAN CHOSE THE WORKER AGAIN
        boolean makeChoiceForbidden = false; //TRUE IF THE PLAYER CAN'T MAKE A CHOICE BECAUSE THERE ARE NO POSSIBLE MOVES
        boolean confirmActionForbidden = false; //TRUE IF THE PLAYER CAN'T CONFIRM THE ACTION SINCE HE HAS NOT CHOSEN A WORKER

        //THIS IF IS ACCESSED WHEN THE PLAYER HAS NOT ALREADY CHOOSE THE WORKER
        if(lastUsedWorker == null){

            System.out.println("Make your move!");

            //IF THE PLAYER HAS NOT CHOSEN A WORKER HE CAN'T CONFIRM AN EMPTY MOVE
            confirmActionForbidden = true;

            List<String> possibleWorkers = new ArrayList<>();

            for(String worker : packetPossibleMoves.getPossibleMoves().keySet()){
                if(!packetPossibleMoves.getPossibleMoves().get(worker).isEmpty()) possibleWorkers.add(worker);
            }

            lastUsedWorker = InputUtilities.getWorkerChoice(possibleWorkers);
            if(lastUsedWorker == null) return false;
        }


        //POSSIBLE POSITIONS CONTAINS THE POSITIONS THAT THE PLAYER CAN CHOOSE AT THIS TIME DURING THE MOVE
        List<Point> possiblePositions = new ArrayList<>(packetPossibleMoves.getPossibleMoves().get(lastUsedWorker));

        graphicalBoard.setPossibleActions(possiblePositions);

        matchData.printMatch();

        graphicalBoard.resetPossibleActions();

        //IF THERE ARE NO POSSIBLE POSITIONS THE PLAYER CAN'T MAKE A CHOICE
        if(possiblePositions.isEmpty()) makeChoiceForbidden = true;

        Integer choice = InputUtilities.getActionChoice(makeChoiceForbidden,restartForbidden, confirmActionForbidden);
        if(choice == -1) return false;

        switch(choice){
            case 1:
                //FIRST WE GET THE PLAYER CHOICE
                Point chosenPosition = getChosenPosition(possiblePositions, board);
                if(chosenPosition ==  null) return false;
                //THE CHOSEN POSITION IS ADDED TO CURRENT POSITIONS THAT WILL FORM THE PACKET CONFIRMATION
                currentPositions.add(chosenPosition);

                Integer workerNumber = Character.getNumericValue(lastUsedWorker.charAt(lastUsedWorker.length() - 1));

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
                matchData.printMatch();
                return true;
            case 3:
                //IN CASE OF PLAYER'S CONFIRMATION WE SEND A PACKET THAT WON'T SIMULATE AND WE ARE SURE THAT IS CORRECT BECAUSE WE CHECKED POSSIBLE MOVES EVERY TIME
                PacketMove packetConfirmation = new PacketMove(matchData.getPlayerName(),lastUsedWorker, false, currentPositions);
                client.send(packetConfirmation);
                break;
        }

        return false;
    }

    private Point getChosenPosition(List<Point> possiblePositions, Board board){
        return InputUtilities.getChosenPosition(possiblePositions, board, lastUsedWorker);
    }

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) { return false; }
}
