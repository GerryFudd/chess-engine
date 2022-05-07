package org.dexenjaeger.chess.services.moves;

import java.util.Set;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.NormalMove;

public interface MoveExtractor {
    Set<NormalMove> moveSet(Square starting);
    boolean canMove(Square from, Square to);
}
