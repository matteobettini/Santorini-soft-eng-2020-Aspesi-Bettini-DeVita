package it.polimi.ingsw.client.gui.strategies;

public interface SetWorkersStrategy extends InteractionStrategy {
    void handleSetWorkers(String activePlayer, boolean isRetry);
}
