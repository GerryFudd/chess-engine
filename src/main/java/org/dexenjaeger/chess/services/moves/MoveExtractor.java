package org.dexenjaeger.chess.services.moves;

import java.util.Set;
import org.dexenjaeger.chess.models.board.Move;

public interface MoveExtractor {
    Set<Move> moveSet();
}
