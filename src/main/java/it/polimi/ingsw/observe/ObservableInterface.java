package it.polimi.ingsw.observe;

public interface ObservableInterface<T> {

    public void addObserver(Observer<T> observer);
}
