package it.polimi.ingsw.packets;

import javafx.util.Pair;

import java.awt.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PacketSetup implements Serializable {

    private static final long serialVersionUID = 6831715670161158668L;
    private final Map<String, List<String>> ids;
    private final Map<String, Color> colors;
    private final Map<String, Pair<String, String >> cards;
    private final boolean isHardcore;

    public PacketSetup(Map<String, List<String>> ids, Map<String, Color> colors, Map<String, Pair<String, String>> cards, boolean isHardcore) {
        this.ids = ids;
        this.colors = colors;
        this.cards = cards;
        this.isHardcore = isHardcore;
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

    public boolean isHardcore() {
        return isHardcore;
    }
}
