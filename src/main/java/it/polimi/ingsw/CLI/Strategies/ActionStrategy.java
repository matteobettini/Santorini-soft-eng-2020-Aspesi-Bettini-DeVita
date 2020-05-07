package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketPossibleMoves;

public interface ActionStrategy {
    void handleAction(PacketDoAction packetDoAction);
    void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves);
}
