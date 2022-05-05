package org.dexenjaeger.chess.services.moves;

import java.util.Optional;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;

@FunctionalInterface
public interface EvaluateOccupyingSide {
    Optional<Side> getOccupyingSide(Square square);
}
