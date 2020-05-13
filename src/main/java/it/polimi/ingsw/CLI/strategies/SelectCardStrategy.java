package it.polimi.ingsw.CLI.strategies;

import it.polimi.ingsw.packets.PacketCardsFromServer;

public interface SelectCardStrategy {
    /**
     * This handler receives the cards from the server and make the player choose a certain requested number of cards among them.
     * If the user is the challenger he will be asked to choose more than one card, only one otherwise.
     * @param packetCardsFromServer is the packet containing the available cards with their names and descriptions and the number of cards to choose.
     * @param isRetry is true if the cards are requested another time, false otherwise.
     */
    void handleCardStrategy(PacketCardsFromServer packetCardsFromServer,boolean isRetry);
}
