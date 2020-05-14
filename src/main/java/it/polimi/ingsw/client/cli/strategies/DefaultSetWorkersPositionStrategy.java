package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.client.cli.match_data.Board;
import it.polimi.ingsw.client.cli.utilities.InputUtilities;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.common.packets.PacketWorkersPositions;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSetWorkersPositionStrategy implements SetWorkersPositionStrategy {

    /**
     * This method handles the setting of the workers on the board. It sends the chosen positions to
     * the server after having checked their correctness (but not if they are already occupied by other players).
     * If the active player is not the user, this method displays who is setting his positions.
     * @param activePlayer is the player asked to set his workers' positions.
     * @param isRetry is true if the positions are asked another time, false otherwise.
     */
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
                helper = InputUtilities.getPoint(point);
                error = board.getCell(helper) == null || positions.containsValue(helper);
            }while(error);
            positions.put(workersID.get(i), helper);
        }

        PacketWorkersPositions packetWorkersPositions = new PacketWorkersPositions(positions);
        matchData.getClient().send(packetWorkersPositions);

    }
}