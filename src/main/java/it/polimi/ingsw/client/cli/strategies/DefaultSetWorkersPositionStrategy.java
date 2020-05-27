package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.client.cli.match_data.Board;
import it.polimi.ingsw.client.cli.utilities.InputUtilities;
import it.polimi.ingsw.client.cli.match_data.MatchData;
import it.polimi.ingsw.client.cli.utilities.OutputUtilities;
import it.polimi.ingsw.common.enums.ActionType;
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
            OutputUtilities.displayOthersActions(ActionType.SET_WORKERS_POSITION, activePlayer, matchData.getPlayersColor().get(activePlayer));
            return;
        }

        Board board = matchData.getBoard();

        if(board.getNumberOfWorkers() == 0 || isRetry) {
            matchData.makeGraphicalBoardEqualToBoard();
            OutputUtilities.printMatch();
        }

        if(isRetry) System.out.println("\nOne or more positions have been already occupied, try again...\n");

        Map<String, Point> positions = InputUtilities.getInitialPositions();

        if(positions == null) return;


        PacketWorkersPositions packetWorkersPositions = new PacketWorkersPositions(positions);
        matchData.getClient().send(packetWorkersPositions);

    }
}