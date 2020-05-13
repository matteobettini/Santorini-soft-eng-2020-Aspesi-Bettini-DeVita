package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

public interface ActionStrategy {
    /**
     * This method is called when a packet with the possible moves is received.
     * @param packetPossibleMoves is the object containing the workers' ids and their possible moves.
     * @return true if the entire move restarted, false if another possible move is requested or the move is confirmed by the player.
     */
    boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves);

    /**
     * This method is called when a packet with the possible builds is received.
     * @param packetPossibleBuilds is the object containing the workers' ids and their possible builds.
     * @return true if the entire build restarted, false if another possible build is requested or the build is confirmed by the player.
     */
    boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds);
}
