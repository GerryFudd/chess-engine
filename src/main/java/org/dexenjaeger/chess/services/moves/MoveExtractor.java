package org.dexenjaeger.chess.services.moves;

import java.util.Set;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SinglePieceMove;

public interface MoveExtractor {
    Set<SinglePieceMove> moveSet(Square starting);
    boolean canMove(Square from, Square to);
}
