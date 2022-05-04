package org.dexenjaeger.chess.utils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.board.Square;

public class DirectionIterable implements Iterable<Square>{
    private final List<Pair<Integer, Integer>> directions;
    private final Square starting;
    private final Predicate<Square> isAvailable;

    public DirectionIterable(
        List<Pair<Integer, Integer>> directions,
        Square starting,
        Predicate<Square> isAvailable
    ) {
        this.directions = directions;
        this.starting = starting;
        this.isAvailable = isAvailable;
    }

    @Override
    public Iterator<Square> iterator() {
        return new ChainedIterator<>(
            directions.stream()
                .map(d -> new DirectionIterator(d, isAvailable, starting))
                .collect(Collectors.toList())
        );
    }
}
