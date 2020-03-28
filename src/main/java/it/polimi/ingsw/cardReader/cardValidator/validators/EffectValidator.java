package it.polimi.ingsw.cardReader.cardValidator.validators;

import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.cardValidator.exceptions.InvalidRuleEffectException;

/**
 * This class offers a method to validate card effect's semantic
 */
public abstract class EffectValidator {

    /**
     * Validate card effect semantic
     * @param effect Effect to be validated
     * @throws InvalidRuleEffectException If problems are found during the scan.
     *                                In the message it's always reported the reason.
     */
    public static void checkRuleEffect(RuleEffect effect) throws InvalidRuleEffectException {
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void allowValidate(RuleEffect effect) throws InvalidRuleEffectException {
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void denyValidate(RuleEffect effect) throws InvalidRuleEffectException {
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void winValidate(RuleEffect effect)  throws InvalidRuleEffectException{
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void setOpponentPositionValidate(RuleEffect effect) throws InvalidRuleEffectException {
        // TODO implement here
    }
}
