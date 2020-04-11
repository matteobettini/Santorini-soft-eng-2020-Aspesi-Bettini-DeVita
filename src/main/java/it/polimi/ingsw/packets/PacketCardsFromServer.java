package it.polimi.ingsw.packets;

import java.util.List;
import java.util.Map;

public class PacketCardsFromServer {

    private final String to;
    private final int numberToChoose;
    private final Map<String, String> allCards;
    private final List<String> availableCards;

    public PacketCardsFromServer(String to, int numberToChoose, Map<String, String> allCards, List<String> availableCards) {
        this.to = to;
        this.numberToChoose = numberToChoose;
        this.allCards = allCards;
        this.availableCards = availableCards;
    }

    public String getTo() {
        return to;
    }

    public int getNumberToChoose() {
        return numberToChoose;
    }

    public Map<String, String> getAllCards() {
        return allCards;
    }

    public List<String> getAvailableCards() {
        return availableCards;
    }
}
