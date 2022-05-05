package org.dexenjaeger.chess.services.moves;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

public class FixedMoveExtractor implements MoveExtractor {
    private final Side side;
    private final PieceType pieceType;
    private final Square starting;
    private final List<Pair<Integer, Integer>> fixedMoves;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public FixedMoveExtractor(
        Side side,
        PieceType pieceType, Square starting,
        List<Pair<Integer, Integer>> fixedMoves,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        this.side = side;
        this.pieceType = pieceType;
        this.starting = starting;
        this.fixedMoves = fixedMoves;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }


    @Override
    public Set<SimpleMove> moveSet() {

        return fixedMoves.stream()
            .map(
                shifts -> starting
                    .getFile()
                    .shift(shifts.getLeft())
                    .flatMap(f -> starting.getRank().shift(shifts.getRight()).map(r -> new SimpleMove(starting, new Square(f, r), pieceType, side)))
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(mv -> evaluateOccupyingSide.getOccupyingSide(mv.getTo()).filter(s -> s == side).isEmpty())
            .collect(Collectors.toSet());
    }
}
