package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.client.cli.match_data.Board;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.utilities.CharStream;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.common.enums.BuildingType;
import it.polimi.ingsw.common.packets.PacketUpdateBoard;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class DefaultUpdateBoardStrategy implements UpdateBoardStrategy {

    /**
     * This method updates the board. It eventually makes the graphical board in line with the last updates to the board.
     * If there is a winner or a loser it sets the info in the MatchData and then displays a message with the winner/loser through
     * the GraphicalMatchMenu.
     * @param packetUpdateBoard is the packet containing the updated workers positions, new buildings and in some cases the winner/loser.
     */
    @Override
    public void handleUpdateBoard(PacketUpdateBoard packetUpdateBoard) {

        MatchData matchData = MatchData.getInstance();
        Board board = matchData.getBoard();
        boolean youWin = false;
        boolean gameOver = false;

        Map<Point, List<BuildingType>> newBuildings = packetUpdateBoard.getNewBuildings();

        //FIRST WE UPDATE THE BOARD
        if(newBuildings != null){
            for(Point pos : newBuildings.keySet()){
                for(BuildingType building : newBuildings.get(pos)){
                    board.getCell(pos).addBuilding(building);
                    matchData.decrementCounter(building, 1);
                }
            }

        }

        Map<String, Point> workersPositions = packetUpdateBoard.getWorkersPositions();

        //SET UPDATED WORKERS' POSITIONS
        if(workersPositions != null){
            //RESET WORKERS' POSITIONS
            board.resetWorkers();

            for(String worker : workersPositions.keySet())
                board.getCell(workersPositions.get(worker)).setWorker(worker);
        }

        String loser = packetUpdateBoard.getPlayerLostID();

        //IF THERE IS A LOSER OR A WINNER WE SET IT
        if(loser != null){

            //WE SET IT IN THE MATCH MENU
            matchData.addLoser(loser);
            if(loser.equals(matchData.getPlayerName())) gameOver = true;

            for(String workersLoser : matchData.getIds().get(loser)){
                board.getCell(board.getWorkerPosition(workersLoser)).removeWorker();
            }

        }

        String winner = packetUpdateBoard.getPlayerWonID();

        if(winner != null){

            matchData.setWinner(winner);

            //IF THE ACTIVE PLAYER WON WE SET YOU WIN
            if(winner.equals(matchData.getPlayerName())) youWin = true;
            else gameOver = true;
        }

        matchData.makeGraphicalBoardEqualToBoard();

        int playersStillInGame = matchData.getIds().size() - matchData.getLosers().size();

        if(playersStillInGame != 1 || winner != null) OutputUtilities.printMatch(youWin, gameOver);

        if(matchData.getPlayerName().equals(loser)) System.out.println("You have lost!");
        else if(loser != null) System.out.println("\n"+ OutputUtilities.fromColorToBackColor(matchData.getPlayersColor().get(loser)).getCode() +loser + " has lost!" + CharStream.ANSI_RESET);

        if(matchData.getPlayerName().equals(winner)) System.out.println("You have won!");
        else if(winner != null)System.out.println("\n"+ OutputUtilities.fromColorToBackColor(matchData.getPlayersColor().get(winner)).getCode() + winner + " has won!" + CharStream.ANSI_RESET);
    }

}
