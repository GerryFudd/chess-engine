package org.dexenjaeger.chess.models.moves;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pieces.PieceType;

@Value
public class SimpleMove implements Move {
    Square from;
    Square to;
    PieceType type;
    Side side;

    public String toString() {
        return String.format("%s%s%s", type.getRepresentation(), from, to);
    }
}
