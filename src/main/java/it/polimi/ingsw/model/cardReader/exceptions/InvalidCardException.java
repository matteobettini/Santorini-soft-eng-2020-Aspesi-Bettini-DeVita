package it.polimi.ingsw.model.cardReader.exceptions;

/**
 * This exception is thrown during reading of a card from a file,
 * if the specified file is not correct either syntactically or semantically.
 * It always carry a message indicating the problem in the file
 */
public class InvalidCardException extends Exception {
    public InvalidCardException(String message) {
        super(message);
    }
}
