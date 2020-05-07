package it.polimi.ingsw.CLI.Strategies;

import it.polimi.ingsw.CLI.CLI;
import it.polimi.ingsw.packets.PacketCardsFromServer;

public interface SelectCardStrategy {
    public void handleCardStrategy(PacketCardsFromServer packetCardsFromServer,boolean isRetry);
}
