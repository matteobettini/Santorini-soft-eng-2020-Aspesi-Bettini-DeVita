package it.polimi.ingsw.client.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.common.packets.PacketDoAction;
import it.polimi.ingsw.common.packets.PacketPossibleBuilds;
import it.polimi.ingsw.common.packets.PacketPossibleMoves;

public interface GameModeStrategy {
    /**
     * This method will call the methods that will ask for the possible actions based on the ActionType.
     * @param packetDoAction is the packet containing the ActionType and its receiver.
     * @param isRetry is true if the action is requested another time, false otherwise.
     */
    void handleAction(PacketDoAction packetDoAction, boolean isRetry);

    /**
     * This method calls the handler of the move action implemented in the ActionStrategy interface.
     * If the handler of the Action Strategy returns true the entire action is repeated.
     * @param packetPossibleMoves is the packet containing the workers' ids and their possible moves.
     */
    void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves);
    /**
     * This method calls the handler of the build action implemented in the ActionStrategy interface.
     * If the handler of the Action Strategy returns true the entire action is repeated.
     * @param packetPossibleBuilds is the packet containing the workers' ids and their possible builds.
     */
    void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds);
}
