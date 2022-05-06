package org.dexenjaeger.chess.models.moves;

import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

public interface SinglePieceMove extends Move {
    Piece getPiece();
    PieceType getType();
    Square getFrom();
    Square getTo();
}
