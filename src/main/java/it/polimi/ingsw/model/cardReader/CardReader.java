package it.polimi.ingsw.model.cardReader;

import it.polimi.ingsw.model.cardReader.exceptions.InvalidCardException;

/**
 * This class permit read a card from a file.
 * The card returned (in form of CardFile) is completely checked syntactically and semantically
 */
public abstract class CardReader {

    /**
     * Read a card from an XML file
     * @param defaultCard Default strategy card, in the form of CardFile
     * @param file Path where the card's XML is placed
     * @return CardFile of the indicated card
     * @throws InvalidCardException If card has problems syntactically or semantically.
     *                              It's always indicated the cause as message
     */
    public abstract CardFile readCard(CardFile defaultCard, String file) throws InvalidCardException;
}
