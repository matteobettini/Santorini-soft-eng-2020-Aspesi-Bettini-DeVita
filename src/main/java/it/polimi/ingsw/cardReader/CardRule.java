package it.polimi.ingsw.cardReader;

import it.polimi.ingsw.cardReader.enums.TriggerType;

import java.util.List;

public abstract class CardRule {

    /**
     * Getter for this card rule statements
     * @return List of all the statements of the card
     */
    public abstract List<RuleStatement> getStatements();

    /**
     * Setter for adding a statement to this rule.
     * Useful for patching runtime the rule
     * @param stm Statement to add to this rule
     */
    public abstract void addStatement(RuleStatement stm);

    /**
     * Getter for the effect of this rule
     * @return Object RuleEffect containing effect data of this rule
     */
    public abstract RuleEffect getEffect();

    /**
     * Getter for the trigger of this rule
     * @return Enum value corresponding to the trigger of this rule
     */
    public abstract TriggerType getTrigger();
}
