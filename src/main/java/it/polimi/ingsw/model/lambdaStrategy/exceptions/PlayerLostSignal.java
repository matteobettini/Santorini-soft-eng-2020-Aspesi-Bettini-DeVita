package it.polimi.ingsw.model.lambdaStrategy.exceptions;

import it.polimi.ingsw.model.Player;

/**
 * This signal indicates that a certain player has lost the match
 */
public class PlayerLostSignal extends Exception{

    private final Player player;

    /**
     * The constructor
     * @param player the loser
     */
    public PlayerLostSignal(Player player){
        this.player = player;
    }

    /**
     * method to get the loser
     * @return the loser
     */
    public Player getPlayer(){
        return player;
    }
}
