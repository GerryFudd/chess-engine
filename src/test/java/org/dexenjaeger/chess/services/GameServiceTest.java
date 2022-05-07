package org.dexenjaeger.chess.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.junit.jupiter.api.Test;

class GameServiceTest {
    private final BoardService boardService = new BoardService(new PieceService());
    private final GameService gameService = new GameService();

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
                new Castle(Side.WHITE, CastleType.LONG),
                new Castle(Side.WHITE, CastleType.SHORT),
                new Castle(Side.BLACK, CastleType.LONG),
                new Castle(Side.BLACK, CastleType.SHORT)
            ),
            initializedGame.getCastlingRights()
        );
    }
}