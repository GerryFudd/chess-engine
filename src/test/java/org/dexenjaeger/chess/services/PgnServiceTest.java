package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.io.PgnFileReader;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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

    private Board boardWithCastlingAvailable() {
        Map<Square, Piece> pieceMap = new HashMap<>();
        pieceMap.put(new Square(FileType.A, RankType.EIGHT), new Piece(Side.BLACK, PieceType.ROOK));
        pieceMap.put(new Square(FileType.A, RankType.ONE), new Piece(Side.WHITE, PieceType.ROOK));
        pieceMap.put(new Square(FileType.H, RankType.EIGHT), new Piece(Side.BLACK, PieceType.ROOK));
        pieceMap.put(new Square(FileType.H, RankType.ONE), new Piece(Side.WHITE, PieceType.ROOK));
        pieceMap.put(new Square(FileType.E, RankType.EIGHT), new Piece(Side.BLACK, PieceType.KING));
        pieceMap.put(new Square(FileType.E, RankType.ONE), new Piece(Side.WHITE, PieceType.KING));
        pieceMap.put(new Square(FileType.A, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN));
        pieceMap.put(new Square(FileType.B, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN));
        pieceMap.put(new Square(FileType.C, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN));
        pieceMap.put(new Square(FileType.F, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN));
        pieceMap.put(new Square(FileType.G, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN));
        pieceMap.put(new Square(FileType.H, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN));
        pieceMap.put(new Square(FileType.A, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN));
        pieceMap.put(new Square(FileType.B, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN));
        pieceMap.put(new Square(FileType.C, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN));
        pieceMap.put(new Square(FileType.F, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN));
        pieceMap.put(new Square(FileType.G, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN));
        pieceMap.put(new Square(FileType.H, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN));
        return new Board(pieceMap);
    }

    @ParameterizedTest
    @EnumSource(Side.class)
    void fromPgnMove_parsesShortCastle(Side side) {
        assertEquals(
            new Castle(side, CastleType.SHORT),
            pgnService.fromPgnMove("O-O", side, boardWithCastlingAvailable())
        );
    }

    @ParameterizedTest
    @EnumSource(Side.class)
    void fromPgnMove_parsesLongCastle(Side side) {
        assertEquals(
            new Castle(side, CastleType.LONG),
            pgnService.fromPgnMove("O-O-O", side, boardWithCastlingAvailable())
        );
    }

    // The following test covers an example from the PGN spec documentation:
    //   | Note that the above disambiguation is needed only to distinguish among moves of the
    //   | same piece type to the same square; it is not used to distinguish among attacks of
    //   | the same piece type to the same square. An example of this would be a position with
    //   | two white knights, one on square c3 and one on square g1 and a vacant square e2 with
    //   | White to move. Both knights attack square e2, and if both could legally move there,
    //   | then a file disambiguation is needed; the (nonchecking) knight moves would be "Nce2"
    //   | and "Nge2". However, if the white king were at square e1 and a black bishop were at
    //   | square b4 with a vacant square d2 (thus an absolute pin of the white knight at square
    //   | c3), then only one white knight (the one at square g1) could move to square e2: "Ne2".
    @Test
    void fromPgnMove_specialCase() {
        Board nimzoBoard = pgnService.boardFromPgn("1. d4 d5 2. c4 e6 3. Nc3 Bb4 4. e3 Nf6");
        assertEquals(
            new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.E, RankType.TWO), PieceType.KNIGHT, Side.WHITE),
            pgnService.fromPgnMove("Ne2", Side.WHITE, nimzoBoard)
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
    void fromPgnTurnList_appliesQGDClassical() {
        Game expectedGame = gameService.startGame();
        for (Move move:List.of(
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PieceType.PAWN, Side.WHITE),
            new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PieceType.PAWN, Side.BLACK),
            new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PieceType.PAWN, Side.WHITE),
            new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.SIX), PieceType.PAWN, Side.BLACK),
            new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
            new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), PieceType.KNIGHT, Side.BLACK),
            new SimpleMove(new Square(FileType.C, RankType.ONE), new Square(FileType.G, RankType.FIVE), PieceType.BISHOP, Side.WHITE),
            new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.D, RankType.SEVEN), PieceType.KNIGHT, Side.BLACK)
        )) {
            gameService.applyMove(expectedGame, move);
        }

        assertEquals(
            expectedGame.getMoveNode().getFirstAncestor(),
            pgnService.fromPgnMoves(PgnFileReader.readOpening(PgnFileReader.QGD_CLASSICAL))
        );
    }

    private final List<Move> nimzoIndianMoves = List.of(
        new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PieceType.PAWN, Side.WHITE),
        new SimpleMove(new Square(FileType.G, RankType.EIGHT), new Square(FileType.F, RankType.SIX), PieceType.KNIGHT, Side.BLACK),
        new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PieceType.PAWN, Side.WHITE),
        new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.SIX), PieceType.PAWN, Side.BLACK),
        new SimpleMove(new Square(FileType.B, RankType.ONE), new Square(FileType.C, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
        new SimpleMove(new Square(FileType.F, RankType.EIGHT), new Square(FileType.B, RankType.FOUR), PieceType.BISHOP, Side.BLACK),
        new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), PieceType.KNIGHT, Side.WHITE),
        new Castle(Side.BLACK, CastleType.SHORT)
    );

    @Test
    void gameFromPgn() {
        Game expectedGame = gameService.startGame();
        for (Move expectedMove: nimzoIndianMoves) {
            gameService.applyMove(
                expectedGame, expectedMove
            );
        }
        assertEquals(
            expectedGame,
            pgnService.gameFromPgn(PgnFileReader.readOpening(PgnFileReader.NIMZO))
        );
    }

    @Test
    void gameFromPgn_withVariations() {
        Game sampleFisherGame = pgnService.gameFromPgn(PgnFileReader.readGame(PgnFileReader.SAMPLE_FISHER_GAME));
        String expectedMoveSummary = "Starting side = WHITE "
            + "Pd2d4 ng8f6 Pc2c4 pg7g6 Nb1c3 bf8g7 Pe2e4 o-o Pe4e5 nf6e8 Pf2f4 pd7d6 Bc1e3 pc7c5 Pd4c5 nb8c6 Pc5d6 pe7d6 Nc3e4 "
            + "("
            + "Pe5d6 ne8d6 "
            + "("
            + "bg7c3 Pb2c3 ne8d6 Be3c5 qd8e7 "
            + "(rf8e8 Bf1e2 nd6e4) "
            + "Bf1e2 rf8d8 Ng1f3 pb7b6 Bc5a3 "
            + "(Bc5f2)"
            + ") "
            + "Ng1f3 "
            + "(Be3c5 bg7c3 Pb2c3 qd8e7 Bf1e2) "
            + "rf8e8 Qd1d2 nd6f5 Qd2d8 bg7c3 Pb2c3 nc6d8"
            + ") "
            + "bc8f5 Ne4g3 "
            + "(Ne4d6 ne8d6 Pe5d6 rf8e8 Qd1e2 nc6d4) "
            + "bf5e6 Ng1f3 qd8c7 Qd1b1 pd6e5 Pf4f5 pe5e4 Pf5e6 pe4f3 Pg2f3 pf7f5 Pf3f4 ne8f6 Bf1e2 rf8e8 Ke1f2 re8e6 Rh1e1 ra8e8 Be2f3 re6e3 Re1e3 re8e3 Kf2e3 <qc7f4> "
            + "6k1/pp4bp/2n2np1/5p2/2P2q2/4KBN1/PP5P/RQ6 0 23";
        assertEquals(
            expectedMoveSummary,
            sampleFisherGame.getMoveNode().toString()
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