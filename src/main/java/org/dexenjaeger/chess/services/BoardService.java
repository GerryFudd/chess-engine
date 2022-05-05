package org.dexenjaeger.chess.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

public class BoardService {

    private static Map<Square, Piece> defaultBoardState() {
        Map<Square, Piece> pieceMap = new HashMap<>();
        for (FileType file: FileType.values()) {
            pieceMap.put(
                new Square(file, RankType.TWO),
                new Piece(Side.WHITE, PieceType.PAWN)
            );
            pieceMap.put(
                new Square(file, RankType.SEVEN),
                new Piece(Side.BLACK, PieceType.PAWN)
            );
        }

        pieceMap.put(
            new Square(FileType.A, RankType.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(FileType.H, RankType.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(FileType.A, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(FileType.H, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );

        pieceMap.put(
            new Square(FileType.B, RankType.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(FileType.G, RankType.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(FileType.B, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(FileType.G, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );

        pieceMap.put(
            new Square(FileType.C, RankType.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(FileType.F, RankType.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(FileType.C, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(FileType.F, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );

        pieceMap.put(
            new Square(FileType.D, RankType.ONE),
            new Piece(Side.WHITE, PieceType.QUEEN)
        );
        pieceMap.put(
            new Square(FileType.E, RankType.ONE),
            new Piece(Side.WHITE, PieceType.KING)
        );
        pieceMap.put(
            new Square(FileType.D, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.QUEEN)
        );
        pieceMap.put(
            new Square(FileType.E, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.KING)
        );

        return pieceMap;
    }

    public static Board standardGameBoard() {
        return new Board(defaultBoardState());
    }

    private final PieceService pieceService;

    public BoardService(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    public Set<SimpleMove> getMoves(Board board, FileType f, RankType r) {
        return board.getPiece(f, r)
            .map(p -> pieceService.getMoves(p, new Square(f, r), sq -> board.getPiece(sq).map(Piece::getSide)))
            .orElse(Set.of());
    }

    public Optional<Square> getOtherPieceLocation(SimpleMove simpleMove, Board board) {
        return board.getBySideAndType(simpleMove.getSide(), simpleMove.getType())
            .stream()
            .filter(sq -> getMoves(
                board, sq.getFile(), sq.getRank()
            )
                .stream()
                .anyMatch(m -> m.getTo().equals(simpleMove.getTo()) && !m.getFrom().equals(simpleMove.getFrom()))
            )
            .findAny();
    }
}
