package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

public interface ActionStrategy {
    boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves);
    boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds);
}
