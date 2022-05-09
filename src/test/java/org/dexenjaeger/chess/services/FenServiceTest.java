package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class FenServiceTest {
    private static final ServiceProvider serviceProvider = new ServiceProvider();
    private final FenService fenService = serviceProvider.getInstance(FenService.class);
    private final PgnService pgnService = serviceProvider.getInstance(PgnService.class);
    private final GameService gameService = serviceProvider.getInstance(GameService.class);

    private final Map<Square, Piece> firstRank = Map.of(
        new Square(FileType.A, RankType.ONE), new Piece(Side.WHITE, PieceType.ROOK),
        new Square(FileType.H, RankType.ONE), new Piece(Side.WHITE, PieceType.ROOK),
        new Square(FileType.B, RankType.ONE), new Piece(Side.WHITE, PieceType.KNIGHT),
        new Square(FileType.G, RankType.ONE), new Piece(Side.WHITE, PieceType.KNIGHT),
        new Square(FileType.C, RankType.ONE), new Piece(Side.WHITE, PieceType.BISHOP),
        new Square(FileType.F, RankType.ONE), new Piece(Side.WHITE, PieceType.BISHOP),
        new Square(FileType.D, RankType.ONE), new Piece(Side.WHITE, PieceType.QUEEN),
        new Square(FileType.E, RankType.ONE), new Piece(Side.WHITE, PieceType.KING)
    );

    private final Map<Square, Piece> secondRank = Map.of(
        new Square(FileType.A, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
        new Square(FileType.H, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
        new Square(FileType.B, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
        new Square(FileType.G, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
        new Square(FileType.C, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
        new Square(FileType.F, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
        new Square(FileType.D, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
        new Square(FileType.E, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN)
    );

    private final Map<Square, Piece> seventhRank = Map.of(
        new Square(FileType.A, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN),
        new Square(FileType.H, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN),
        new Square(FileType.B, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN),
        new Square(FileType.G, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN),
        new Square(FileType.C, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN),
        new Square(FileType.F, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN),
        new Square(FileType.D, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN),
        new Square(FileType.E, RankType.SEVEN), new Piece(Side.BLACK, PieceType.PAWN)
    );

    private final Map<Square, Piece> eigthRank = Map.of(
        new Square(FileType.A, RankType.EIGHT), new Piece(Side.BLACK, PieceType.ROOK),
        new Square(FileType.H, RankType.EIGHT), new Piece(Side.BLACK, PieceType.ROOK),
        new Square(FileType.B, RankType.EIGHT), new Piece(Side.BLACK, PieceType.KNIGHT),
        new Square(FileType.G, RankType.EIGHT), new Piece(Side.BLACK, PieceType.KNIGHT),
        new Square(FileType.C, RankType.EIGHT), new Piece(Side.BLACK, PieceType.BISHOP),
        new Square(FileType.F, RankType.EIGHT), new Piece(Side.BLACK, PieceType.BISHOP),
        new Square(FileType.D, RankType.EIGHT), new Piece(Side.BLACK, PieceType.QUEEN),
        new Square(FileType.E, RankType.EIGHT), new Piece(Side.BLACK, PieceType.KING)
    );

    // 16.1.3.1: Piece placement data
    // The first field represents the placement of the pieces on the board.
    // The board contents are specified starting with the eighth rank and
    // ending with the first rank. For each rank, the squares are specified
    // from file a to file h. White pieces are identified by uppercase SAN
    // piece letters ("PNBRQK") and black pieces are identified by lowercase
    // SAN piece letters ("pnbrqk"). Empty squares are represented by the
    // digits one through eight; the digit used represents the count of
    // contiguous empty squares along a rank. A solidus character "/" is used
    // to separate data of adjacent ranks.
    @Test
    void readPiecesFromRank_startingEight() {
        assertEquals(
            eigthRank,
            fenService.readPiecesFromRank(RankType.EIGHT,"rnbqkbnr")
        );
    }
    @Test
    void readPiecesFromRank_startingOne() {
        assertEquals(
            firstRank,
            fenService.readPiecesFromRank(RankType.ONE,"RNBQKBNR")
        );
    }
    @Test
    void readPiecesFromRank_startingSeven() {
        assertEquals(
            seventhRank,
            fenService.readPiecesFromRank(RankType.SEVEN,"pppppppp")
        );
    }
    @Test
    void readPiecesFromRank_startingTwo() {
        assertEquals(
            secondRank,
            fenService.readPiecesFromRank(RankType.TWO,"PPPPPPPP")
        );
    }
    @Test
    void readPiecesFromRank_startingEmptyLines() {
        assertEquals(
            Map.of(),
            fenService.readPiecesFromRank(RankType.THREE,"8")
        );
    }
    @Test
    void readPiecesFromRank_partiallyFullRank() {
        assertEquals(
            Map.of(
                new Square(FileType.B, RankType.FOUR), new Piece(Side.BLACK, PieceType.BISHOP),
                new Square(FileType.F, RankType.FOUR), new Piece(Side.BLACK, PieceType.BISHOP),
                new Square(FileType.C, RankType.FOUR), new Piece(Side.WHITE, PieceType.PAWN),
                new Square(FileType.D, RankType.FOUR), new Piece(Side.WHITE, PieceType.PAWN)
            ),
            fenService.readPiecesFromRank(RankType.FOUR,"1bPP1b2")
        );
    }

    @Test
    void readPieceLocations_startingPosition() {
        assertEquals(
            BoardService.standardGameBoard(),
            fenService.readPieceLocations("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR")
        );
    }

    @ParameterizedTest
    @EnumSource(Side.class)
    void readSide(Side side) {
        assertEquals(
            side,
            fenService.readSide(side.getRepresentation())
        );
    }

    @Test
    void getCastlingRights_all() {
        assertEquals(
            Set.of(
                new Castle(Side.WHITE, CastleType.SHORT),
                new Castle(Side.WHITE, CastleType.LONG),
                new Castle(Side.BLACK, CastleType.SHORT),
                new Castle(Side.BLACK, CastleType.LONG)
            ),
            fenService.getCastlingRights("KQkq")
        );
    }

    @Test
    void getCastlingRights_none() {
        assertEquals(
            Set.of(),
            fenService.getCastlingRights("-")
        );
    }

    @Test
    void getCastlingRights_whiteOnly() {
        assertEquals(
            Set.of(
                new Castle(Side.WHITE, CastleType.SHORT),
                new Castle(Side.WHITE, CastleType.LONG)
            ),
            fenService.getCastlingRights("QK")
        );
    }

    @Test
    void getCastlingRights_allButOne() {
        assertEquals(
            Set.of(
                new Castle(Side.WHITE, CastleType.LONG),
                new Castle(Side.BLACK, CastleType.SHORT),
                new Castle(Side.BLACK, CastleType.LONG)
            ),
            fenService.getCastlingRights("Qqk")
        );
    }

    @Test
    void getEnPassantSquare_placeholder() {
        assertEquals(
            Optional.empty(),
            fenService.getEnPassantSquare("-")
        );
    }

    @Test
    void getEnPassantSquare_validSquare() {
        assertEquals(
            Optional.of(new Square(FileType.E, RankType.THREE)),
            fenService.getEnPassantSquare("e3")
        );
    }

    @Test
    void getGame_startingPosition() {
        String completeStartingBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        assertEquals(
            gameService.startGame(),
            fenService.getGame(completeStartingBoard)
        );
    }

    @Test
    void getGame_afterE4() {
        String completeWithEnPassant = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1";
        assertEquals(
            pgnService.gameFromPgn("1. e4"),
            fenService.getGame(completeWithEnPassant)
        );
    }

    @Test
    void getGame_afterSomeOpeningMoves() {
        Game fullGame = pgnService.gameFromPgn("1. e4 e5 2. Nf3");
        String afterSomeOpeningMoves = "rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2";
        assertEquals(
            Game.init(2, Side.BLACK, fullGame.getCurrentBoard())
                .addCastlingRights(fullGame.getCastlingRights()),
            fenService.getGame(afterSomeOpeningMoves)
        );
    }

    @Test
    void getGame_laterGamePosition() {
        Map<Square, Piece> pieceMap = Map.of(
            new Square(FileType.E, RankType.EIGHT), new Piece(Side.BLACK, PieceType.KING),
            new Square(FileType.E, RankType.TWO), new Piece(Side.WHITE, PieceType.PAWN),
            new Square(FileType.E, RankType.ONE), new Piece(Side.WHITE, PieceType.KING)
        );
        String laterGamePosition = "4k3/8/8/8/8/8/4P3/4K3 w - - 5 39";
        assertEquals(
            Game.init(39, Side.WHITE, new Board(pieceMap)),
            fenService.getGame(laterGamePosition)
        );
    }
}