package org.dexenjaeger.chess.utils;

import java.util.Iterator;

public class MergedIterator<T, U> implements Iterator<Pair<T, U>> {
    private final Iterator<T> tIterator;
    private final Iterator<U> uIterator;

    public MergedIterator(Iterator<T> tIterator, Iterator<U> uIterator) {
        this.tIterator = tIterator;
        this.uIterator = uIterator;
    }

    @Override
    public boolean hasNext() {
        return tIterator.hasNext() && uIterator.hasNext();
    }

    @Override
    public Pair<T, U> next() {
        return new Pair<>(tIterator.next(), uIterator.next());
    }
}
