package org.dexenjaeger.chess.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Move;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

public class BoardService {

    private static Map<Square, Piece> defaultBoardState() {
        Map<Square, Piece> pieceMap = new HashMap<>();
        for (File file:File.values()) {
            pieceMap.put(
                new Square(file, Rank.TWO),
                new Piece(Side.WHITE, PieceType.PAWN)
            );
            pieceMap.put(
                new Square(file, Rank.SEVEN),
                new Piece(Side.BLACK, PieceType.PAWN)
            );
        }

        pieceMap.put(
            new Square(File.A, Rank.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(File.H, Rank.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(File.A, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(File.H, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );

        pieceMap.put(
            new Square(File.B, Rank.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(File.G, Rank.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(File.B, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(File.G, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );

        pieceMap.put(
            new Square(File.C, Rank.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(File.F, Rank.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(File.C, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(File.F, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );

        pieceMap.put(
            new Square(File.D, Rank.ONE),
            new Piece(Side.WHITE, PieceType.QUEEN)
        );
        pieceMap.put(
            new Square(File.E, Rank.ONE),
            new Piece(Side.WHITE, PieceType.KING)
        );
        pieceMap.put(
            new Square(File.D, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.QUEEN)
        );
        pieceMap.put(
            new Square(File.E, Rank.EIGHT),
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

    public Set<Move> getMoves(Board board, File f, Rank r) {
        return board.getPiece(f, r)
            .map(p -> pieceService.getMoves(p, new Square(f, r), sq -> board.getPiece(sq).map(Piece::getSide)))
            .orElse(Set.of());
    }
}
