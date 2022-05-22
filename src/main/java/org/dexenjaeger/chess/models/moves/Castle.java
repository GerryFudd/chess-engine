package org.dexenjaeger.chess.models.moves;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;

@Value
public class Castle implements Move {
    Side side;
    CastleType type;

    public String toFen() {
        String result;
        if (type == CastleType.LONG) {
            result = "Q";
        } else {
            result = "K";
        }
        return side == Side.WHITE ? result : result.toLowerCase();
    }

    public String toString() {
        String result;
        if (type == CastleType.LONG) {
            result = "O-O-O";
        } else {
            result = "O-O";
        }
        return side == Side.WHITE ? result : result.toLowerCase();
    }
}
