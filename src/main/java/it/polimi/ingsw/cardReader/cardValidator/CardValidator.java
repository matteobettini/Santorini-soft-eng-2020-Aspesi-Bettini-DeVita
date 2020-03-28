package it.polimi.ingsw.cardReader.cardValidator;

import it.polimi.ingsw.cardReader.CardFile;
import it.polimi.ingsw.cardReader.CardRule;
import it.polimi.ingsw.cardReader.exceptions.InvalidCardException;

/**
 * This class offers a method to validate semantically a CardFile
 */
public abstract class CardValidator {

    /**
     * This method is used to validate a CardFile semantically
     * @param card CardFile to be validated
     * @throws InvalidCardException If problems are found during the scan.
     *                              In the message it's always reported the reason.
     */
    public static void checkCardFile(CardFile card) throws InvalidCardException {
        // TODO implement here
        return;
    }

    /**
     * @param rule
     * @return
     */
    private static void checkCardRule(CardRule rule) {
        // TODO implement here
        return;
    }
}
