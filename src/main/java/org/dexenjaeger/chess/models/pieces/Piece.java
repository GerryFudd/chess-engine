package org.dexenjaeger.chess.models.pieces;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;

@Value
public class Piece {
    Side side;
    PieceType type;
}
