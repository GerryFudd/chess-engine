package org.dexenjaeger.chess.utils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;

public class DirectionIterable implements Iterable<Pair<File, Rank>>{
    private final List<Pair<Integer, Integer>> directions;
    private final Pair<File, Rank> starting;
    private final Predicate<Pair<File, Rank>> isAvailable;

    public DirectionIterable(
        List<Pair<Integer, Integer>> directions,
        Pair<File, Rank> starting,
        Predicate<Pair<File, Rank>> isAvailable
    ) {
        this.directions = directions;
        this.starting = starting;
        this.isAvailable = isAvailable;
    }

    @Override
    public Iterator<Pair<File, Rank>> iterator() {
        return new ChainedIterator<>(
            directions.stream()
                .map(d -> new DirectionIterator(d, isAvailable, starting))
                .collect(Collectors.toList())
        );
    }
}
