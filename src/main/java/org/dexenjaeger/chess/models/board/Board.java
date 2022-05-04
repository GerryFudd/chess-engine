package org.dexenjaeger.chess.models.board;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

public class Board {
    private final Map<Square, Piece> pieces;

    public Board(Map<Square, Piece> pieces) {
        this.pieces = pieces;
    }

    public Optional<Piece> getPiece(File file, Rank rank) {
        return getPiece(new Square(file, rank));
    }
    
    public Optional<Piece> getPiece(Square square) {
        return Optional.ofNullable(pieces.get(square));
    }
    
    public Board movePiece(Move move) {
        Piece piece = Optional.ofNullable(pieces.remove(move.getFrom())).orElseThrow();
        pieces.put(move.getTo(), piece);
        return this;
    }
}
