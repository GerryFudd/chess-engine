package org.dexenjaeger.chess.models.board;

import static org.dexenjaeger.chess.models.Side.WHITE;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

@EqualsAndHashCode
public class Board {
    private final Map<Square, Piece> pieces;

    public Board(Map<Square, Piece> pieces) {
        this.pieces = pieces;
    }

    public String toString() {
        LinkedList<String> result = new LinkedList<>();
        for (RankType rank:RankType.values()) {
            StringBuilder rankRep = new StringBuilder();
            for (FileType file:FileType.values()) {
                rankRep.append(getPiece(file, rank).map(Piece::toString).orElse("  "));
            }
            result.addFirst(rankRep.toString());
        }

        return String.join("\n", result);
    }

    public Optional<Piece> getPiece(FileType file, RankType rank) {
        return getPiece(new Square(file, rank));
    }
    
    public Optional<Piece> getPiece(Square square) {
        return Optional.ofNullable(pieces.get(square));
    }

    public Optional<Side> getOccupyingSide(Square square) {
        return getPiece(square).map(Piece::getSide);
    }

    public Board castle(Castle castle) {
        Map<Square, Piece> newBoardState = new HashMap<>(pieces);
        Side side = castle.getSide();
        CastleType castleType = castle.getType();
        FileType rookFileFrom = castleType.getRookFileFrom();
        RankType rank = side == WHITE ? RankType.ONE : RankType.EIGHT;
        Piece rook = Optional.ofNullable(newBoardState.remove(new Square(rookFileFrom, rank))).orElseThrow();
        Piece king = Optional.ofNullable(newBoardState.remove(new Square(FileType.E, rank))).orElseThrow();

        FileType rookFileTo = castleType.getRookFileTo();
        FileType kingFileTo = castleType.getKingFileTo();
        newBoardState.put(new Square(rookFileTo, rank), rook);
        newBoardState.put(new Square(kingFileTo, rank), king);
        return new Board(newBoardState);
    }

    public Board movePiece(SimpleMove move) {
        Map<Square, Piece> newBoardState = new HashMap<>(pieces);
        Piece piece = Optional.ofNullable(newBoardState.remove(move.getFrom())).orElseThrow();
        newBoardState.put(move.getTo(), piece);
        return new Board(newBoardState);
    }

    public Set<Square> getBySideAndType(Side side, PieceType type) {
        return pieces.entrySet()
            .stream()
            .filter(ent -> ent.getValue().getType() == type && ent.getValue().getSide() == side)
            .map(Entry::getKey)
            .collect(Collectors.toSet());
    }

    public Set<Square> getBySide(Side side) {
        return pieces.entrySet()
            .stream()
            .filter(ent -> ent.getValue().getSide() == side)
            .map(Entry::getKey)
            .collect(Collectors.toSet());
    }
}
