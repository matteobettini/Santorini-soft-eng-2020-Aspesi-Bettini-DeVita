package it.polimi.ingsw;

public interface ClientObserver <T> {
    void update(T message, boolean isRetry);
}
