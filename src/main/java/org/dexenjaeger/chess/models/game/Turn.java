package org.dexenjaeger.chess.models.game;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.dexenjaeger.chess.models.moves.Move;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Turn {
    @Getter
    private final int turnNumber;

    @Getter
    private final Move whiteMove;

    @Setter
    private Move blackMove;

    @Getter
    @Setter
    private String whiteMoveCommentary;

    @Getter
    @Setter
    private String blackMoveCommentary;

    public Turn(int turnNumber, Move whiteMove) {
        this(turnNumber, whiteMove, null, null, null);
    }

    public Turn(int turnNumber, Move whiteMove, String whiteMoveCommentary) {
        this(turnNumber, whiteMove, null, whiteMoveCommentary, null);
    }

    public Turn(int turnNumber, Move whiteMove, Move blackMove) {
        this(turnNumber, whiteMove, blackMove, null, null);
    }

    public Optional<Move> getBlackMove() {
        return Optional.ofNullable(blackMove);
    }

    public boolean isEmpty() {
        return whiteMove == null && blackMove == null;
    }
}
