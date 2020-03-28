package it.polimi.ingsw.cardReader.cardValidator;

import it.polimi.ingsw.cardReader.CardFile;

/**
 * This class offers a method to patch CardFile with
 * part of the default strategy
 */
public abstract class CardPatcher {

    /**
     * This method adds missing statements/parts of the specified CardFile using
     * the default strategy
     * @param defaultCard CardFile of the default strategy
     * @param card CardFile of the card to be patched
     */
    public abstract void patchCard(CardFile defaultCard, CardFile card);
}
