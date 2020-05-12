package it.polimi.ingsw.utils;

public class Pair<K,T> {

    private final K first;
    private final T second;

    public Pair(K first, T second) {
        assert first != null && second != null;
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

}
