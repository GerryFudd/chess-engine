package org.dexenjaeger.chess.services.moves;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.NormalMove;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.ConversionUtil;
import org.dexenjaeger.chess.utils.DirectionIterable;
import org.dexenjaeger.chess.utils.DirectionIterationTester;
import org.dexenjaeger.chess.utils.Pair;

public class DirectionalMoveExtractor implements MoveExtractor {
    private final Side side;
    private final PieceType pieceType;
    private final List<Pair<Integer, Integer>> directions;
    private final DirectionIterationTester checkAvailability;

    public DirectionalMoveExtractor(Side side, PieceType pieceType, List<Pair<Integer, Integer>> directions,
        DirectionIterationTester checkAvailability) {
        this.side = side;
        this.pieceType = pieceType;
        this.directions = directions;
        this.checkAvailability = checkAvailability;
    }

    @Override
    public Set<NormalMove> moveSet(Square starting) {
        Set<NormalMove> moves = new HashSet<>();
        for (Square square:new DirectionIterable(
            directions, starting, checkAvailability
        )) {
            moves.add(new SimpleMove(starting, square, pieceType, side));
        }
        return moves;
    }

    @Override
    public boolean canMove(Square from, Square to) {
        int fileShift = to.getFile().ordinal() - from.getFile().ordinal();
        int rankShift = to.getRank().ordinal() - from.getRank().ordinal();
        return ConversionUtil.directionFromShifts(fileShift, rankShift)
            .filter(directions::contains)
            .map(dir -> {
                for (Square square:new DirectionIterable(
                    List.of(dir), from, checkAvailability
                )) {
                    if (square.equals(to)) {
                        return true;
                    }
                }
                return false;
            })
            .orElse(false);
    }
}
