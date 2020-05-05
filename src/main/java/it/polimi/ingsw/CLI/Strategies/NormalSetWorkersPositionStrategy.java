package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalSetWorkersPositionStrategy implements SetWorkersPositionStrategy {
    @Override
    public void handleSetWorkersPosition(CLI cli) {
        CharStream stream = cli.getStream();
        Board board = cli.getBoard();
        GraphicalMatchMenu graphicalMatchMenu = cli.getGraphicalMatchMenu();
        GraphicalBoard graphicalBoard = cli.getGraphicalBoard();

        GraphicalOcean graphicalOcean = new GraphicalOcean(stream,159, 50);
        graphicalOcean.draw();
        graphicalBoard.draw();
        graphicalMatchMenu.draw();
        stream.print(System.out);
        stream.reset();

        Map<String, Point> positions = new HashMap<>();
        List<String> workersID = board.getIds().get(board.getPlayerName());
        for(int i = 1; i <= 2; ++i){
            String pos;
            List<String> coordinates;
            System.out.print("Choose your worker" + i + "'s position" + (i == 1 ? " (ex. 1, 2)" : "") + ": ");
            pos = InputUtilities.getLine();
            if(pos == null) return;
            coordinates = Arrays.asList(pos.split("\\s*,\\s*"));
            if(coordinates.size() == 2){
                int x = Integer.parseInt(coordinates.get(0));
                int y = Integer.parseInt(coordinates.get(1));
                Point helper = new Point(x, y);
                positions.put(workersID.get(i - 1), helper);
            }
        }

        PacketWorkersPositions packetWorkersPositions = new PacketWorkersPositions(positions);
        cli.getClient().send(packetWorkersPositions);

    }
}
