package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

public interface GameModeStrategy {
    void handleAction(PacketDoAction packetDoAction, boolean isRetry);
    void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves);
    void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds);
}
