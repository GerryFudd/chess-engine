package org.dexenjaeger.chess.services.moves;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.utils.DirectionIterable;
import org.dexenjaeger.chess.utils.Pair;

public class DirectionalMoveExtractor implements MoveExtractor {
    private final Side side;
    private final List<Pair<Integer, Integer>> directions;
    private final Pair<File, Rank> starting;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public DirectionalMoveExtractor(Side side, List<Pair<Integer, Integer>> directions,
        Pair<File, Rank> starting, EvaluateOccupyingSide evaluateOccupyingSide) {
        this.side = side;
        this.directions = directions;
        this.starting = starting;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }

    @Override
    public Set<Pair<File, Rank>> moveSet() {
        Set<Pair<File, Rank>> moves = new HashSet<>();
        for (Pair<File, Rank> square:new DirectionIterable(
            directions, starting, sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s == side).isEmpty()
        )) {
            moves.add(square);
        }
        return moves;
    }
}
