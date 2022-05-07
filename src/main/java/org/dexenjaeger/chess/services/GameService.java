package org.dexenjaeger.chess.services;

import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;

public class GameService {
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
}
