package it.polimi.ingsw.client.cli.strategies;

import it.polimi.ingsw.common.packets.PacketUpdateBoard;

public interface UpdateBoardStrategy {
    /**
     * This handler updates the board and the graphical one. It eventually displays winner/loser in the GraphicalMatchMenu.
     * @param packetUpdateBoard is the packet containing the new workers' positions and , new buildings and eventually the winner/loser.
     */
    void handleUpdateBoard(PacketUpdateBoard packetUpdateBoard);
}
