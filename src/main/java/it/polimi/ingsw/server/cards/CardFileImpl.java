package it.polimi.ingsw.server.cards;

import it.polimi.ingsw.server.cards.enums.TriggerType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This file represents a read card.
 * Starting from this file, it's possible to compile rules
 * for matches, or analyse card behaviour.
 */
class CardFileImpl implements CardFile {

    private final String name;
    private final String description;
    private final List<CardRuleImpl> rules;
    private final List<Integer> numbersOfPlayers;

    public CardFileImpl(String name, String description, List<CardRuleImpl> rules, List<Integer> numbersOfPlayers) {
        assert (name != null && description != null && rules != null && numbersOfPlayers != null);
        this.name = name;
        this.description = description;
        this.rules = rules;
        this.numbersOfPlayers = numbersOfPlayers;
    }

    public CardFileImpl(String name, String description, List<CardRuleImpl> rules) {
        assert (name != null && description != null && rules != null);
        this.name = name;
        this.description = description;
        this.rules = rules;
        List<Integer> numbersOfPlayers = new ArrayList<>();
        numbersOfPlayers.add(2);
        numbersOfPlayers.add(3);
        numbersOfPlayers.add(4);
        this.numbersOfPlayers = numbersOfPlayers;
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
     * Getter for the card instance possible numbers of players
     * @return a List of Integer containing the possible numbers of players
     */
    public List<Integer> getNumbersOfPlayers() { return new ArrayList<>(this.numbersOfPlayers); }

    /**
     * Getter for this card's rules
     * @return List of all the rules of the card
     */
    public List<CardRule> getRules(){
        return new LinkedList<>(this.rules);
    }
    public List<CardRuleImpl> getRulesInternal(){
        return new LinkedList<>(this.rules);
    }

    /**
     * Getter for this card's rules that have a specific trigger
     * @param trigger Trigger for the returned rules
     * @return List of all the rules of the card that have this trigger
     */
    public List<CardRule> getRules(TriggerType trigger){
        return this.rules.stream().filter(r->r.getTrigger() == trigger).collect(Collectors.toList());
    }
    public List<CardRuleImpl> getRulesInternal(TriggerType trigger){
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
        CardFileImpl cardFile = (CardFileImpl) o;
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
