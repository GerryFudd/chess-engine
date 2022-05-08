package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.junit.jupiter.api.Test;

class FenServiceTest {
    private static final ServiceProvider serviceProvider = new ServiceProvider();
    private final FenService fenService = serviceProvider.getInstance(FenService.class);

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
            fenService.readPieceLocations("rnbqkbnr / pppppppp / 8 / 8 / 8 / 8 / PPPPPPPP / RNBQKBNR")
        );
    }
}