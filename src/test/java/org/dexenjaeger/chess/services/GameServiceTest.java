package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.GameStatus.WHITE_WON;
import static org.dexenjaeger.chess.models.Side.BLACK;
import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.MoveSummary;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.EnPassantCapture;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.ZeroMove;
import org.dexenjaeger.chess.utils.TreeNode;
import org.junit.jupiter.api.Test;

class GameServiceTest {
    private final ServiceProvider serviceProvider = new ServiceProvider();
    private final GameService gameService = serviceProvider.getInstance(GameService.class);
    private final PgnService pgnService = serviceProvider.getInstance(PgnService.class);
    private final FenService fenService = serviceProvider.getInstance(FenService.class);

    @Test
    void startGameTest() {
        Game initializedGame = gameService.startGame();
        assertEquals(
            new TreeNode<>(new MoveSummary(
                0, new ZeroMove(BLACK), BoardService.standardGameBoard(), 0, null
            ), null, null),
            initializedGame.getMoveNode()
        );
        assertEquals(
            Set.of(
                new Castle(WHITE, CastleType.LONG),
                new Castle(WHITE, CastleType.SHORT),
                new Castle(Side.BLACK, CastleType.LONG),
                new Castle(Side.BLACK, CastleType.SHORT)
            ),
            initializedGame.getCastlingRights()
        );
    }

    @Test
    void getAvailableMovesTest_startingPosition() {
        Set<Move> availableMoves = gameService.getAvailableMoves(gameService.startGame());
        assertEquals(20, availableMoves.size());
        availableMoves.forEach(mv -> assertEquals(WHITE, mv.getSide()));
    }

    @Test
    void getAvailableMovesTest_includesCastling() {
        Game game = pgnService.gameFromPgn("1. Nf3 b6 2. g3 Bb7 3. Bg2 e6");
        Set<Move> availableMoves = gameService.getAvailableMoves(game);
        assertTrue(
            availableMoves.contains(new Castle(WHITE, CastleType.SHORT)),
            "Available moves should include castling."
        );
    }

    @Test
    void getAvailableMovesTest_includesEnPassantForWhite() {
        Game game = pgnService.gameFromPgn("1. d4 d5 2. c4 e6 3. Nc3 c6 4. c5 b5");
        Set<Move> availableMoves = gameService.getAvailableMoves(game);
        assertTrue(
            availableMoves.contains(new EnPassantCapture(WHITE, FileType.C, FileType.B)),
            "Available moves should include en passant captures."
        );
    }

    @Test
    void getAvailableMovesTest_removesEnPassantMoveAfterOneTurn() {
        Game game = pgnService.gameFromPgn("1. d4 d5 2. c4 e6 3. Nc3 c6 4. c5 b5 5. Nf3 a5");
        Set<Move> availableMoves = gameService.getAvailableMoves(game);
        assertFalse(
            availableMoves.contains(new EnPassantCapture(WHITE, FileType.C, FileType.B)),
            "Available moves should not include en passant captures after one turn."
        );
    }

    @Test
    void getAvailableMovesTest_includesMultipleEnPassantForWhite() {
        Game game = pgnService.gameFromPgn("1. d4 d5 2. c4 e6 3. Nc3 c6 4. c5 Nf6 "
            + "5. a4 Nbd7 6. a5 b5");
        Set<Move> availableMoves = gameService.getAvailableMoves(game);
        assertTrue(
            availableMoves.containsAll(Set.of(
                new EnPassantCapture(WHITE, FileType.C, FileType.B),
                new EnPassantCapture(WHITE, FileType.A, FileType.B)
            )),
            "Available moves should include all en passant captures."
        );
    }

    @Test
    void getAvailableMovesTest_includesEnPassantForBlack() {
        Game game = pgnService.gameFromPgn("1. d4 d5 2. c4 e5 3. Nf3 e4 4. Nfd2 Nf6 5. f4");
        Set<Move> availableMoves = gameService.getAvailableMoves(game);
        assertTrue(
            availableMoves.contains(new EnPassantCapture(BLACK, FileType.E, FileType.F)),
            "Available moves should include en passant captures."
        );
    }

    @Test
    void detachGameStateTest_accuratelyStartsFromCurrent() {
        Game game = pgnService.gameFromPgn("1. d4 d5 2. c4 e5 3. Nf3 e4 4. Nfd2 Nf6 5. f4");
        Game detachedGame = gameService.detachGameState(game);

        Board expectedBoard = fenService.readPieceLocations("rnbqkb1r/ppp2ppp/5n2/3p4/2PPpP2/8/PP1NP1PP/RNBQKB1R");
        assertEquals(expectedBoard, detachedGame.getCurrentBoard());
        assertEquals(game.getCastlingRights(), detachedGame.getCastlingRights());
        assertEquals(
            new TreeNode<>(
                new MoveSummary(
                    5, new ZeroMove(WHITE), game.getCurrentBoard(), 0, null
                ),
                null, null
            ),
            detachedGame.getMoveNode()
        );
    }

    @Test
    void detachGameStateTest_accuratelyStartsFromParent() {
        Game game = pgnService.gameFromPgn("1. d4 d5 2. c4 e5 3. Nf3 e4 4. Nfd2 Nf6 5. f4").goToParentMove();
        Game detachedGame = gameService.detachGameState(game);

        Board expectedBoard = fenService.readPieceLocations("rnbqkb1r/ppp2ppp/5n2/3p4/2PPp3/8/PP1NPPPP/RNBQKB1R");
        assertEquals(expectedBoard, detachedGame.getCurrentBoard());
        assertEquals(game.getCastlingRights(), detachedGame.getCastlingRights());
        assertEquals(
            new TreeNode<>(
                new MoveSummary(4, new ZeroMove(BLACK), game.getCurrentBoard(), 2, null),
                null, null
            ),
            detachedGame.getMoveNode()
        );
    }

    @Test
    void detachGameStateTest_accuratelyErasesEverythingElse() {
        Game game = pgnService.gameFromPgn("1. d4 d5 2. c4 e5 3. Nf3 e4 4. Nfd2 Nf6 5. f4").goToParentMove();
        Game detachedGame = gameService.detachGameState(game.goToFirstMove());

        assertEquals(gameService.startGame(), detachedGame);
    }

    @Test
    void getGameStatusTest_recognizesCheckmate() {
        Game game = pgnService.gameFromPgn("1. e4 e5 2. Bc4 Nc6 3. Qf3 d6 4. Qf7");
        assertEquals(
            WHITE_WON,
            gameService.getGameStatus(game)
        );
    }
}