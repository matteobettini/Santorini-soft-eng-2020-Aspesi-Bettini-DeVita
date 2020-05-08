package it.polimi.ingsw.CLI.strategies.game_mode_strategy;

import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

public class HardcoreStrategy implements GameModeStrategy {
    @Override
    public void handleAction(PacketDoAction packetDoAction, boolean isRetry){
        //SWITCH SUL DOACTION
    }

    @Override
    public void handlePossibleMoves(PacketPossibleMoves packetPossibleMoves){ }

    @Override
    public void handlePossibleBuilds(PacketPossibleBuilds packetPossibleBuilds) { }
}
