package it.polimi.ingsw.client.gui.strategies;

import it.polimi.ingsw.common.packets.PacketPossibleMoves;

public interface MakeMoveStrategy extends InteractionStrategy{
    void handleMoveAction(String activePlayer, boolean isRetry);
    void handlePossibleActions(PacketPossibleMoves data);
}
