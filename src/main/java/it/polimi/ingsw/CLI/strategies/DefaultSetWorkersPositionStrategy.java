package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.*;
import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DefaultSetWorkersPositionStrategy implements SetWorkersPositionStrategy {

    @Override
    public void handleSetWorkersPosition(String activePlayer, boolean isRetry) {
        MatchData matchData = MatchData.getInstance();

        if(!activePlayer.equals(matchData.getPlayerName())){
            System.out.println("\n" + activePlayer + " is setting his workers positions...");
            return;
        }

        Board board = matchData.getBoard();

        if(board.getNumberOfWorkers() == 0) OutputUtilities.printMatch();

        if(isRetry) System.out.println("One or more positions have been already occupied, try again.");

        Map<String, Point> positions = new HashMap<>();
        List<String> workersID = matchData.getIds().get(matchData.getPlayerName());
        for(int i = 0; i < workersID.size(); ++i){
            String point;
            Point helper;
            boolean error = false;
            do{
                if(error) System.out.println("Invalid position for worker " + (i + 1) + ", retry");
                do{
                    if(i > 0) System.out.print("Choose your worker" + (i + 1) + "'s position: ");
                    else System.out.print("Choose your worker" + (i + 1) + "'s position (ex A1, B2, ...): ");
                    point = InputUtilities.getLine();
                    if(point == null) return;
                }while(!InputUtilities.POSITION_PATTERN.matcher(point).matches());
                helper = board.getPoint(Character.getNumericValue(point.charAt(1)), Character.toUpperCase(point.charAt(0)));
                error = board.getCell(helper) == null || positions.containsValue(helper);
            }while(error);
            positions.put(workersID.get(i), helper);
        }

        PacketWorkersPositions packetWorkersPositions = new PacketWorkersPositions(positions);
        matchData.getClient().send(packetWorkersPositions);

    }
}