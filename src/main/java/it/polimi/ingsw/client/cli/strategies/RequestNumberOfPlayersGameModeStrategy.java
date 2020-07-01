package it.polimi.ingsw.client.cli.strategies;

public interface RequestNumberOfPlayersGameModeStrategy {
    /**
     * This handler makes the user choose the number of players and the game-mode.
     * @param message is the message of request sent from the server.
     * @param isRetry True if last choice was invalid
     */
    void handleRequestNumberOfPlayerGameMode(String message, boolean isRetry);
}
