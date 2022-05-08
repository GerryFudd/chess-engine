package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.io.PgnFileReader;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.moves.Turn;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;
import org.junit.jupiter.api.Test;

class PgnServiceTest {
    // The PGN service needs to read text files that follow this spec:
    // http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm#c2
    private final ServiceProvider serviceProvider = new ServiceProvider();
    private final PgnService pgnService = serviceProvider.getInstance(PgnService.class);
    private final BoardService boardService = serviceProvider.getInstance(BoardService.class);
    private final GameService gameService = serviceProvider.getInstance(GameService.class);

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
    void fromPgnMove_edgeCaseWithThreeQueens() {
        Map<Square, Piece> pieceLocations = new HashMap<>();
        pieceLocations.put(new Square(FileType.A, RankType.EIGHT), new Piece(Side.WHITE, PieceType.QUEEN));
        pieceLocations.put(new Square(FileType.A, RankType.FIVE), new Piece(Side.WHITE, PieceType.QUEEN));
        pieceLocations.put(new Square(FileType.D, RankType.FIVE), new Piece(Side.WHITE, PieceType.QUEEN));
        pieceLocations.put(new Square(FileType.B, RankType.SEVEN), new Piece(Side.WHITE, PieceType.KING));
        pieceLocations.put(new Square(FileType.E, RankType.SEVEN), new Piece(Side.BLACK, PieceType.KING));
        pieceLocations.put(new Square(FileType.F, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN));
        pieceLocations.put(new Square(FileType.E, RankType.SIX), new Piece(Side.BLACK, PieceType.PAWN));
        pieceLocations.put(new Square(FileType.D, RankType.ONE), new Piece(Side.BLACK, PieceType.ROOK));

        Board boardWithThreeQueens = new Board(pieceLocations);
        assertEquals(
            new SimpleMove(
                new Square(FileType.A, RankType.FIVE), new Square(FileType.D, RankType.EIGHT), PieceType.QUEEN, Side.WHITE
            ),
            pgnService.fromPgnMove("Qa5d8", Side.WHITE, boardWithThreeQueens)
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
            pgnService.fromPgnTurn("1. d4 Nf6", BoardService.standardGameBoard())
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
            pgnService.fromPgnTurnList(PgnFileReader.readOpening("QGDClassical.pgn"))
        );
    }

    private final List<Turn> nimzoIndianTurns = List.of(
        new Turn(
            1,
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PieceType.PAWN, Side.WHITE),
            new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), PieceType.KNIGHT, Side.BLACK)
        ),
        new Turn(
            2,
            new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PieceType.PAWN, Side.WHITE),
            new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.SIX), PieceType.PAWN, Side.BLACK)
        ),
        new Turn(
            3,
            new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
            new SimpleMove(new Square(FileType.F, RankType.EIGHT), new Square(FileType.B, RankType.FOUR), PieceType.BISHOP, Side.BLACK)
        ),
        new Turn(
            4,
            new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
            new Castle(Side.BLACK, CastleType.SHORT)
        )
    );

    @Test
    void gameFromPgn() {
        Game expectedGame = gameService.startGame();
        for (Turn expectedTurn:nimzoIndianTurns) {
            expectedGame.addBoard(boardService.applyMove(
                expectedGame.currentBoard(), expectedTurn.getWhiteMove()
            ));
            expectedTurn.getBlackMove()
                .ifPresent(m -> expectedGame.addBoard(boardService.applyMove(
                    expectedGame.currentBoard(), m
                )));
            expectedGame.addTurn(expectedTurn);
        }
        assertEquals(
            expectedGame,
            pgnService.gameFromPgn(PgnFileReader.readOpening("NimzoIndianDefenseKasparov.pgn"))
        );
    }

    @Test
    void gameFromPgnTest_tags() {
        // A PGN contains the following tags
        // Event (the name of the tournament or match event)
        // Site (the location of the event)
        // Date (the starting date of the game)
        // Round (the playing round ordinal of the game)
        // White (the player of the white pieces)
        // Black (the player of the black pieces)
        // Result (the result of the game)
        String withTags = "[Event \"This is the event description\"]\n"
            + "[Foo \"This is the foo tag\"]\n"
            + "[Site \"This is the site\"]\n"
            + "[Black \"Calvin MacBrittishname\"]\n"
            + "[Date \"2022.05.07\"]\n"
            + "[White \"Smythe Smootnovitch\"]\n"
            + "[Result \"1-0\"]\n"
            + "[Round \"2.1.2\"]\n"
            + "\n"
            + "1. d4 d5";
        assertEquals(
            List.of(
                // These tags need to be exported in this order
                new Pair<>("Event", "This is the event description"),
                new Pair<>("Site", "This is the site"),
                new Pair<>("Date", "2022.05.07"),
                new Pair<>("Round", "2.1.2"),
                new Pair<>("White", "Smythe Smootnovitch"),
                new Pair<>("Black", "Calvin MacBrittishname"),
                new Pair<>("Result", "1-0"),
                new Pair<>("Foo", "This is the foo tag")
            ),
            pgnService.gameFromPgn(withTags).getTags()
        );
    }
}