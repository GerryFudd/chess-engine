package org.dexenjaeger.chess.services.moves;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.DirectionIterable;
import org.dexenjaeger.chess.utils.Pair;

public class DirectionalMoveExtractor implements MoveExtractor {
    private final Side side;
    private final PieceType pieceType;
    private final List<Pair<Integer, Integer>> directions;
    private final Predicate<Square> checkAvailability;

    public DirectionalMoveExtractor(Side side, PieceType pieceType, List<Pair<Integer, Integer>> directions,
        Predicate<Square> checkAvailability) {
        this.side = side;
        this.pieceType = pieceType;
        this.directions = directions;
        this.checkAvailability = checkAvailability;
    }

    @Override
    public Set<SimpleMove> moveSet(Square starting) {
        Set<SimpleMove> moves = new HashSet<>();
        for (Square square:new DirectionIterable(
            directions, starting, checkAvailability
        )) {
            moves.add(new SimpleMove(starting, square, pieceType, side));
        }
        return moves;
    }

    @Override
    public boolean canMove(Square from, Square to) {
        return false;
    }
}
