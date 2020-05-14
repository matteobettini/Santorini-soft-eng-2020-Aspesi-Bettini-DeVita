package it.polimi.ingsw.common.utils;

import java.io.Serializable;
import java.util.Objects;

public class Pair<K,T> implements Serializable {
    private static final long serialVersionUID = -637459517647683573L;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
