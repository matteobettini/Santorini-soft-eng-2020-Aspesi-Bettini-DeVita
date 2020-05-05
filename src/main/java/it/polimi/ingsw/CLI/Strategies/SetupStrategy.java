package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.packets.PacketSetup;

public interface SetupStrategy {
    public void handleSetup(PacketSetup packetSetup, CLI cli);
}
