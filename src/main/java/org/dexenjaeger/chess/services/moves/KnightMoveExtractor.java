package org.dexenjaeger.chess.services.moves;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.utils.Pair;

public class KnightMoveExtractor implements MoveExtractor {
    private final Side side;
    private final Pair<File, Rank> starting;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public KnightMoveExtractor(
        Side side,
        Pair<File, Rank> starting,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        this.side = side;
        this.starting = starting;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }


    @Override
    public Set<Pair<File, Rank>> moveSet() {

        return Stream.of(
            new Pair<>(-2, -1),
            new Pair<>(-2, 1),
            new Pair<>(-1, -2),
            new Pair<>(-1, 2),
            new Pair<>(2, -1),
            new Pair<>(2, 1),
            new Pair<>(1, -2),
            new Pair<>(1, 2)
        )
            .map(
                shifts -> starting
                    .getLeft()
                    .shift(shifts.getLeft())
                    .flatMap(f -> starting.getRight().shift(shifts.getRight()).map(r -> new Pair<>(f, r)))
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s == side).isEmpty())
            .collect(Collectors.toSet());
    }
}
