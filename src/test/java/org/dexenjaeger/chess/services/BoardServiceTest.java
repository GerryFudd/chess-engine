package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.pieces.Piece;

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
}
