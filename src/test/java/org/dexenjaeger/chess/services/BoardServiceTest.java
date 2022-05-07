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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.PromotionMove;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.PgnFileUtil;
import org.junit.jupiter.api.Test;

class BoardServiceTest {
    public static final ServiceProvider serviceProvider = new ServiceProvider();

    public static void assertPiece(Board board, FileType x, RankType y, Piece expected) {
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

    public static void assertEmpty(Board board, FileType x, RankType y) {
        assertTrue(
            board.getPiece(x, y).isEmpty(),
            String.format("Position %c%d should not have a piece.", x.getCharVal(), y.getAsNumber())
        );
    }

    public static Board nimzoIndianBoard() {
        return serviceProvider.getInstance(PgnService.class)
            .boardFromPgn(PgnFileUtil.readOpening("NimzoIndianDefenseKasparov.pgn"));
    }

    public static Board simpleEndgameWithCFilePromotion() {
        Map<Square, Piece> newBoardState = new HashMap<>();
        newBoardState.put(new Square(FileType.C, RankType.SEVEN), new Piece(WHITE, PAWN));
        newBoardState.put(new Square(FileType.D, RankType.SEVEN), new Piece(WHITE, KING));
        newBoardState.put(new Square(FileType.B, RankType.SEVEN), new Piece(BLACK, KING));
        return new Board(newBoardState);
    }

    @Test
    void getMoves_pinnedPiece() {
        Board board = nimzoIndianBoard();

        // The knight on C3 is pinned
        assertEquals(
            Set.of(),
            serviceProvider.getInstance(BoardService.class)
                .getMoves(board, FileType.C, RankType.THREE)
        );
    }

    @Test
    void getMovesForSide_inCheck() {
        Board board = serviceProvider.getInstance(BoardService.class)
            .applyMove(
                nimzoIndianBoard(),
                new SimpleMove(new Square(FileType.D, RankType.ONE), new Square(FileType.C, RankType.TWO), PieceType.QUEEN, Side.WHITE),
                new SimpleMove(new Square(FileType.B, RankType.FOUR), new Square(FileType.C, RankType.THREE), PieceType.BISHOP, Side.BLACK)
            );

        // White is in check and must address this
        assertEquals(
            Set.of(
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.THREE), PieceType.QUEEN, Side.WHITE),
                new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.D, RankType.TWO), PieceType.QUEEN, Side.WHITE),
                new SimpleMove(new Square(FileType.B, RankType.TWO), new Square(FileType.C, RankType.THREE), PieceType.PAWN, Side.WHITE),
                new SimpleMove(new Square(FileType.E, RankType.ONE), new Square(FileType.D, RankType.ONE), PieceType.KING, Side.WHITE),
                new SimpleMove(new Square(FileType.C, RankType.ONE), new Square(FileType.D, RankType.TWO), PieceType.BISHOP, Side.WHITE),
                new SimpleMove(new Square(FileType.F, RankType.THREE), new Square(FileType.D, RankType.TWO), PieceType.KNIGHT, Side.WHITE)
            ),
            serviceProvider.getInstance(BoardService.class).getMovesBySide(board, Side.WHITE)
        );
    }

    @Test
    void getMoves_pawnPromotion() {
        assertEquals(
            Set.of(
                new PromotionMove(WHITE, FileType.C, ROOK),
                new PromotionMove(WHITE, FileType.C, KNIGHT),
                new PromotionMove(WHITE, FileType.C, BISHOP),
                new PromotionMove(WHITE, FileType.C, QUEEN)
            ),
            serviceProvider
                .getInstance(BoardService.class)
                .getMoves(simpleEndgameWithCFilePromotion(), FileType.C, RankType.SEVEN)
        );
    }
}
