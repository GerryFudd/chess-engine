package org.dexenjaeger.chess.services.moves;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Move;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.utils.DirectionIterable;
import org.dexenjaeger.chess.utils.Pair;

public class DirectionalMoveExtractor implements MoveExtractor {
    private final Side side;
    private final List<Pair<Integer, Integer>> directions;
    private final Square starting;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public DirectionalMoveExtractor(Side side, List<Pair<Integer, Integer>> directions,
        Square starting, EvaluateOccupyingSide evaluateOccupyingSide) {
        this.side = side;
        this.directions = directions;
        this.starting = starting;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }

    @Override
    public Set<Move> moveSet() {
        Set<Move> moves = new HashSet<>();
        for (Square square:new DirectionIterable(
            directions, starting, sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s == side).isEmpty()
        )) {
            moves.add(new Move(starting, square));
        }
        return moves;
    }
}
