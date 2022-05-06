package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.moves.Turn;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.junit.jupiter.api.Test;

class PgnServiceTest {
    private final PgnService pgnService = new PgnService(new BoardService(new PieceService()));

    @Test
    void toPgnMove_simpleOpeningPawnMove() {
        Board board = BoardService.standardGameBoard();
        assertEquals(
            "d4",
            pgnService.toPgnMove(
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PieceType.PAWN, Side.WHITE),
                board
            )
        );
    }

    @Test
    void toPgnMove_simpleOpeningKnightMove() {
        assertEquals(
            "Nf3",
            pgnService.toPgnMove(
                new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
                BoardService.standardGameBoard()
            )
        );
    }

    @Test
    void toPgnMove_simpleOpeningPawnMoveBlack() {
        Board board = BoardService.standardGameBoard();
        assertEquals(
            "d5",
            pgnService.toPgnMove(
                new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PieceType.PAWN, Side.BLACK),
                board
            )
        );
    }

    @Test
    void toPgnMove_simpleOpeningKnightMoveBlack() {
        assertEquals(
            "Nf6",
            pgnService.toPgnMove(
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), PieceType.KNIGHT, Side.BLACK),
                BoardService.standardGameBoard()
            )
        );
    }

    @Test
    void fromPgnMove_simpleOpeningPawnMove() {
        assertEquals(
            new SimpleMove(new Square(FileType.F, RankType.TWO), new Square(FileType.F, RankType.THREE), PieceType.PAWN, Side.WHITE),
            pgnService.fromPgnMove("f3", Side.WHITE, BoardService.standardGameBoard())
        );
    }

    @Test
    void fromPgnMove_simpleOpeningKnightMove() {
        assertEquals(
            new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
            pgnService.fromPgnMove("Nf3", Side.WHITE, BoardService.standardGameBoard())
        );
    }

    @Test
    void fromPgnMove_simpleOpeningPawnMoveBlack() {
        assertEquals(
            new SimpleMove(new Square(FileType.F, RankType.SEVEN), new Square(FileType.F, RankType.SIX), PieceType.PAWN, Side.BLACK),
            pgnService.fromPgnMove("f6", Side.BLACK, BoardService.standardGameBoard())
        );
    }

    @Test
    void fromPgnMove_simpleOpeningKnightMoveBlack() {
        assertEquals(
            new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), PieceType.KNIGHT, Side.BLACK),
            pgnService.fromPgnMove("Nf6", Side.BLACK, BoardService.standardGameBoard())
        );
    }

    @Test
    void fromPgnTurn_firstTurnKingsIndian() {
        assertEquals(
            new Turn(
                1,
                new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PieceType.PAWN, Side.WHITE),
                new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), PieceType.KNIGHT, Side.BLACK)
            ),
            pgnService.fromPgnTurn("1. d4 Nf6")
        );
    }

    @Test
    void fromPgnTurnList_appliesQGDClassical() {
        assertEquals(
            List.of(
                new Turn(
                    1,
                    new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PieceType.PAWN, Side.WHITE),
                    new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PieceType.PAWN, Side.BLACK)
                ),
                new Turn(
                    2,
                    new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PieceType.PAWN, Side.WHITE),
                    new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.SIX), PieceType.PAWN, Side.BLACK)
                ),
                new Turn(
                    3,
                    new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
                    new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), PieceType.KNIGHT, Side.BLACK)
                ),
                new Turn(
                    4,
                    new SimpleMove(new Square(FileType.C, RankType.ONE), new Square(FileType.G, RankType.FIVE), PieceType.BISHOP, Side.WHITE),
                    new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.D, RankType.SEVEN), PieceType.KNIGHT, Side.BLACK)
                )
            ),
            pgnService.fromPgnTurnList("1. d4 d5 2. c4 e6 3. Nc3 Nf6 4. Bg5 Nbd7")
        );
    }
}