package org.dexenjaeger.chess.services.moves;

import java.util.Set;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;

public interface MoveExtractor {
    Set<SimpleMove> moveSet(Square starting);
    boolean canMove(Square from, Square to);
}
