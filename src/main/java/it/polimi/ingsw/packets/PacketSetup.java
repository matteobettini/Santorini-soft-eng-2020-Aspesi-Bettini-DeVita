package it.polimi.ingsw.packets;

import it.polimi.ingsw.model.enums.BuildingType;
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
    Map<BuildingType, Integer> buildingsCounter;

    public PacketSetup(Map<String, List<String>> ids, Map<String, Color> colors, Map<String, Pair<String, String>> cards, Map<BuildingType, Integer> buildingsCounter) {
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

        this.buildingsCounter = new HashMap<>();
        for(BuildingType buildingType : buildingsCounter.keySet()){
            this.buildingsCounter.put(buildingType, buildingsCounter.get(buildingType));
        }


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

    public Map<BuildingType, Integer> getBuildingsCounter() { return buildingsCounter; }
}
