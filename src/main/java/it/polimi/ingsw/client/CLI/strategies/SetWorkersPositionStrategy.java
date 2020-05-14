package it.polimi.ingsw.client.CLI.strategies;

public interface SetWorkersPositionStrategy {
    /**
     * This handler make the player choose his initial workers'position on the board.
     * @param activePlayer is the player asked to set his workers's position.
     * @param isRetry is true if positions are requested another time because they are already occupied, false otherwise.
     */
    void handleSetWorkersPosition(String activePlayer,boolean isRetry);
}
