package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.model.enums.BuildingType;
import it.polimi.ingsw.packets.PacketUpdateBoard;

import java.awt.*;

public class NormalUpdateBoardStrategy implements UpdateBoardStrategy {
    @Override
    public void handleUpdateBoard(PacketUpdateBoard packetUpdateBoard, CLI cli) {
        Board board = cli.getBoard();
        GraphicalBoard graphicalBoard = cli.getGraphicalBoard();
        GraphicalMatchMenu graphicalMatchMenu = cli.getGraphicalMatchMenu();
        CharStream stream = cli.getStream();

        //FIRST WE UPDATE THE BOTH THE BOARD AND THE GRAPHICAL ONE
        if(packetUpdateBoard.getNewBuildings() != null){
            for(Point pos : packetUpdateBoard.getNewBuildings().keySet()){
                for(BuildingType building : packetUpdateBoard.getNewBuildings().get(pos)){
                    board.getCell(pos).addBuilding(building);
                    graphicalBoard.getCell(pos).addBuilding(building);
                    graphicalMatchMenu.decrementCounter(building, 1);
                }
            }

        }
        //RESET GAME OVER AND YOU WIN
        graphicalMatchMenu.setGameOver(false);
        graphicalMatchMenu.setYouWin(false);

        //SET UPDATED WORKERS' POSITIONS
        if(packetUpdateBoard.getWorkersPositions() != null){
            //RESET WORKERS' POSITIONS
            board.resetWorkers();
            graphicalBoard.resetWorkers();

            for(String worker : packetUpdateBoard.getWorkersPositions().keySet()){
                board.getCell(packetUpdateBoard.getWorkersPositions().get(worker)).setWorker(worker);

                //UPDATE FOR THE GRAPHICAL BOARD
                String workerOwner = "";
                char workerNumber = '\0';
                for(String player : board.getIds().keySet()){
                    for(int i = 1; i <= 2; ++i){
                        if(board.getIds().get(player).get(i - 1).equals(worker)){
                            if(i == 1) workerNumber = '1';
                            else workerNumber = '2';
                            workerOwner = player;
                        }
                    }

                }
                Color colorOwner = board.getPlayersColor().get(workerOwner);
                graphicalBoard.getCell(packetUpdateBoard.getWorkersPositions().get(worker)).setWorker(colorOwner, workerNumber, workerOwner);
            }
        }

        //IF THERE IS A LOSER OR A WINNER WE SET IT
        if(packetUpdateBoard.getPlayerLostID() != null){
            String loser = packetUpdateBoard.getPlayerLostID();
            board.setLoser(loser);

            //WE ALSO SET IT IN THE MATCH MENU
            graphicalMatchMenu.setLoser(loser);
            if(loser.equals(board.getPlayerName())) graphicalMatchMenu.setGameOver(true);
        }
        if(packetUpdateBoard.getPlayerWonID() != null){
            String winner = packetUpdateBoard.getPlayerWonID();
            board.setWinner(winner);

            //IF THE ACTIVE PLAYER WON WE SET YOU WIN
            if(winner.equals(board.getPlayerName())) graphicalMatchMenu.setYouWin(true);
        }


        GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
        graphicalOcean.draw();
        graphicalBoard.draw();
        graphicalMatchMenu.draw();
        stream.print(System.out);
        stream.reset();
    }
}
