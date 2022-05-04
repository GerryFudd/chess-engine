package org.dexenjaeger.chess.services;

import java.util.HashMap;
import java.util.Map;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

public class BoardService {
    private static Map<Pair<File, Rank>, Piece> defaultBoardState() {
        Map<Pair<File, Rank>, Piece> pieceMap = new HashMap<>();
        for (File file:File.values()) {
            pieceMap.put(
                new Pair<>(file, Rank.TWO),
                new Piece(Side.WHITE, PieceType.PAWN)
            );
            pieceMap.put(
                new Pair<>(file, Rank.SEVEN),
                new Piece(Side.BLACK, PieceType.PAWN)
            );
        }

        pieceMap.put(
            new Pair<>(File.A, Rank.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Pair<>(File.H, Rank.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Pair<>(File.A, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );
        pieceMap.put(
            new Pair<>(File.H, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );

        pieceMap.put(
            new Pair<>(File.B, Rank.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Pair<>(File.G, Rank.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Pair<>(File.B, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Pair<>(File.G, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );

        pieceMap.put(
            new Pair<>(File.C, Rank.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Pair<>(File.F, Rank.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Pair<>(File.C, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );
        pieceMap.put(
            new Pair<>(File.F, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );

        pieceMap.put(
            new Pair<>(File.D, Rank.ONE),
            new Piece(Side.WHITE, PieceType.QUEEN)
        );
        pieceMap.put(
            new Pair<>(File.E, Rank.ONE),
            new Piece(Side.WHITE, PieceType.KING)
        );
        pieceMap.put(
            new Pair<>(File.D, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.QUEEN)
        );
        pieceMap.put(
            new Pair<>(File.E, Rank.EIGHT),
            new Piece(Side.BLACK, PieceType.KING)
        );

        return pieceMap;
    }

    public static Board standardGameBoard() {
        return new Board(defaultBoardState());
    }
}
