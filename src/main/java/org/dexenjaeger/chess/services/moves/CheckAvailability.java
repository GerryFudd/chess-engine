package org.dexenjaeger.chess.services.moves;

import java.util.function.Predicate;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.utils.DirectionIterableTestResult;
import org.dexenjaeger.chess.utils.DirectionIterationTester;

public class CheckAvailability implements Predicate<Square>, DirectionIterationTester {
    private final EvaluateOccupyingSide evaluateOccupyingSide;
    private final Side side;

    public CheckAvailability(EvaluateOccupyingSide evaluateOccupyingSide, Side side) {
        this.evaluateOccupyingSide = evaluateOccupyingSide;
        this.side = side;
    }

    @Override
    public boolean test(Square square) {
        return evaluateOccupyingSide.getOccupyingSide(square).filter(s -> s == side).isEmpty();
    }

    @Override
    public DirectionIterableTestResult testIteration(Square square) {
        return evaluateOccupyingSide.getOccupyingSide(square)
            .map(s -> s == side ? DirectionIterableTestResult.UNAVAILABLE : DirectionIterableTestResult.LAST)
            .orElse(DirectionIterableTestResult.CONTINUE);
    }
}
