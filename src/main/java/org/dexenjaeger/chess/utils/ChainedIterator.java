package org.dexenjaeger.chess.utils;

import java.util.Iterator;
import java.util.List;

public class ChainedIterator<T> implements Iterator<T> {
    private int current = 0;
    private final List<Iterator<T>> iterators;

    public ChainedIterator(List<Iterator<T>> iterators) {
        this.iterators = iterators;
    }

    @Override
    public boolean hasNext() {
        return iterators.subList(current, iterators.size())
            .stream()
            .anyMatch(Iterator::hasNext);
    }

    @Override
    public T next() {
        while (current < iterators.size() && !iterators.get(current).hasNext()) {
            current++;
        }
        if (current >= iterators.size()) {
            throw new RuntimeException("Chained iterator has no next element.");
        }
        return iterators.get(current).next();
    }
}
