package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.TriggerType;

import java.util.List;

/**
 * This file represents a read card.
 * Starting from this file, it's possible to compile rules
 * for matches, or analyse card behaviour.
 */
public abstract class CardFile {

    /**
     * Getter for the card instance name
     * @return Card name
     */
    public abstract String getName();

    /**
     * Getter for the card instance description
     * @return Card description
     */
    public abstract String getDescription();

    /**
     * Getter for this card's rules
     * @return List of all the rules of the card
     */
    public abstract List<CardRule> getRules();

    /**
     * Getter for this card's rules that have a specific trigger
     * @param trigger Trigger for the returned rules
     * @return List of all the rules of the card that have this trigger
     */
    public abstract List<CardRule> getRules(TriggerType trigger);
}
