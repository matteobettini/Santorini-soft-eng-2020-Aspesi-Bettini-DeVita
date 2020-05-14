package it.polimi.ingsw.common.utils.observe;

public interface ClientObserver <T> {
    void update(T message, boolean isRetry);
}
