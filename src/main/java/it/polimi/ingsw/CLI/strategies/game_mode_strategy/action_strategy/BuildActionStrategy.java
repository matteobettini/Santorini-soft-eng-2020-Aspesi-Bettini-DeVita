package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

public class BuildActionStrategy implements ActionStrategy{

    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        return false;
    }

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) {
        return false;
    }
}
