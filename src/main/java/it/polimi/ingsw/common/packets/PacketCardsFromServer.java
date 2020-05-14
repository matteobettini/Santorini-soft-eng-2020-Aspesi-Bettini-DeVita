package it.polimi.ingsw.common.packets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketCardsFromServer implements Serializable {

    private static final long serialVersionUID = 7440406814331172895L;
    private final String to;
    private final int numberToChoose;
    private final Map<String, String> allCards;
    private final List<String> availableCards;

    public PacketCardsFromServer(String to, int numberToChoose, Map<String, String> allCards, List<String> availableCards) {
        this.to = to;
        this.numberToChoose = numberToChoose;
        this.allCards = new HashMap<>(allCards);
        this.availableCards = new ArrayList<>(availableCards);
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
