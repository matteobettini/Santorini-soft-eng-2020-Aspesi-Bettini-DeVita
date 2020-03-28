package it.polimi.ingsw.cardReader.cardValidator.exceptions;

/**
 * This exception is thrown when a problem is found during the scan of a RuleEffect
 */
public class InvalidEffectException extends Exception {
    public InvalidEffectException(String message) {
        super(message);
    }
}
