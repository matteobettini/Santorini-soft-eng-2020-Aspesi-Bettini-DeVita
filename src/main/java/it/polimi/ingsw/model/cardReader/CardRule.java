package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.enums.TriggerType;

import java.util.List;

public interface CardRule {
    /**
     * Getter for the trigger of this rule
     * @return Enum value corresponding to the trigger of this rule
     */
    TriggerType getTrigger();

    /**
     * Getter for this card rule statements
     * @return List of all the statements of the card
     */
    List<RuleStatement> getStatements();
    /**
     * Getter for the effect of this rule
     * @return Object RuleEffect containing effect data of this rule
     */
    RuleEffect getEffect();
}
