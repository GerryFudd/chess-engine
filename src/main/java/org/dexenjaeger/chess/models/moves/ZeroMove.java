package org.dexenjaeger.chess.models.moves;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;

@Value
public class ZeroMove implements Move {
    Side side;
    public String toString() {
        return String.format("Starting side = %s", side.other().name());
    }
}
