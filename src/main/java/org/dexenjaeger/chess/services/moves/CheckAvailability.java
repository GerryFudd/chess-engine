package org.dexenjaeger.chess.services.moves;

import java.util.function.Predicate;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;

public class CheckAvailability implements Predicate<Square> {
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
}
