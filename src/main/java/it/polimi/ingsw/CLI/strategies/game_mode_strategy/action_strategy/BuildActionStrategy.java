package it.polimi.ingsw.CLI.strategies.game_mode_strategy.action_strategy;

import it.polimi.ingsw.packets.PacketBuild;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

import java.util.regex.Pattern;

public class BuildActionStrategy implements ActionStrategy{

    private static final String POSITIONS_REGEXP = "(([A-E]|[a-e])[1-5])";
    private static final Pattern POSITION_PATTERN = Pattern.compile(POSITIONS_REGEXP);


    @Override
    public boolean handleMoveAction(PacketPossibleMoves packetPossibleMoves) {
        return false;
    }

    @Override
    public boolean handleBuildAction(PacketPossibleBuilds packetPossibleBuilds) {












        return false;
    }
}
