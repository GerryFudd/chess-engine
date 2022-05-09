package org.dexenjaeger.chess.models.moves;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;

@Value
public class Castle implements Move {
    Side side;
    CastleType type;

    public String toString() {
        String result;
        if (type == CastleType.LONG) {
            result = "q";
        } else {
            result = "k";
        }
        return side == Side.WHITE ? result.toUpperCase() : result;
    }
}
