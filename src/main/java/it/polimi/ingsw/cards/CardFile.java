package it.polimi.ingsw.cards;

import it.polimi.ingsw.cards.enums.TriggerType;

import java.util.List;

public interface CardFile {
    /**
     * Getter for the card instance name
     * @return Card name
     */
    String getName();
    /**
     * Getter for the card instance description
     * @return Card description
     */
    String getDescription();
    /**
     * Getter for this card's rules
     * @return List of all the rules of the card
     */
    List<CardRule> getRules();
    /**
     * Getter for this card's rules that have a specific trigger
     * @param trigger Trigger for the returned rules
     * @return List of all the rules of the card that have this trigger
     */
    List<CardRule> getRules(TriggerType trigger);
}
