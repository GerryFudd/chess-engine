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
import java.util.Set;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.utils.Pair;
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

    @Test
    void getAvailableMoves() {
        Board board = BoardService.standardGameBoard();
        BoardService service = new BoardService(new PieceService());

        assertEquals(
            Set.of(
                new Pair<>(File.A, Rank.THREE),
                new Pair<>(File.A, Rank.FOUR)
            ),
            service.getMoves(board, File.A, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.A, Rank.THREE),
                new Pair<>(File.C, Rank.THREE)
            ),
            service.getMoves(board, File.B, Rank.ONE)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.B, Rank.THREE),
                new Pair<>(File.B, Rank.FOUR)
            ),
            service.getMoves(board, File.B, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.C, Rank.THREE),
                new Pair<>(File.C, Rank.FOUR)
            ),
            service.getMoves(board, File.C, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.D, Rank.THREE),
                new Pair<>(File.D, Rank.FOUR)
            ),
            service.getMoves(board, File.D, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.E, Rank.THREE),
                new Pair<>(File.E, Rank.FOUR)
            ),
            service.getMoves(board, File.E, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.F, Rank.THREE),
                new Pair<>(File.F, Rank.FOUR)
            ),
            service.getMoves(board, File.F, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.G, Rank.THREE),
                new Pair<>(File.G, Rank.FOUR)
            ),
            service.getMoves(board, File.G, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.F, Rank.THREE),
                new Pair<>(File.H, Rank.THREE)
            ),
            service.getMoves(board, File.G, Rank.ONE)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.H, Rank.THREE),
                new Pair<>(File.H, Rank.FOUR)
            ),
            service.getMoves(board, File.H, Rank.TWO)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.A, Rank.SIX),
                new Pair<>(File.A, Rank.FIVE)
            ),
            service.getMoves(board, File.A, Rank.SEVEN)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.A, Rank.SIX),
                new Pair<>(File.C, Rank.SIX)
            ),
            service.getMoves(board, File.B, Rank.EIGHT)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.B, Rank.SIX),
                new Pair<>(File.B, Rank.FIVE)
            ),
            service.getMoves(board, File.B, Rank.SEVEN)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.C, Rank.SIX),
                new Pair<>(File.C, Rank.FIVE)
            ),
            service.getMoves(board, File.C, Rank.SEVEN)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.D, Rank.SIX),
                new Pair<>(File.D, Rank.FIVE)
            ),
            service.getMoves(board, File.D, Rank.SEVEN)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.E, Rank.SIX),
                new Pair<>(File.E, Rank.FIVE)
            ),
            service.getMoves(board, File.E, Rank.SEVEN)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.F, Rank.SIX),
                new Pair<>(File.F, Rank.FIVE)
            ),
            service.getMoves(board, File.F, Rank.SEVEN)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.G, Rank.SIX),
                new Pair<>(File.G, Rank.FIVE)
            ),
            service.getMoves(board, File.G, Rank.SEVEN)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.F, Rank.SIX),
                new Pair<>(File.H, Rank.SIX)
            ),
            service.getMoves(board, File.G, Rank.EIGHT)
        );

        assertEquals(
            Set.of(
                new Pair<>(File.H, Rank.SIX),
                new Pair<>(File.H, Rank.FIVE)
            ),
            service.getMoves(board, File.H, Rank.SEVEN)
        );
    }
}