package it.polimi.ingsw.observe;

public interface Observer<T> {

    public void update(T message);

}
