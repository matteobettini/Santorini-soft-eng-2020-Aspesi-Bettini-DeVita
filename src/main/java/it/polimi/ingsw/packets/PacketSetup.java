package it.polimi.ingsw.packets;

import javafx.util.Pair;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class PacketSetup {

    private final Map<String, List<String>> ids;
    private final Map<String, Color> colors;
    private final Map<String, Pair<String, String >> cards;

    public PacketSetup(Map<String, List<String>> ids, Map<String, Color> colors, Map<String, Pair<String, String>> cards) {
        this.ids = ids;
        this.colors = colors;
        this.cards = cards;
    }

    public Map<String, List<String>> getIds() {
        return ids;
    }

    public Map<String, Color> getColors() {
        return colors;
    }

    public Map<String, Pair<String, String>> getCards() {
        return cards;
    }
}
