package it.polimi.ingsw.cardReader.cardValidator.validators;

import it.polimi.ingsw.cardReader.RuleEffect;
import it.polimi.ingsw.cardReader.cardValidator.exceptions.InvalidEffectException;

/**
 * This class offers a method to validate card effect's semantic
 */
public abstract class EffectValidator {

    /**
     * Validate card effect semantic
     * @param effect Effect to be validated
     * @throws InvalidEffectException If problems are found during the scan.
     *                                In the message it's always reported the reason.
     */
    public static void checkRuleEffect(RuleEffect effect) throws InvalidEffectException {
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void allowValidate(RuleEffect effect) throws InvalidEffectException {
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void denyValidate(RuleEffect effect) throws InvalidEffectException {
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void winValidate(RuleEffect effect)  throws InvalidEffectException{
        // TODO implement here
    }

    /**
     * @param effect
     */
    private static void setOpponentPositionValidate(RuleEffect effect) throws InvalidEffectException {
        // TODO implement here
    }
}
