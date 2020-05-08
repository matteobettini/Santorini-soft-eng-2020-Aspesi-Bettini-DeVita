package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.PacketUpdateBoard;

import java.awt.*;

public class DefaultUpdateBoardStrategy implements UpdateBoardStrategy {
    @Override
    public void handleUpdateBoard(PacketUpdateBoard packetUpdateBoard) {

        MatchData matchData = MatchData.getInstance();
        Board board = matchData.getBoard();
        GraphicalBoard graphicalBoard = matchData.getGraphicalBoard();
        boolean youWin = false;
        boolean gameOver = false;

        //FIRST WE UPDATE BOTH THE BOARD AND THE GRAPHICAL ONE
        if(packetUpdateBoard.getNewBuildings() != null){
            for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
                for(BuildingType building : packetUpdateBoard.getNewBuildings().get(pos)){
                    board.getCell(pos).addBuilding(building);
                    //graphicalBoard.getCell(pos).addBuilding(building);
                    matchData.decrementCounter(building, 1);
                }
            }

        }

        //SET UPDATED WORKERS' POSITIONS
        if(packetUpdateBoard.getWorkersPositions() != null){
            //RESET WORKERS' POSITIONS
            board.resetWorkers();
            //graphicalBoard.resetWorkers();

            for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
                board.getCell(packetUpdateBoard.getWorkersPositions().get(worker)).setWorker(worker);
                //graphicalBoard.getCell(packetUpdateBoard.getWorkersPositions().get(worker)).setWorker(worker);
            }
        }

        matchData.makeGraphicalBoardEqualToBoard();

        //IF THERE IS A LOSER OR A WINNER WE SET IT
        if(packetUpdateBoard.getPlayerLostID() != null){
            String loser = packetUpdateBoard.getPlayerLostID();

            matchData.setLoser(loser);

            //WE ALSO SET IT IN THE MATCH MENU
            matchData.setLoser(loser);
            if(loser.equals(matchData.getPlayerName())) gameOver = true;
        }
        if(packetUpdateBoard.getPlayerWonID() != null){
            String winner = packetUpdateBoard.getPlayerWonID();

            matchData.setWinner(winner);

            //IF THE ACTIVE PLAYER WON WE SET YOU WIN
            if(winner.equals(matchData.getPlayerName())) youWin = true;
        }

        matchData.printMatch(youWin, gameOver);

        if(matchData.getWinner() != null){
            if(matchData.getWinner().equals(matchData.getPlayerName())) System.out.println("You have won!");
            else System.out.println(matchData.getWinner() + " has won!");
        }
        if(matchData.getLoser() != null){
            if(matchData.getLoser().equals(matchData.getPlayerName())) System.out.println("You have lost!");
            else System.out.println(matchData.getLoser() + " has lost!");

        }
    }
}
