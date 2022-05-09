package org.dexenjaeger.chess.utils;

import java.util.Iterator;

public class MergedIterable<T, U> implements Iterable<Pair<T, U>> {
    private final Iterable<T> tIterable;
    private final Iterable<U> uIterable;

    public MergedIterable(Iterable<T> tIterable, Iterable<U> uIterable) {
        this.tIterable = tIterable;
        this.uIterable = uIterable;
    }

    @Override
    public Iterator<Pair<T, U>> iterator() {
        return new MergedIterator<>(tIterable.iterator(), uIterable.iterator());
    }
}
