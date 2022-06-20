package org.dexenjaeger.chess.models.game;

import lombok.Value;
import org.dexenjaeger.chess.models.NodeValue;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Move;

@Value
public class MoveSummary implements NodeValue {
    int turnNumber;
    Move move;
    Board board;
    int fiftyMoveRuleCounter;
    String commentary;

    @Override
    public String shortString() {
        return move.toString();
    }

    @Override
    public String longString() {
        return String.format(
            "%s %d %d",
            board,
            fiftyMoveRuleCounter,
            turnNumber
        );
    }
}
