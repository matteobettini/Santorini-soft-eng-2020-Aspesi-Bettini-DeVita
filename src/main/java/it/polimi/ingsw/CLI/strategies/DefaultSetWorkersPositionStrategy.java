package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSetWorkersPositionStrategy implements SetWorkersPositionStrategy {
    @Override
    public void handleSetWorkersPosition(boolean isRetry) {
        ViewModel viewModel = ViewModel.getInstance();

        CharStream stream = viewModel.getStream();
        Board board = viewModel.getBoard();
        GraphicalMatchMenu graphicalMatchMenu = new GraphicalMatchMenu(stream);
        GraphicalBoard graphicalBoard = viewModel.getGraphicalBoard();

        if(board.getNumberOfWorkers() == 0){
            GraphicalOcean graphicalOcean = new GraphicalOcean(stream,stream.getWidth(), stream.getHeight());
            graphicalOcean.draw();
            graphicalBoard.draw();
            graphicalMatchMenu.draw();
            stream.print(System.out);
            stream.reset();
        }

        Map<String, Point> positions = new HashMap<>();
        List<String> workersID = viewModel.getIds().get(viewModel.getPlayerName());
        for(int i = 0; i < workersID.size(); ++i){
            Integer cordX;
            String cordY;
            Point helper;
            boolean error = false;
            do{
                if(error) System.out.println("Invalid position!");
                else System.out.println("Choose your worker" + (i + 1) + "'s position:");
                System.out.print("X: ");
                cordX = InputUtilities.getInt();
                if(cordX == null) return;
                System.out.print("Y: ");
                cordY = InputUtilities.getLine();
                if (cordY == null) return;
                char y = cordY.charAt(0);
                helper = board.getPoint(cordX, y);
                if(board.getCell(helper) == null || positions.containsValue(helper)) error = true;
            }while(board.getCell(helper) == null || positions.containsValue(helper));
            positions.put(workersID.get(i), helper);
        }

        PacketWorkersPositions packetWorkersPositions = new PacketWorkersPositions(positions);
        viewModel.getClient().send(packetWorkersPositions);

    }
}