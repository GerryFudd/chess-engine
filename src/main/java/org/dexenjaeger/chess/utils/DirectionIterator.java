package org.dexenjaeger.chess.utils;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;

public class DirectionIterator implements Iterator<Pair<File, Rank>> {
    private final Pair<Integer, Integer> direction;
    private final Predicate<Pair<File, Rank>> tester;
    private Pair<File, Rank> current;

    public DirectionIterator(
        Pair<Integer, Integer> direction,
        Predicate<Pair<File, Rank>> tester,
        Pair<File, Rank> initial
    ) {
        this.direction = direction;
        this.tester = tester;
        this.current = initial;
    }

    private Optional<Pair<File, Rank>> getNextIfPresent() {
        return current.getLeft()
            .shift(direction.getLeft())
            .flatMap(f -> current.getRight()
                .shift(direction.getRight())
                .map(r -> new Pair<>(f, r)))
            .filter(tester);
    }

    @Override
    public boolean hasNext() {
        return getNextIfPresent().isPresent();
    }

    @Override
    public Pair<File, Rank> next() {
        current = getNextIfPresent().orElseThrow();
        return current;
    }
}
