package it.polimi.ingsw.model;

import it.polimi.ingsw.cardReader.CardFile;
import it.polimi.ingsw.model.enums.PlayerState;

import java.util.List;

/**
 * This class contains the info about a Player. Each Player is uniquely identified by his nickname.
 * Furthermore each Player has an associated GodCard and can have one of
 * the states specified in PlayerState during his turn.
 */
public abstract class Player {

    /**
     * Getter that returns the nickname of the Player.
     * @return a String containing the nickname.
     */
    public abstract String getNickname();

    /**
     * Getter that returns the CardFile of the GodCard associated to the Player.
     * @return an instance of CardFile.
     */
    public abstract CardFile getCard();

    /**
     * Getter that returns the current state of the Player.
     * @return one of the states contained in PlayerState.
     */
    public abstract PlayerState getState();

    /**
     * Getter that returns the Workers associated to the Player.
     * @return a List of all the Workers possessed by the Player.
     */
    public abstract List<Worker> getWorkers();

    /**
     * Setter that sets the state of the Player to one of the possible states in PlayerState.
     * @param ps is the state of the Player to set.
     */
    public abstract void setPlayerState(PlayerState ps);

    /**
     * Setter that sets the GodCard associated to the Player.
     * @param c is an instance of CardFile to set.
     */
    public abstract void setCard(CardFile c);

    /**
     * This method checks if the given obj equals the Player.
     * @param obj is an instance of Object to check.
     * @return true if obj and the Player are identical, false otherwise.
     */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * This method returns a clone of the Player.
     * @return an cloned instance of the Player.
     */
    @Override
    protected abstract Player clone();
}
