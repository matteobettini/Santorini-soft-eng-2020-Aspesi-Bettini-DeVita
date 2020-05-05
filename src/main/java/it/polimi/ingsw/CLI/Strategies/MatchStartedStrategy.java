package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.packets.PacketMatchStarted;

public interface MatchStartedStrategy {
    public void handleMatchStarted(PacketMatchStarted packetMatchStarted, CLI cli);
}
