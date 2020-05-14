package it.polimi.ingsw.client.CLI.strategies;

import it.polimi.ingsw.client.CLI.CLI;
import it.polimi.ingsw.common.packets.PacketMatchStarted;

public interface MatchStartedStrategy {
    /**
     * This handler receives a PacketMatchStarted and sets/displays the info (players and game-mode).
     * @param packetMatchStarted is the packet containing the info.
     * @param cli is the instance of CLI used to set its strategies if necessary.
     */
    void handleMatchStarted(PacketMatchStarted packetMatchStarted, CLI cli);
}
