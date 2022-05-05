package org.dexenjaeger.chess.models.moves;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;

@Value
public class Castle implements Move {
    Side side;
    CastleType type;
}
