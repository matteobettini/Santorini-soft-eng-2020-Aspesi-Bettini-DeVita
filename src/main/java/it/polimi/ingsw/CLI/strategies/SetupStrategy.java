package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.packets.PacketSetup;

public interface SetupStrategy {
    /**
     * This handler receives a PacketSetup from the server and set all the received info in the MatchData instance.
     * @param packetSetup is the packet containing info such as the players and workers' ids, their colors,
     * the association between players and their cards and the buildings' counter.
     */
    void handleSetup(PacketSetup packetSetup);
}
