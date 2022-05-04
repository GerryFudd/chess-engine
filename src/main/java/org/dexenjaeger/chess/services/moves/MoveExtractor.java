package org.dexenjaeger.chess.services.moves;

import java.util.Set;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.utils.Pair;

public interface MoveExtractor {
    Set<Pair<File, Rank>> moveSet();
}
