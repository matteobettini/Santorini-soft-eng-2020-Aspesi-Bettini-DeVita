package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.packets.PacketDoAction;
import it.polimi.ingsw.packets.PacketPossibleBuilds;
import it.polimi.ingsw.packets.PacketPossibleMoves;

public interface ActionStrategy {
    public void handleAction(PacketDoAction packetDoAction, PacketPossibleMoves packetPossibleMoves, PacketPossibleBuilds packetPossibleBuilds);
}
