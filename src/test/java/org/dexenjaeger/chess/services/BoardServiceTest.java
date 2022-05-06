package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.PgnFileUtil;
import org.junit.jupiter.api.Test;

class BoardServiceTest {
    private final BoardService boardService = new BoardService(new PieceService());
    public static void assertPiece(Board board, FileType x, RankType y, Piece expected) {
        Optional<Piece> p = board.getPiece(x, y);
        assertTrue(
            p.isPresent(),
            String.format("Position %c%d should have a piece.", x.getCharVal(), y.getAsNumber())
        );
        assertEquals(
            expected,
            p.get()
        );
    }

    public static void assertEmpty(Board board, FileType x, RankType y) {
        assertTrue(
            board.getPiece(x, y).isEmpty(),
            String.format("Position %c%d should not have a piece.", x.getCharVal(), y.getAsNumber())
        );
    }

    private Board nimzoIndianBoard() {
        return new PgnService(boardService)
            .boardFromPgn(PgnFileUtil.readOpening("NimzoIndianDefenseKasparov.pgn"));
    }

    @Test
    void getMoves_pinnedPiece() {
        Board board = nimzoIndianBoard();

        // The knight on C3 is pinned
        assertEquals(
            Set.of(),
            boardService.getMoves(board, FileType.C, RankType.THREE)
        );
    }

    @Test
    void getMovesForSide_inCheck() {
        Board board = boardService.applyMove(
            nimzoIndianBoard(),
            new SimpleMove(new Square(FileType.D, RankType.ONE), new Square(FileType.C, RankType.TWO), PieceType.QUEEN, Side.WHITE),
            new SimpleMove(new Square(FileType.B, RankType.FOUR), new Square(FileType.C, RankType.THREE), PieceType.BISHOP, Side.BLACK)
        );

        // White is in check and must address this
        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.THREE), PieceType.QUEEN, Side.WHITE),
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.D, RankType.TWO), PieceType.QUEEN, Side.WHITE),
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.C, RankType.THREE), PieceType.PAWN, Side.WHITE),
                new SimpleMove(new Square(FileType.E, RankType.ONE), new Square(FileType.D, RankType.ONE), PieceType.KING, Side.WHITE),
                new SimpleMove(new Square(FileType.C, RankType.ONE), new Square(FileType.D, RankType.TWO), PieceType.BISHOP, Side.WHITE),
                new SimpleMove(new Square(FileType.F, RankType.THREE), new Square(FileType.D, RankType.TWO), PieceType.KNIGHT, Side.WHITE)
            ),
            boardService.getMovesBySide(board, Side.WHITE)
        );
    }
}
