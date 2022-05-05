package org.dexenjaeger.chess.services.moves;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

public class FixedMoveExtractor implements MoveExtractor {
    private final Side side;
    private final PieceType pieceType;
    private final List<Pair<Integer, Integer>> fixedMoves;
    private final Predicate<Square> checkAvailability;

    public FixedMoveExtractor(
        Side side,
        PieceType pieceType,
        List<Pair<Integer, Integer>> fixedMoves,
        Predicate<Square> checkAvailability
    ) {
        this.side = side;
        this.pieceType = pieceType;
        this.fixedMoves = fixedMoves;
        this.checkAvailability = checkAvailability;
    }

    @Override
    public Set<SimpleMove> moveSet(Square starting) {

        return fixedMoves.stream()
            .map(
                shifts -> starting
                    .getFile()
                    .shift(shifts.getLeft())
                    .flatMap(f -> starting.getRank().shift(shifts.getRight()).map(r -> new Square(f, r)))
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(checkAvailability)
            .map(sq -> new SimpleMove(starting, sq, pieceType, side))
            .collect(Collectors.toSet());
    }

    @Override
    public boolean canMove(Square from, Square to) {
        if (!fixedMoves.contains(new Pair<>(
            to.getFile().ordinal() - from.getFile().ordinal(),
            to.getRank().ordinal() - from.getRank().ordinal()
        ))) {
            return false;
        }
        return checkAvailability.test(to);
    }
}
