package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.TriggerType;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This file represents a read card.
 * Starting from this file, it's possible to compile rules
 * for matches, or analyse card behaviour.
 */
public class CardFile {

    private final String name;
    private final String description;
    private final List<CardRule> rules;

    public CardFile(String name, String description, List<CardRule> rules) {
        assert (name != null && description != null && rules != null);
        this.name = name;
        this.description = description;
        this.rules = rules;
    }

    /**
     * Getter for the card instance name
     * @return Card name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Getter for the card instance description
     * @return Card description
     */
    public String getDescription(){
        return this.description;
    }

    /**
     * Getter for this card's rules
     * @return List of all the rules of the card
     */
    public List<CardRule> getRules(){
        return this.rules;
    }

    /**
     * Getter for this card's rules that have a specific trigger
     * @param trigger Trigger for the returned rules
     * @return List of all the rules of the card that have this trigger
     */
    public List<CardRule> getRules(TriggerType trigger){
        return this.rules.stream().filter(r->r.getTrigger() == trigger).collect(Collectors.toList());
    }

    /**
     * Compares two CardFiles, using the internal state instead of the memory location
     * @param o Object to compare to
     * @return True if the two objects contains the same information
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardFile cardFile = (CardFile) o;
        return name.equals(cardFile.name) &&
                description.equals(cardFile.description) &&
                rules.equals(cardFile.rules);
    }

    /**
     * Return an hash code for this class, using the internal information
     * @return The generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description, rules);
    }
}
