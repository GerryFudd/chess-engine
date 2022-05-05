package org.dexenjaeger.chess.models.board;

import static org.dexenjaeger.chess.models.Side.WHITE;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

public class Board {
    private final Map<Square, Piece> pieces;

    public Board(Map<Square, Piece> pieces) {
        this.pieces = pieces;
    }

    public Optional<Piece> getPiece(FileType file, RankType rank) {
        return getPiece(new Square(file, rank));
    }
    
    public Optional<Piece> getPiece(Square square) {
        return Optional.ofNullable(pieces.get(square));
    }

    public Board castle(Castle castle) {
        Side side = castle.getSide();
        CastleType castleType = castle.getType();
        FileType rookFileFrom = castleType.getRookFileFrom();
        RankType rank = side == WHITE ? RankType.ONE : RankType.EIGHT;
        Piece rook = Optional.ofNullable(pieces.remove(new Square(rookFileFrom, rank))).orElseThrow();
        Piece king = Optional.ofNullable(pieces.remove(new Square(FileType.E, rank))).orElseThrow();

        FileType rookFileTo = castleType.getRookFileTo();
        FileType kingFileTo = castleType.getKingFileTo();
        pieces.put(new Square(rookFileTo, rank), rook);
        pieces.put(new Square(kingFileTo, rank), king);
        return this;
    }

    public Board movePiece(SimpleMove move) {
        Piece piece = Optional.ofNullable(pieces.remove(move.getFrom())).orElseThrow();
        pieces.put(move.getTo(), piece);
        return this;
    }

    public Set<Square> getBySideAndType(Side side, PieceType type) {
        return pieces.entrySet()
            .stream()
            .filter(ent -> ent.getValue().getType() == type && ent.getValue().getSide() == side)
            .map(Entry::getKey)
            .collect(Collectors.toSet());
    }
}
