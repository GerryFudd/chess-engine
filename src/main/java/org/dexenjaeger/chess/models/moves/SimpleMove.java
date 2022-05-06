package org.dexenjaeger.chess.models.moves;

import lombok.Value;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

@Value
public class SimpleMove implements SinglePieceMove {
    Square from;
    Square to;
    Piece piece;

    public SimpleMove(Square from, Square to, Piece piece) {
        this.from = from;
        this.to = to;
        this.piece = piece;
    }

    public SimpleMove(Square from, Square to, PieceType type, Side side) {
        this(from, to, new Piece(side, type));
    }

    public PieceType getType() {
        return piece.getType();
    }

    public Side getSide() {
        return piece.getSide();
    }

    public String toString() {
        return String.format("%s%s%s", getType().getRepresentation(), from, to);
    }
}
