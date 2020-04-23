package it.polimi.ingsw.packets;

import com.sun.source.doctree.SerialDataTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PacketCardsFromClient implements Serializable {

    private static final long serialVersionUID = 5252735296852071637L;
    private final List<String> chosenCards;

    public PacketCardsFromClient(List<String> chosenCards) {
        assert(chosenCards != null);
        this.chosenCards = new ArrayList<>(chosenCards);
    }

    public List<String> getChosenCards() {
        return chosenCards;
    }
}
