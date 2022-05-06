package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.utils.PgnFileUtil;
import org.junit.jupiter.api.Test;

class BoardServiceTest {
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

    @Test
    void getMoves_pinnedPiece() {
        BoardService boardService = new BoardService(new PieceService());
        Board board = new PgnService(boardService).boardFromPgn(PgnFileUtil.readOpening("NimzoIndianDefenseKasparov.pgn"));

        // The knight on C3 is pinned
        assertEquals(
            Set.of(),
            boardService.getMoves(board, FileType.C, RankType.THREE)
        );
    }
}
