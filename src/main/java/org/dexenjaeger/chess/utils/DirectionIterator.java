package org.dexenjaeger.chess.utils;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;
import org.dexenjaeger.chess.models.board.Square;

public class DirectionIterator implements Iterator<Square> {
    private final Pair<Integer, Integer> direction;
    private final Predicate<Square> tester;
    private Square current;

    public DirectionIterator(
        Pair<Integer, Integer> direction,
        Predicate<Square> tester,
        Square initial
    ) {
        this.direction = direction;
        this.tester = tester;
        this.current = initial;
    }

    private Optional<Square> getNextIfPresent() {
        return current.getFile()
            .shift(direction.getLeft())
            .flatMap(f -> current.getRank()
                .shift(direction.getRight())
                .map(r -> new Square(f, r)))
            .filter(tester);
    }

    @Override
    public boolean hasNext() {
        return getNextIfPresent().isPresent();
    }

    @Override
    public Square next() {
        current = getNextIfPresent().orElseThrow();
        return current;
    }
}
