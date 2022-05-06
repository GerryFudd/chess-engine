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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.junit.jupiter.api.Test;

class BoardServiceTest {
    private final BoardService boardService = new BoardService(new PieceService());

    void assertPiece(Board board, FileType x, RankType y, Piece expected) {
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

    void assertEmpty(Board board, FileType x, RankType y) {
        assertTrue(
            board.getPiece(x, y).isEmpty(),
            String.format("Position %c%d should not have a piece.", x.getCharVal(), y.getAsNumber())
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

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.A, RankType.TWO), new Square(FileType.A, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.A, RankType.TWO), new Square(FileType.A, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.A, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.A, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), KNIGHT, WHITE)
            ),
            boardService.getMoves(board, FileType.B, RankType.ONE)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.B, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.B, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.B, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.C, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.D, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.E, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.F, RankType.TWO), new Square(FileType.F, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.F, RankType.TWO), new Square(FileType.F, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.F, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.TWO), new Square(FileType.G, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.TWO), new Square(FileType.G, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.G, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.H, RankType.THREE), KNIGHT, WHITE)
            ),
            boardService.getMoves(board, FileType.G, RankType.ONE)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.H, RankType.TWO), new Square(FileType.H, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.H, RankType.TWO), new Square(FileType.H, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMoves(board, FileType.H, RankType.TWO)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.A, RankType.SEVEN), new Square(FileType.A, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.A, RankType.SEVEN), new Square(FileType.A, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.A, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.A, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.C, RankType.SIX), KNIGHT, BLACK)
            ),
            boardService.getMoves(board, FileType.B, RankType.EIGHT)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.SEVEN), new Square(FileType.B, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.B, RankType.SEVEN), new Square(FileType.B, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.B, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.C, RankType.SEVEN), new Square(FileType.C, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.C, RankType.SEVEN), new Square(FileType.C, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.C, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.D, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.E, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.F, RankType.SEVEN), new Square(FileType.F, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.F, RankType.SEVEN), new Square(FileType.F, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.F, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.SEVEN), new Square(FileType.G, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.SEVEN), new Square(FileType.G, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.G, RankType.SEVEN)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.H, RankType.SIX), KNIGHT, BLACK)
            ),
            boardService.getMoves(board, FileType.G, RankType.EIGHT)
        );

        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.H, RankType.SEVEN), new Square(FileType.H, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.H, RankType.SEVEN), new Square(FileType.H, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMoves(board, FileType.H, RankType.SEVEN)
        );
    }

    @Test
    void getAvailableMoves_whereEmpty() {
        Board board = BoardService.standardGameBoard();

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
                boardService.getMoves(board, sq.getFile(), sq.getRank())
            ));
    }

    @Test
    void applyMoveTest_doesntMutateBoard() {
        Board startingBoard = BoardService.standardGameBoard();
        Board board = boardService.applyMove(
            startingBoard,
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE)
        );

        assertNotEquals(
            startingBoard, board, "Applying a move should not mutate the starting board."
        );
        assertEquals(
            BoardService.standardGameBoard(), startingBoard
        );
    }

    @Test
    void applyMoveTest_applySimplePawnMove() {
        Board board = boardService.applyMove(
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
    void applyMoveTest_applySimpleKnightMove() {
        Board board = boardService.applyMove(
            BoardService.standardGameBoard(),
            new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), KNIGHT, WHITE)
        );

        assertEmpty(board, FileType.G, RankType.ONE);
        assertPiece(
            board, FileType.F, RankType.THREE,
            new Piece(WHITE, KNIGHT)
        );
    }

    @Test
    void applySuccessiveMoveTest_applySuccessiveMoves() {
        Board board = boardService.applyMove(
            BoardService.standardGameBoard(),
            new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), KNIGHT, WHITE),
            new SimpleMove(new Square(FileType.B, RankType.SEVEN), new Square(FileType.B, RankType.SIX), PAWN, BLACK),
            new SimpleMove(new Square(FileType.G, RankType.TWO), new Square(FileType.G, RankType.THREE), PAWN, WHITE),
            new SimpleMove(new Square(FileType.C, RankType.EIGHT), new Square(FileType.B, RankType.SEVEN), BISHOP, BLACK)
        );

        assertPiece(
            board, FileType.B, RankType.SEVEN,
            new Piece(BLACK, BISHOP)
        );
        assertPiece(
            board, FileType.B, RankType.SIX,
            new Piece(BLACK, PAWN)
        );
        assertEmpty(board, FileType.C, RankType.EIGHT);

        assertPiece(
            board, FileType.F, RankType.THREE,
            new Piece(WHITE, KNIGHT)
        );
        assertPiece(
            board, FileType.G, RankType.THREE,
            new Piece(WHITE, PAWN)
        );
        assertEmpty(board, FileType.G, RankType.TWO);
    }

    @Test
    void applyMoveTest_unavailableMove() {
        ServiceException e = assertThrows(ServiceException.class, () -> boardService.applyMove(
            BoardService.standardGameBoard(),
            new SimpleMove(new Square(FileType.D, RankType.ONE), new Square(FileType.D, RankType.EIGHT), QUEEN, WHITE)
        ));

        assertEquals(
            "The move Qd1d8 is not available on this board.\n"
                + "bRbNbBbQbKbBbNbR\n"
                + "bpbpbpbpbpbpbpbp\n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "wpwpwpwpwpwpwpwp\n"
                + "wRwNwBwQwKwBwNwR",
            e.getMessage()
        );
    }

    @Test
    void getMovesBySideAndTargetTest_fromOpening() {
        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), KNIGHT, WHITE)
            ),
            boardService.getMovesBySideAndTarget(
                BoardService.standardGameBoard(), WHITE, new Square(FileType.C, RankType.THREE)
            )
        );
    }

    @Test
    void getMovesBySideTest_fromOpening() {
        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.A, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.H, RankType.THREE), KNIGHT, WHITE),
                new SimpleMove(new Square(FileType.A, RankType.TWO), new Square(FileType.A, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.A, RankType.TWO), new Square(FileType.A, RankType.FOUR), PAWN, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.B, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.B, RankType.FOUR), PAWN, WHITE),
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PAWN, WHITE),
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE),
                new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.FOUR), PAWN, WHITE),
                new SimpleMove(new Square(FileType.F, RankType.TWO), new Square(FileType.F, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.F, RankType.TWO), new Square(FileType.F, RankType.FOUR), PAWN, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.TWO), new Square(FileType.G, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.G, RankType.TWO), new Square(FileType.G, RankType.FOUR), PAWN, WHITE),
                new SimpleMove(new Square(FileType.H, RankType.TWO), new Square(FileType.H, RankType.THREE), PAWN, WHITE),
                new SimpleMove(new Square(FileType.H, RankType.TWO), new Square(FileType.H, RankType.FOUR), PAWN, WHITE)
            ),
            boardService.getMovesBySide(
                BoardService.standardGameBoard(), WHITE
            )
        );
        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.A, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.C, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.H, RankType.SIX), KNIGHT, BLACK),
                new SimpleMove(new Square(FileType.A, RankType.SEVEN), new Square(FileType.A, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.A, RankType.SEVEN), new Square(FileType.A, RankType.FIVE), PAWN, BLACK),
                new SimpleMove(new Square(FileType.B, RankType.SEVEN), new Square(FileType.B, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.B, RankType.SEVEN), new Square(FileType.B, RankType.FIVE), PAWN, BLACK),
                new SimpleMove(new Square(FileType.C, RankType.SEVEN), new Square(FileType.C, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.C, RankType.SEVEN), new Square(FileType.C, RankType.FIVE), PAWN, BLACK),
                new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PAWN, BLACK),
                new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.FIVE), PAWN, BLACK),
                new SimpleMove(new Square(FileType.F, RankType.SEVEN), new Square(FileType.F, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.F, RankType.SEVEN), new Square(FileType.F, RankType.FIVE), PAWN, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.SEVEN), new Square(FileType.G, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.G, RankType.SEVEN), new Square(FileType.G, RankType.FIVE), PAWN, BLACK),
                new SimpleMove(new Square(FileType.H, RankType.SEVEN), new Square(FileType.H, RankType.SIX), PAWN, BLACK),
                new SimpleMove(new Square(FileType.H, RankType.SEVEN), new Square(FileType.H, RankType.FIVE), PAWN, BLACK)
            ),
            boardService.getMovesBySide(
                BoardService.standardGameBoard(), BLACK
            )
        );
    }

    @Test
    void applyMove_castleNotAvailable() {
        ServiceException whiteShort = assertThrows(ServiceException.class, () -> boardService.applyMove(
            BoardService.standardGameBoard(), new Castle(WHITE, CastleType.SHORT)
        ));
        assertEquals(
            "The move Castle(side=WHITE, type=SHORT) is not available on this board.\n"
                + "bRbNbBbQbKbBbNbR\n"
                + "bpbpbpbpbpbpbpbp\n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "wpwpwpwpwpwpwpwp\n"
                + "wRwNwBwQwKwBwNwR", whiteShort.getMessage()
        );
        ServiceException whiteLong = assertThrows(ServiceException.class, () -> boardService.applyMove(
            BoardService.standardGameBoard(), new Castle(WHITE, CastleType.LONG)
        ));
        assertEquals(
            "The move Castle(side=WHITE, type=LONG) is not available on this board.\n"
                + "bRbNbBbQbKbBbNbR\n"
                + "bpbpbpbpbpbpbpbp\n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "wpwpwpwpwpwpwpwp\n"
                + "wRwNwBwQwKwBwNwR", whiteLong.getMessage()
        );
        ServiceException blackShort = assertThrows(ServiceException.class, () -> boardService.applyMove(
            BoardService.standardGameBoard(), new Castle(BLACK, CastleType.SHORT)
        ));
        assertEquals(
            "The move Castle(side=BLACK, type=SHORT) is not available on this board.\n"
                + "bRbNbBbQbKbBbNbR\n"
                + "bpbpbpbpbpbpbpbp\n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "wpwpwpwpwpwpwpwp\n"
                + "wRwNwBwQwKwBwNwR", blackShort.getMessage()
        );
        ServiceException blackLong = assertThrows(ServiceException.class, () -> boardService.applyMove(
            BoardService.standardGameBoard(), new Castle(BLACK, CastleType.LONG)
        ));
        assertEquals(
            "The move Castle(side=BLACK, type=LONG) is not available on this board.\n"
                + "bRbNbBbQbKbBbNbR\n"
                + "bpbpbpbpbpbpbpbp\n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "                \n"
                + "wpwpwpwpwpwpwpwp\n"
                + "wRwNwBwQwKwBwNwR", blackLong.getMessage()
        );
    }
}
