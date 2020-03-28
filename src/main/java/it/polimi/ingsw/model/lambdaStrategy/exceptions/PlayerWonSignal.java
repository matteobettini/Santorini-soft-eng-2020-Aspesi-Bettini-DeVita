package it.polimi.ingsw.model.lambdaStrategy.exceptions;

import it.polimi.ingsw.model.Player;

/**
 * This signal indicates that a certain player has won the match
 */
public class PlayerWonSignal extends Exception{

    private final Player player;

    /**
     * The constructor
     * @param player the winner
     */
    public PlayerWonSignal(Player player){
        this.player = player;
    }

    /**
     * method to get the winner
     * @return the winner
     */
    public Player getPlayer(){
        return player;
    }
}
