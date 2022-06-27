package org.dexenjaeger.chess.models.analysis;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Value;
import org.dexenjaeger.chess.models.NodeValue;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.moves.Move;

@Value
public class IterationResult implements NodeValue {
    IterationStatus status;
    AtomicInteger maxTurns;
    int turns;
    int fiftyMoveRuleCounter;
    int turnNumber;
    Move move;
    Board board;

    public IterationResult copyToStatus(IterationStatus newStatus) {
        return new IterationResult(
            newStatus,
            maxTurns,
            turns,
            fiftyMoveRuleCounter,
            turnNumber,
            move,
            board
        );
    }

    public IterationResult copyFromGame(IterationStatus newStatus, int newTurns, Game game) {
        return new IterationResult(
            newStatus,
            maxTurns,
            newTurns,
            game.getGameNode().getValue().getFiftyMoveRuleCounter(),
            game.getGameNode().getValue().getTurnNumber(),
            game.getPreviousMove(),
            game.getCurrentBoard()
        );
    }

    @Override
    public String toString() {
        if (status != IterationStatus.SUCCESS) {
            return status.name();
        }
        return move.toString();
    }
}
