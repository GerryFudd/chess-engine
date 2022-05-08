package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.Side.BLACK;
import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.dexenjaeger.chess.models.pieces.PieceType.BISHOP;
import static org.dexenjaeger.chess.models.pieces.PieceType.KING;
import static org.dexenjaeger.chess.models.pieces.PieceType.KNIGHT;
import static org.dexenjaeger.chess.models.pieces.PieceType.PAWN;
import static org.dexenjaeger.chess.models.pieces.PieceType.QUEEN;
import static org.dexenjaeger.chess.models.pieces.PieceType.ROOK;
import static org.dexenjaeger.chess.services.BoardServiceTest.assertEmpty;
import static org.dexenjaeger.chess.services.BoardServiceTest.assertPiece;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.EnPassantCapture;
import org.dexenjaeger.chess.models.moves.PromotionMove;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

public class BoardServiceApplyMoveTest {
    private final BoardService boardService = BoardServiceTest
        .serviceProvider
        .getInstance(BoardService.class);

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
                + "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR",
            e.getMessage()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "WHITE,SHORT",
        "WHITE,LONG",
        "BLACK,SHORT",
        "BLACK,LONG"
    })
    void applyMove_castleNotAvailableWhenPiecesBetween(String sideName, String typeName) {
        Side side = Side.valueOf(sideName);
        CastleType type = CastleType.valueOf(typeName);

        ServiceException e = assertThrows(ServiceException.class, () -> boardService.applyMove(
            BoardService.standardGameBoard(), new Castle(side, type)
        ));
        assertEquals(
            String.format(
                "The move Castle(side=%s, type=%s) is not available on this board.\n%s",
                sideName, typeName, BoardService.standardGameBoard()
            ),
            e.getMessage()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "WHITE,SHORT",
        "WHITE,LONG",
        "BLACK,SHORT",
        "BLACK,LONG"
    })
    void applyMove_noCastlingAfterKingMove(String sideName, String typeName) {
        Map<Square, Piece> initialBoardState = new HashMap<>();
        initialBoardState.put(new Square(FileType.D, RankType.ONE), new Piece(WHITE, KING));
        initialBoardState.put(new Square(FileType.A, RankType.ONE), new Piece(WHITE, ROOK));
        initialBoardState.put(new Square(FileType.H, RankType.ONE), new Piece(WHITE, ROOK));
        initialBoardState.put(new Square(FileType.D, RankType.EIGHT), new Piece(BLACK, KING));
        initialBoardState.put(new Square(FileType.A, RankType.EIGHT), new Piece(BLACK, ROOK));
        initialBoardState.put(new Square(FileType.H, RankType.EIGHT), new Piece(BLACK, ROOK));
        Board board = new Board(initialBoardState);

        Side side = Side.valueOf(sideName);
        CastleType type = CastleType.valueOf(typeName);

        ServiceException e = assertThrows(
            ServiceException.class, () -> boardService.applyMove(board, new Castle(side, type))
        );

        assertEquals(String.format(
            "The move Castle(side=%s, type=%s) is not available on this board.\n%s",
            sideName, typeName, board
        ), e.getMessage());
    }

    @ParameterizedTest
    @EnumSource(Side.class)
    void applyMove_noCastlingLongAfterAFileRookMove(Side side) {
        Map<Square, Piece> initialBoardState = new HashMap<>();
        initialBoardState.put(new Square(FileType.E, RankType.ONE), new Piece(WHITE, KING));
        initialBoardState.put(new Square(FileType.D, RankType.ONE), new Piece(WHITE, ROOK));
        initialBoardState.put(new Square(FileType.H, RankType.ONE), new Piece(WHITE, ROOK));
        initialBoardState.put(new Square(FileType.E, RankType.EIGHT), new Piece(BLACK, KING));
        initialBoardState.put(new Square(FileType.D, RankType.EIGHT), new Piece(BLACK, ROOK));
        initialBoardState.put(new Square(FileType.H, RankType.EIGHT), new Piece(BLACK, ROOK));
        Board board = new Board(initialBoardState);

        ServiceException e = assertThrows(
            ServiceException.class, () -> boardService.applyMove(board, new Castle(side, CastleType.LONG))
        );

        assertEquals(String.format(
            "The move Castle(side=%s, type=LONG) is not available on this board.\n%s",
            side.name(), board
        ), e.getMessage());
    }

    @ParameterizedTest
    @EnumSource(Side.class)
    void applyMove_noCastlingShortAfterHFileRookMove(Side side) {
        Map<Square, Piece> initialBoardState = new HashMap<>();
        initialBoardState.put(new Square(FileType.E, RankType.ONE), new Piece(WHITE, KING));
        initialBoardState.put(new Square(FileType.A, RankType.ONE), new Piece(WHITE, ROOK));
        initialBoardState.put(new Square(FileType.F, RankType.ONE), new Piece(WHITE, ROOK));
        initialBoardState.put(new Square(FileType.E, RankType.EIGHT), new Piece(BLACK, KING));
        initialBoardState.put(new Square(FileType.A, RankType.EIGHT), new Piece(BLACK, ROOK));
        initialBoardState.put(new Square(FileType.F, RankType.EIGHT), new Piece(BLACK, ROOK));
        Board board = new Board(initialBoardState);

        ServiceException e = assertThrows(
            ServiceException.class, () -> boardService.applyMove(board, new Castle(side, CastleType.SHORT))
        );

        assertEquals(String.format(
            "The move Castle(side=%s, type=SHORT) is not available on this board.\n%s",
            side.name(), board
        ), e.getMessage());
    }

    @Test
    void applyMove_doesntAllowCheckAfterMove() {
        Board nimzoIndianBoard = BoardServiceTest.nimzoIndianBoard();

        // The Knight on c3 is pinned and can't move
        ServiceException e = assertThrows(ServiceException.class, () -> boardService.applyMove(
            nimzoIndianBoard, new SimpleMove(new Square(FileType.C, RankType.THREE), new Square(FileType.D, RankType.FIVE), KNIGHT, WHITE)
        ));

        assertEquals(
            "The move Nc3d5 is not available on this board.\n"
                + "rnbq1rk1/pppp1ppp/4pn2/8/"
                + "1bPP4/2N2N2/PP2PPPP/R1BQKB1R",
            e.getMessage()
        );
    }

    @Test
    void applyMove_pawnPromotion() {
        Board result = boardService.applyMove(
            BoardServiceTest.simpleEndgameWithCFilePromotion(), new PromotionMove(WHITE, FileType.C, QUEEN)
        );
        assertPiece(result, FileType.C, RankType.EIGHT, new Piece(WHITE, QUEEN));
        assertEmpty(result, FileType.C, RankType.SEVEN);
    }

    @Test
    void applyMove_enPassantCapture() {
        Board result = boardService.applyMove(
            BoardServiceTest.simpleEndgameWithEnPassantConfiguration(),
            new EnPassantCapture(WHITE, FileType.C, FileType.B)
        );
        assertPiece(result, FileType.B, RankType.SIX, new Piece(WHITE, PAWN));
        assertEmpty(result, FileType.B, RankType.FIVE);
    }
}
