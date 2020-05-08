package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.packets.PacketCardsFromServer;

public interface SelectCardStrategy {
    void handleCardStrategy(PacketCardsFromServer packetCardsFromServer,boolean isRetry);
}
