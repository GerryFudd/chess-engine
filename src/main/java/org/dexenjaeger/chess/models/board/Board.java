package org.dexenjaeger.chess.models.board;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

public class Board {
    private final Map<Pair<File, Rank>, Piece> pieces;

    public Board(Map<Pair<File, Rank>, Piece> pieces) {
        this.pieces = pieces;
    }

    public Optional<Piece> getPiece(File file, Rank rank) {
        return getPiece(new Pair<>(file, rank));
    }
    
    public Optional<Piece> getPiece(Pair<File, Rank> square) {
        return Optional.ofNullable(pieces.get(square));
    }
    
    public Board movePiece(Pair<File, Rank> start, Pair<File, Rank> end) {
        Piece piece = Optional.ofNullable(pieces.remove(start)).orElseThrow();
        pieces.put(end, piece);
        return this;
    }
}
