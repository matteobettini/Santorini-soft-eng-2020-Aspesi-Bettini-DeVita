package it.polimi.ingsw.packets;

import javafx.util.Pair;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class PacketSetup implements Serializable {

    private static final long serialVersionUID = 6831715670161158668L;
    private final Map<String, List<String>> ids;
    private final Map<String, Color> colors;
    private final Map<String, Pair<String, String >> cards;
    private final int NUM_OF_FIRST_FLOOR;
    private final int NUM_OF_SECOND_FLOOR;
    private final int NUM_OF_THIRD_FLOOR;
    private final int NUM_OF_DOME;

    public PacketSetup(Map<String, List<String>> ids, Map<String, Color> colors, Map<String, Pair<String, String>> cards, int numFF, int numSF, int numTF, int numDM) {
        this.ids = new HashMap<>();
        for(String s : ids.keySet()){
            List<String> internalList = new ArrayList<>(ids.get(s));
            this.ids.put(s,internalList);
        }
        this.colors = new HashMap<>();
        for(String s : colors.keySet()){
            this.colors.put(s, colors.get(s));
        }

        this.cards = new HashMap<>();
        for(String s : cards.keySet()){
            Pair<String,String> internalPair = new Pair<>( cards.get(s).getKey(), cards.get(s).getValue());
            this.cards.put(s,internalPair);
        }

        NUM_OF_FIRST_FLOOR = numFF;
        NUM_OF_SECOND_FLOOR = numSF;
        NUM_OF_THIRD_FLOOR = numTF;
        NUM_OF_DOME = numDM;

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

    public int getNUM_OF_FIRST_FLOOR() {
        return NUM_OF_FIRST_FLOOR;
    }

    public int getNUM_OF_SECOND_FLOOR() {
        return NUM_OF_SECOND_FLOOR;
    }

    public int getNUM_OF_THIRD_FLOOR() {
        return NUM_OF_THIRD_FLOOR;
    }

    public int getNUM_OF_DOME() {
        return NUM_OF_DOME;
    }
}
