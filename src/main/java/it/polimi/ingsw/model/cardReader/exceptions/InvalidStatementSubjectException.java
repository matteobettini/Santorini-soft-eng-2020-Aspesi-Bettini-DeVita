package it.polimi.ingsw.model.cardReader.exceptions;

/**
 * This exception is thrown when a problem is found during the scan of the object of a RuleStatement
 */
public class InvalidStatementSubjectException extends Exception {
    public InvalidStatementSubjectException(String message) {
        super(message);
    }
}
