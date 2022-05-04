package org.dexenjaeger.chess.services.moves;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Move;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.utils.Pair;

public class FixedMoveExtractor implements MoveExtractor {
    private final Side side;
    private final Square starting;
    private final List<Pair<Integer, Integer>> fixedMoves;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public FixedMoveExtractor(
        Side side,
        Square starting,
        List<Pair<Integer, Integer>> fixedMoves,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        this.side = side;
        this.starting = starting;
        this.fixedMoves = fixedMoves;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }


    @Override
    public Set<Move> moveSet() {

        return fixedMoves.stream()
            .map(
                shifts -> starting
                    .getFile()
                    .shift(shifts.getLeft())
                    .flatMap(f -> starting.getRank().shift(shifts.getRight()).map(r -> new Move(starting, new Square(f, r))))
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(mv -> evaluateOccupyingSide.getOccupyingSide(mv.getTo()).filter(s -> s == side).isEmpty())
            .collect(Collectors.toSet());
    }
}
