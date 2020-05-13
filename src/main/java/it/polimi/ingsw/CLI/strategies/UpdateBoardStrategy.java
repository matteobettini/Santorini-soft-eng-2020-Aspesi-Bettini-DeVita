package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.packets.PacketUpdateBoard;

public interface UpdateBoardStrategy {
    /**
     * This handler updates the board and the graphical one. It eventually displays winner/loser in the GraphicalMatchMenu.
     * @param packetUpdateBoard is the packet containing the new workers' positions and , new buildings and eventually the winner/loser.
     */
    void handleUpdateBoard(PacketUpdateBoard packetUpdateBoard);
}
