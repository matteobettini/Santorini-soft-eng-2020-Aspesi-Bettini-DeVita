package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.packets.PacketUpdateBoard;

public interface UpdateBoardStrategy {
    public void handleUpdateBoard(PacketUpdateBoard packetUpdateBoard);
}
