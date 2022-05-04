package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.Side.BLACK;
import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.dexenjaeger.chess.models.pieces.PieceType.BISHOP;
import static org.dexenjaeger.chess.models.pieces.PieceType.KING;
import static org.dexenjaeger.chess.models.pieces.PieceType.KNIGHT;
import static org.dexenjaeger.chess.models.pieces.PieceType.PAWN;
import static org.dexenjaeger.chess.models.pieces.PieceType.QUEEN;
import static org.dexenjaeger.chess.models.pieces.PieceType.ROOK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.junit.jupiter.api.Test;

class BoardServiceTest {
    void assertPiece(Board board, File x, Rank y, Piece expected) {
        Optional<Piece> p = board.getPiece(x, y);
        assertTrue(
            p.isPresent(),
            String.format("Position %c%d should have a piece.", x.getVal(), y.getAsNumber())
        );
        assertEquals(
            expected,
            p.get()
        );
    }

    void assertEmpty(Board board, File x, Rank y) {
        assertTrue(
            board.getPiece(x, y).isEmpty(),
            String.format("Position %c%d should not have a piece.", x.getVal(), y.getAsNumber())
        );
    }

    @Test
    void standardGameBoard_hasCorrectPieces() {
        Board standardBoard = BoardService.standardGameBoard();

        assertPiece(standardBoard, File.A, Rank.ONE, new Piece(WHITE, ROOK));
        assertPiece(standardBoard, File.B, Rank.ONE, new Piece(WHITE, KNIGHT));
        assertPiece(standardBoard, File.C, Rank.ONE, new Piece(WHITE, BISHOP));
        assertPiece(standardBoard, File.D, Rank.ONE, new Piece(WHITE, QUEEN));
        assertPiece(standardBoard, File.E, Rank.ONE, new Piece(WHITE, KING));
        assertPiece(standardBoard, File.F, Rank.ONE, new Piece(WHITE, BISHOP));
        assertPiece(standardBoard, File.G, Rank.ONE, new Piece(WHITE, KNIGHT));
        assertPiece(standardBoard, File.H, Rank.ONE, new Piece(WHITE, ROOK));

        assertPiece(standardBoard, File.A, Rank.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, File.B, Rank.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, File.C, Rank.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, File.D, Rank.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, File.E, Rank.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, File.F, Rank.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, File.G, Rank.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, File.H, Rank.TWO, new Piece(WHITE, PAWN));

        assertEmpty(standardBoard, File.A, Rank.THREE);
        assertEmpty(standardBoard, File.B, Rank.THREE);
        assertEmpty(standardBoard, File.C, Rank.THREE);
        assertEmpty(standardBoard, File.D, Rank.THREE);
        assertEmpty(standardBoard, File.E, Rank.THREE);
        assertEmpty(standardBoard, File.F, Rank.THREE);
        assertEmpty(standardBoard, File.G, Rank.THREE);

        assertEmpty(standardBoard, File.A, Rank.FOUR);
        assertEmpty(standardBoard, File.B, Rank.FOUR);
        assertEmpty(standardBoard, File.C, Rank.FOUR);
        assertEmpty(standardBoard, File.D, Rank.FOUR);
        assertEmpty(standardBoard, File.E, Rank.FOUR);
        assertEmpty(standardBoard, File.F, Rank.FOUR);
        assertEmpty(standardBoard, File.G, Rank.FOUR);

        assertEmpty(standardBoard, File.A, Rank.FIVE);
        assertEmpty(standardBoard, File.B, Rank.FIVE);
        assertEmpty(standardBoard, File.C, Rank.FIVE);
        assertEmpty(standardBoard, File.D, Rank.FIVE);
        assertEmpty(standardBoard, File.E, Rank.FIVE);
        assertEmpty(standardBoard, File.F, Rank.FIVE);
        assertEmpty(standardBoard, File.G, Rank.FIVE);

        assertEmpty(standardBoard, File.A, Rank.SIX);
        assertEmpty(standardBoard, File.B, Rank.SIX);
        assertEmpty(standardBoard, File.C, Rank.SIX);
        assertEmpty(standardBoard, File.D, Rank.SIX);
        assertEmpty(standardBoard, File.E, Rank.SIX);
        assertEmpty(standardBoard, File.F, Rank.SIX);
        assertEmpty(standardBoard, File.G, Rank.SIX);

        assertPiece(standardBoard, File.A, Rank.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, File.B, Rank.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, File.C, Rank.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, File.D, Rank.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, File.E, Rank.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, File.F, Rank.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, File.G, Rank.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, File.H, Rank.SEVEN, new Piece(BLACK, PAWN));

        assertPiece(standardBoard, File.A, Rank.EIGHT, new Piece(BLACK, ROOK));
        assertPiece(standardBoard, File.B, Rank.EIGHT, new Piece(BLACK, KNIGHT));
        assertPiece(standardBoard, File.C, Rank.EIGHT, new Piece(BLACK, BISHOP));
        assertPiece(standardBoard, File.D, Rank.EIGHT, new Piece(BLACK, QUEEN));
        assertPiece(standardBoard, File.E, Rank.EIGHT, new Piece(BLACK, KING));
        assertPiece(standardBoard, File.F, Rank.EIGHT, new Piece(BLACK, BISHOP));
        assertPiece(standardBoard, File.G, Rank.EIGHT, new Piece(BLACK, KNIGHT));
        assertPiece(standardBoard, File.H, Rank.EIGHT, new Piece(BLACK, ROOK));
    }
}