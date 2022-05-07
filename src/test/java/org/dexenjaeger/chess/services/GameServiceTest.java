package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Set;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.junit.jupiter.api.Test;

class GameServiceTest {
    private final BoardService boardService = new BoardService(new PieceService());
    private final GameService gameService = new GameService(boardService);

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
    void getAvailableMovesTest() {
        Set<Move> availableMoves = gameService.getAvailableMoves(gameService.startGame());
        assertEquals(20, availableMoves.size());
        availableMoves.forEach(mv -> assertEquals(WHITE, mv.getSide()));
    }
}