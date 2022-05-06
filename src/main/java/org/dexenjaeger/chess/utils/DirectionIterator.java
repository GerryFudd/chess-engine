package org.dexenjaeger.chess.utils;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.dexenjaeger.chess.models.board.Square;

public class DirectionIterator implements Iterator<Square> {
    private final Pair<Integer, Integer> direction;
    private final DirectionIterationTester tester;
    private Square current;
    private final AtomicBoolean hitLast = new AtomicBoolean(false);

    public DirectionIterator(
        Pair<Integer, Integer> direction,
        DirectionIterationTester tester,
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
                .filter(this::isAvailable);
    }

    @Override
    public boolean hasNext() {
        return !hitLast.get() && getNextIfPresent().isPresent();
    }

    private void processCurrent() {
        if (tester.testIteration(current) == DirectionIterableTestResult.LAST) {
            hitLast.set(true);
        }
    }

    private boolean isAvailable(Square square) {
        return tester.testIteration(square) != DirectionIterableTestResult.UNAVAILABLE;
    }

    @Override
    public Square next() {
        current = getNextIfPresent().orElseThrow();
        processCurrent();
        return current;
    }
}
