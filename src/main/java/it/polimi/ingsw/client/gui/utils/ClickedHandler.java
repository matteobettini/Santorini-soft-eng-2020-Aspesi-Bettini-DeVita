package it.polimi.ingsw.client.gui.utils;

/**
 * Handler for a clicked T
 * @param <T> Class linked to the click event
 */
public interface ClickedHandler<T> {
    void handle(T target);
}
