package it.polimi.ingsw.CLI.strategies;

public interface RequestNumberOfPlayersGameModeStrategy {
    /**
     * This handler makes the user choose the number of players and the game-mode.
     * @param message is the message of request sent from the server.
     */
    void handleRequestNumberOfPlayerGameMode(String message);
}
