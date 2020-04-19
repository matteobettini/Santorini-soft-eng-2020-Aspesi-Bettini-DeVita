package it.polimi.ingsw.observe;

public interface ObservableInterface<T> {

    void addObserver(Observer<T> observer);
}
