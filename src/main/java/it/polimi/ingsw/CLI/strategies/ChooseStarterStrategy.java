package it.polimi.ingsw.CLI.strategies;

public interface ChooseStarterStrategy {
    /**
     * This handler asks the queried player (also called challenger) the starting player. If the active player is not the user
     * this method will display who is choosing the starting player.
     * @param activePlayer is the player asked to do this action.
     * @param isRetry true if the action is requested another time, false otherwise.
     */
    void handleChooseStartPlayer(String activePlayer,boolean isRetry);
}
