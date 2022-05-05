package org.dexenjaeger.chess.models.pieces;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;

@Value
public class Piece {
    Side side;
    PieceType type;

    public String toString() {
        if (type == PieceType.PAWN) {
            return String.format("%sp", side.getRepresentation());
        }
        return String.format("%s%s", side.getRepresentation(), type.getRepresentation());
    }
}
