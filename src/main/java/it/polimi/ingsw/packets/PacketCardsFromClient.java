package it.polimi.ingsw.packets;

import java.util.ArrayList;
import java.util.List;

public class PacketCardsFromClient {

    private final List<String> chosenCards;

    public PacketCardsFromClient(List<String> chosenCards) {
        assert(chosenCards != null);
        this.chosenCards = new ArrayList<>(chosenCards);
    }

    public List<String> getChosenCards() {
        return chosenCards;
    }
}
