package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.junit.jupiter.api.Test;

class GameServiceTest {
    private final ServiceProvider serviceProvider = new ServiceProvider();
    private final GameService gameService = serviceProvider.getInstance(GameService.class);
    private final PgnService pgnService = serviceProvider.getInstance(PgnService.class);

    @Test
    void startGameTest() {
        Game initializedGame = gameService.startGame();
        assertEquals(
            BoardService.standardGameBoard(),
            initializedGame.getBoardHistory().getFirst()
        );
        assertEquals(
            List.of(),
            initializedGame.getTurnHistory()
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
}