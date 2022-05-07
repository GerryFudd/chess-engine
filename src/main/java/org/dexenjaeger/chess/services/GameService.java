package org.dexenjaeger.chess.services;

import java.util.Set;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;

public class GameService {
    private final BoardService boardService;

    @Inject
    public GameService(BoardService boardService) {
        this.boardService = boardService;
    }

    public Game startGame() {
        Game result = new Game()
            .addBoard(BoardService.standardGameBoard());

        for (Side side:Side.values()) {
            for (CastleType type:CastleType.values()) {
                result.getCastlingRights().add(new Castle(side, type));
            }
        }

        return result;
    }

    public Side currentSide(Game game) {
        if (game.getTurnHistory().isEmpty()) {
            return Side.WHITE;
        }
        return game.getTurnHistory().getLast().getBlackMove().isPresent()
            ? Side.WHITE
            : Side.BLACK;
    }

    public Set<Move> getAvailableMoves(Game game) {
        return boardService.getMovesBySide(
            game.getBoardHistory().getLast(),
            currentSide(game)
        );
    }
}
