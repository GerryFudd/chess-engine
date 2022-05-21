package org.dexenjaeger.chess.models.game;

import static org.dexenjaeger.chess.models.Side.BLACK;
import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.dexenjaeger.chess.models.pieces.PieceType.BISHOP;
import static org.dexenjaeger.chess.models.pieces.PieceType.KING;
import static org.dexenjaeger.chess.models.pieces.PieceType.KNIGHT;
import static org.dexenjaeger.chess.models.pieces.PieceType.PAWN;
import static org.dexenjaeger.chess.models.pieces.PieceType.QUEEN;
import static org.dexenjaeger.chess.models.pieces.PieceType.ROOK;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.PromotionMove;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.moves.ZeroMove;
import org.dexenjaeger.chess.services.BoardService;
import org.junit.jupiter.api.Test;

class MoveNodeTest {
    private final static ServiceProvider serviceProvider = new ServiceProvider();
    private final BoardService boardService = serviceProvider.getInstance(BoardService.class);

    @Test
    void toString_starting() {
        Board board = BoardService.standardGameBoard();
        assertEquals(
            String.format("Starting side = WHITE %s", board),
            new MoveNode(0, new ZeroMove(Side.BLACK), board).toString()
        );
    }

    @Test
    void toString_WithSingleMove() {
        Move firstMove =  new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE);
        Board board = boardService.applyMove(MoveNode.opening().getBoard(), firstMove);
        MoveNode moveNode = MoveNode.opening().addChild(
           firstMove, board
        );
        assertEquals(
            String.format("Starting side = WHITE Pd2d4 %s", board),
           moveNode.toString()
        );
    }

    @Test
    void toString_WithParentAndChildren() {
        MoveNode moveNode = MoveNode.opening();
        for (Move move:List.of(
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE),
            new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PAWN, BLACK),
            new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PAWN, WHITE)
        )) {
            moveNode = moveNode.addChild(move, boardService.applyMove(moveNode.getBoard(), move));
        }
        moveNode = moveNode.getParent().orElseThrow();
        assertEquals(
            String.format("Starting side = WHITE Pd2d4 Pd7d5 Pc2c4 %s", moveNode.getBoard()),
           moveNode.toString()
        );
    }

    @Test
    void toString_WithParentAndMultipleChildren() {
        MoveNode moveNode = MoveNode.opening();
        for (Move move:List.of(
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE),
            new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PAWN, BLACK),
            new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PAWN, WHITE)
        )) {
            moveNode = moveNode.addChild(move, boardService.applyMove(moveNode.getBoard(), move));
        }
        moveNode = moveNode.getParent().orElseThrow();
        Move alternateMove1 = new SimpleMove(new Square(FileType.C, RankType.ONE), new Square(FileType.F, RankType.FOUR), BISHOP, WHITE);
        moveNode.addChild(alternateMove1, boardService.applyMove(moveNode.getBoard(), alternateMove1));

        Move alternateMove2 = new SimpleMove(new Square(FileType.G, RankType.ONE), new Square(FileType.F, RankType.THREE), KNIGHT, WHITE);
        moveNode.addChild(alternateMove2, boardService.applyMove(moveNode.getBoard(), alternateMove2));

        assertEquals(
            String.format("Starting side = WHITE Pd2d4 Pd7d5... (Bc1f4) (Ng1f3) Pc2c4 %s", moveNode.getBoard()),
            moveNode.toString()
        );
    }

    private static class MoveLine {
        private final BoardService service;
        private MoveNode tail;

        private MoveLine(BoardService service, MoveNode tail) {
            this.service = service;
            this.tail = tail;
        }

        private MoveNode getMoveResult(Move move) {
            return tail.addChild(move, service.applyMove(tail.getBoard(), move));
        }

        public MoveNode applyMoves(Move... moves) {
            for (Move move:moves) {
                tail = getMoveResult(move);
            }
            return tail;
        }
    }

    @Test
    void toString_WithMultipleGenerationsInABranch() {
        MoveNode ancestor = MoveNode.opening();

        // This follows the last move in the main line
        MoveLine mainLine = new MoveLine(boardService, ancestor);

        // Apply moves until we get to the first branch
        // Pd2d4 pd7d5 Pc2c4 pe7e5 Pd4e5 pd5d4 Pe2e3 bf8b4 Bc1d2 pd4e3 Bd2b4 pe3f2 Ke1e2 pf2g1=N
        // Start a branch just before Ke2e1 (first variation)
        MoveNode firstBranchNode = mainLine.applyMoves(
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.D, RankType.FOUR), PAWN, WHITE),
            new SimpleMove(new Square(FileType.D, RankType.SEVEN), new Square(FileType.D, RankType.FIVE), PAWN, BLACK),
            new SimpleMove(new Square(FileType.C, RankType.TWO), new Square(FileType.C, RankType.FOUR), PAWN, WHITE),
            new SimpleMove(new Square(FileType.E, RankType.SEVEN), new Square(FileType.E, RankType.FIVE), PAWN, BLACK),
            new SimpleMove(new Square(FileType.D, RankType.FOUR), new Square(FileType.E, RankType.FIVE), PAWN, WHITE),
            new SimpleMove(new Square(FileType.D, RankType.FIVE), new Square(FileType.D, RankType.FOUR), PAWN, BLACK),
            new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.THREE), PAWN, WHITE),
            new SimpleMove(new Square(FileType.F, RankType.EIGHT), new Square(FileType.B, RankType.FOUR), BISHOP, BLACK),
            new SimpleMove(new Square(FileType.C, RankType.ONE), new Square(FileType.D, RankType.TWO), BISHOP, WHITE),
            new SimpleMove(new Square(FileType.D, RankType.FOUR), new Square(FileType.E, RankType.THREE), PAWN, BLACK),
            new SimpleMove(new Square(FileType.D, RankType.TWO), new Square(FileType.B, RankType.FOUR), BISHOP, WHITE),
            new SimpleMove(new Square(FileType.E, RankType.THREE), new Square(FileType.F, RankType.TWO), PAWN, BLACK),
            new SimpleMove(new Square(FileType.E, RankType.ONE), new Square(FileType.E, RankType.TWO), KING, WHITE),
            new PromotionMove(BLACK, FileType.F, FileType.G, KNIGHT)
        );

        // Apply Ke2e1
        // Start just before qd8h4
        MoveNode secondBranchNode = mainLine
            .applyMoves(new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.ONE), KING, WHITE));

        // This is the termination of the main line
        // qd8h4 (second variation)
        mainLine.applyMoves(
            new SimpleMove(new Square(FileType.D, RankType.EIGHT), new Square(FileType.H, RankType.FOUR), QUEEN, BLACK)
        );

        // Fill in the first variation (Rh1g1 bc8g4 Ke2e1 qd8d1 Ke1f2)
        MoveLine firstVariation = new MoveLine(boardService, firstBranchNode);
        firstVariation.applyMoves(
            new SimpleMove(new Square(FileType.H, RankType.ONE), new Square(FileType.G, RankType.ONE), ROOK, WHITE),
            new SimpleMove(new Square(FileType.C, RankType.EIGHT), new Square(FileType.G, RankType.FOUR), BISHOP, BLACK),
            new SimpleMove(new Square(FileType.E, RankType.TWO), new Square(FileType.E, RankType.ONE), KING, WHITE),
            new SimpleMove(new Square(FileType.D, RankType.EIGHT), new Square(FileType.D, RankType.ONE), QUEEN, BLACK),
            new SimpleMove(new Square(FileType.E, RankType.ONE), new Square(FileType.F, RankType.TWO), KING, WHITE)
        );

        // Fill in the second variation (qd8d1 Ke1d1 ...)
        // Start a new node just before bc8g4 (third variation)
        MoveLine secondVariation = new MoveLine(boardService, secondBranchNode);
        MoveNode thirdBranchNode = secondVariation.applyMoves(
            new SimpleMove(new Square(FileType.D, RankType.EIGHT), new Square(FileType.D, RankType.ONE), QUEEN, BLACK),
            new SimpleMove(new Square(FileType.E, RankType.ONE), new Square(FileType.D, RankType.ONE), KING, WHITE)
        );

        // This is the termination of the second variation
        // bc8g4 (third variation) Kd1e1
        secondVariation.applyMoves(
            new SimpleMove(new Square(FileType.C, RankType.EIGHT), new Square(FileType.G, RankType.FOUR), BISHOP, BLACK),
            new SimpleMove(new Square(FileType.D, RankType.ONE), new Square(FileType.E, RankType.ONE), KING, WHITE)
        );

        // Fill in the third variation (nb8c6 Bb4c3 bc8g4 Kd1e1 O-O-O Rh1g1 rd8d1 Ke1f2)
        MoveLine thirdVariation = new MoveLine(boardService, thirdBranchNode);
        thirdVariation.applyMoves(
            new SimpleMove(new Square(FileType.B, RankType.EIGHT), new Square(FileType.C, RankType.SIX), KNIGHT, BLACK),
            new SimpleMove(new Square(FileType.B, RankType.FOUR), new Square(FileType.C, RankType.THREE), BISHOP, WHITE),
            new SimpleMove(new Square(FileType.C, RankType.EIGHT), new Square(FileType.G, RankType.FOUR), BISHOP, BLACK),
            new SimpleMove(new Square(FileType.D, RankType.ONE), new Square(FileType.E, RankType.ONE), KING, WHITE),
            new Castle(BLACK, CastleType.LONG),
            new SimpleMove(new Square(FileType.H, RankType.ONE), new Square(FileType.G, RankType.ONE), ROOK, WHITE),
            new SimpleMove(new Square(FileType.D, RankType.EIGHT), new Square(FileType.D, RankType.ONE), ROOK, BLACK),
            new SimpleMove(new Square(FileType.E, RankType.ONE), new Square(FileType.F, RankType.TWO), KING, WHITE)
        );

        while (ancestor.getParent().isPresent()) {
            ancestor = ancestor.getParent().orElseThrow();
        }

        String expectedString = "Starting side = WHITE Pd2d4 pd7d5 Pc2c4 pe7e5 Pd4e5 pd5d4 Pe2e3 bf8b4 Bc1d2 pd4e3 Bd2b4 pe3f2 Ke1e2 " 
            + "pf2g1=N Ke2e1 (Rh1g1 bc8g4 Ke2e1 qd8d1 Ke1f2) qd8h4 (qd8d1 Ke1d1 bc8g4 (nb8c6 Bb4c3 bc8g4 Kd1e1 o-o-o Rh1g1 rd8d1 Ke1f2) Kd1e1)";
        assertEquals(
            String.format(
                "%s %s",
                expectedString,
                ancestor.getBoard()),
            ancestor.toString()
        );
    }
}
