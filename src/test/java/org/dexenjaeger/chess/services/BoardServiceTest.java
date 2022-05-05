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
import java.util.stream.Stream;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.junit.jupiter.api.Test;

class BoardServiceTest {
    private final BoardService boardService = new BoardService(new PieceService());

    void assertPiece(Board board, FileType x, RankType y, Piece expected) {
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

    void assertEmpty(Board board, FileType x, RankType y) {
        assertTrue(
            board.getPiece(x, y).isEmpty(),
            String.format("Position %c%d should not have a piece.", x.getVal(), y.getAsNumber())
        );
    }

    @Test
    void standardGameBoard_hasCorrectPieces() {
        Board standardBoard = BoardService.standardGameBoard();

        assertPiece(standardBoard, FileType.A, RankType.ONE, new Piece(WHITE, ROOK));
        assertPiece(standardBoard, FileType.B, RankType.ONE, new Piece(WHITE, KNIGHT));
        assertPiece(standardBoard, FileType.C, RankType.ONE, new Piece(WHITE, BISHOP));
        assertPiece(standardBoard, FileType.D, RankType.ONE, new Piece(WHITE, QUEEN));
        assertPiece(standardBoard, FileType.E, RankType.ONE, new Piece(WHITE, KING));
        assertPiece(standardBoard, FileType.F, RankType.ONE, new Piece(WHITE, BISHOP));
        assertPiece(standardBoard, FileType.G, RankType.ONE, new Piece(WHITE, KNIGHT));
        assertPiece(standardBoard, FileType.H, RankType.ONE, new Piece(WHITE, ROOK));

        assertPiece(standardBoard, FileType.A, RankType.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, FileType.B, RankType.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, FileType.C, RankType.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, FileType.D, RankType.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, FileType.E, RankType.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, FileType.F, RankType.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, FileType.G, RankType.TWO, new Piece(WHITE, PAWN));
        assertPiece(standardBoard, FileType.H, RankType.TWO, new Piece(WHITE, PAWN));

        assertEmpty(standardBoard, FileType.A, RankType.THREE);
        assertEmpty(standardBoard, FileType.B, RankType.THREE);
        assertEmpty(standardBoard, FileType.C, RankType.THREE);
        assertEmpty(standardBoard, FileType.D, RankType.THREE);
        assertEmpty(standardBoard, FileType.E, RankType.THREE);
        assertEmpty(standardBoard, FileType.F, RankType.THREE);
        assertEmpty(standardBoard, FileType.G, RankType.THREE);

        assertEmpty(standardBoard, FileType.A, RankType.FOUR);
        assertEmpty(standardBoard, FileType.B, RankType.FOUR);
        assertEmpty(standardBoard, FileType.C, RankType.FOUR);
        assertEmpty(standardBoard, FileType.D, RankType.FOUR);
        assertEmpty(standardBoard, FileType.E, RankType.FOUR);
        assertEmpty(standardBoard, FileType.F, RankType.FOUR);
        assertEmpty(standardBoard, FileType.G, RankType.FOUR);

        assertEmpty(standardBoard, FileType.A, RankType.FIVE);
        assertEmpty(standardBoard, FileType.B, RankType.FIVE);
        assertEmpty(standardBoard, FileType.C, RankType.FIVE);
        assertEmpty(standardBoard, FileType.D, RankType.FIVE);
        assertEmpty(standardBoard, FileType.E, RankType.FIVE);
        assertEmpty(standardBoard, FileType.F, RankType.FIVE);
        assertEmpty(standardBoard, FileType.G, RankType.FIVE);

        assertEmpty(standardBoard, FileType.A, RankType.SIX);
        assertEmpty(standardBoard, FileType.B, RankType.SIX);
        assertEmpty(standardBoard, FileType.C, RankType.SIX);
        assertEmpty(standardBoard, FileType.D, RankType.SIX);
        assertEmpty(standardBoard, FileType.E, RankType.SIX);
        assertEmpty(standardBoard, FileType.F, RankType.SIX);
        assertEmpty(standardBoard, FileType.G, RankType.SIX);

        assertPiece(standardBoard, FileType.A, RankType.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, FileType.B, RankType.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, FileType.C, RankType.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, FileType.D, RankType.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, FileType.E, RankType.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, FileType.F, RankType.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, FileType.G, RankType.SEVEN, new Piece(BLACK, PAWN));
        assertPiece(standardBoard, FileType.H, RankType.SEVEN, new Piece(BLACK, PAWN));

        assertPiece(standardBoard, FileType.A, RankType.EIGHT, new Piece(BLACK, ROOK));
        assertPiece(standardBoard, FileType.B, RankType.EIGHT, new Piece(BLACK, KNIGHT));
        assertPiece(standardBoard, FileType.C, RankType.EIGHT, new Piece(BLACK, BISHOP));
        assertPiece(standardBoard, FileType.D, RankType.EIGHT, new Piece(BLACK, QUEEN));
        assertPiece(standardBoard, FileType.E, RankType.EIGHT, new Piece(BLACK, KING));
        assertPiece(standardBoard, FileType.F, RankType.EIGHT, new Piece(BLACK, BISHOP));
        assertPiece(standardBoard, FileType.G, RankType.EIGHT, new Piece(BLACK, KNIGHT));
        assertPiece(standardBoard, FileType.H, RankType.EIGHT, new Piece(BLACK, ROOK));
    }

    @Test
    void getAvailableMoves_whereAvailable() {
        Board board = BoardService.standardGameBoard();
        BoardService service = new BoardService(new PieceService());

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.A, RankType.TWO), new Square(FileType.A, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.A, RankType.TWO), new Square(FileType.A, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.A, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.A, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), KNIGHT, WHITE)
            ),
            service.getMoves(board, FileType.B, RankType.ONE)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.B, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.B, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.B, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.C, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.D, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.E, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.F, RankType.TWO), new Square(FileType.F, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.F, RankType.TWO), new Square(FileType.F, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.F, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.TWO), new Square(FileType.G, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.TWO), new Square(FileType.G, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.G, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.H, RankType.THREE), KNIGHT, WHITE)
            ),
            service.getMoves(board, FileType.G, RankType.ONE)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.H, RankType.TWO), new Square(FileType.H, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.H, RankType.TWO), new Square(FileType.H, RankType.FOUR), PAWN, WHITE)
            ),
            service.getMoves(board, FileType.H, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.A, RankType.SEVEN), new Square(FileType.A, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.A, RankType.SEVEN), new Square(FileType.A, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.A, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.A, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.C, RankType.SIX), KNIGHT, BLACK)
            ),
            service.getMoves(board, FileType.B, RankType.EIGHT)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.SEVEN), new Square(FileType.B, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.B, RankType.SEVEN), new Square(FileType.B, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.B, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.C, RankType.SEVEN), new Square(FileType.C, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.C, RankType.SEVEN), new Square(FileType.C, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.C, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.D, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.E, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.F, RankType.SEVEN), new Square(FileType.F, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.F, RankType.SEVEN), new Square(FileType.F, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.F, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.SEVEN), new Square(FileType.G, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.SEVEN), new Square(FileType.G, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.G, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.H, RankType.SIX), KNIGHT, BLACK)
            ),
            service.getMoves(board, FileType.G, RankType.EIGHT)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.H, RankType.SEVEN), new Square(FileType.H, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.H, RankType.SEVEN), new Square(FileType.H, RankType.FIVE), PAWN, BLACK)
            ),
            service.getMoves(board, FileType.H, RankType.SEVEN)
        );
    }

    @Test
    void getAvailableMoves_whereEmpty() {
        Board board = BoardService.standardGameBoard();
        BoardService service = new BoardService(new PieceService());

        Stream.of(
            new Square(FileType.A, RankType.ONE),
            new Square(FileType.C, RankType.ONE),
            new Square(FileType.D, RankType.ONE),
            new Square(FileType.E, RankType.ONE),
            new Square(FileType.F, RankType.ONE),
            new Square(FileType.H, RankType.ONE),
            new Square(FileType.A, RankType.THREE),
            new Square(FileType.B, RankType.THREE),
            new Square(FileType.C, RankType.THREE),
            new Square(FileType.D, RankType.THREE),
            new Square(FileType.E, RankType.THREE),
            new Square(FileType.F, RankType.THREE),
            new Square(FileType.G, RankType.THREE),
            new Square(FileType.H, RankType.THREE),
            new Square(FileType.A, RankType.FOUR),
            new Square(FileType.B, RankType.FOUR),
            new Square(FileType.C, RankType.FOUR),
            new Square(FileType.D, RankType.FOUR),
            new Square(FileType.E, RankType.FOUR),
            new Square(FileType.F, RankType.FOUR),
            new Square(FileType.G, RankType.FOUR),
            new Square(FileType.H, RankType.FOUR),
            new Square(FileType.A, RankType.FIVE),
            new Square(FileType.B, RankType.FIVE),
            new Square(FileType.C, RankType.FIVE),
            new Square(FileType.D, RankType.FIVE),
            new Square(FileType.E, RankType.FIVE),
            new Square(FileType.F, RankType.FIVE),
            new Square(FileType.G, RankType.FIVE),
            new Square(FileType.H, RankType.FIVE),
            new Square(FileType.A, RankType.SIX),
            new Square(FileType.B, RankType.SIX),
            new Square(FileType.C, RankType.SIX),
            new Square(FileType.D, RankType.SIX),
            new Square(FileType.E, RankType.SIX),
            new Square(FileType.F, RankType.SIX),
            new Square(FileType.G, RankType.SIX),
            new Square(FileType.H, RankType.SIX),
            new Square(FileType.A, RankType.EIGHT),
            new Square(FileType.C, RankType.EIGHT),
            new Square(FileType.D, RankType.EIGHT),
            new Square(FileType.E, RankType.EIGHT),
            new Square(FileType.F, RankType.EIGHT),
            new Square(FileType.H, RankType.EIGHT)
        )
            .forEach(sq -> assertEquals(
                Set.of(),
                service.getMoves(board, sq.getFile(), sq.getRank())
            ));
    }

    @Test
    void applySimpleMoveTest_applySimplePawnMove() {
        Board board = boardService.applySimpleMove(
            BoardService.standardGameBoard(),
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE)
        );

        assertEmpty(board, FileType.D, RankType.TWO);
        assertPiece(
            board, FileType.D, RankType.FOUR,
            new Piece(WHITE, PAWN)
        );
    }

    @Test
    void applySimpleMoveTest_applySimpleKnightMove() {
        Board board = boardService.applySimpleMove(
            BoardService.standardGameBoard(),
            new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), KNIGHT, WHITE)
        );

        assertEmpty(board, FileType.G, RankType.ONE);
        assertPiece(
            board, FileType.F, RankType.THREE,
            new Piece(WHITE, KNIGHT)
        );
    }
}