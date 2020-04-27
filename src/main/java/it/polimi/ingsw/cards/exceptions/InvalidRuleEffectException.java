package it.polimi.ingsw.cards.exceptions;

/**
 * This exception is thrown when a problem is found during the scan of a RuleEffect
 */
public class InvalidRuleEffectException extends Exception {
    public InvalidRuleEffectException(String message) {
        super(message);
    }
}
