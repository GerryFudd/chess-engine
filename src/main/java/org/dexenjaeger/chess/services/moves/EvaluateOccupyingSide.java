package org.dexenjaeger.chess.services.moves;

import java.util.Optional;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.utils.Pair;

@FunctionalInterface
public interface EvaluateOccupyingSide {
    Optional<Side> getOccupyingSide(Pair<File, Rank> square);
}
