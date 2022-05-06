package org.dexenjaeger.chess.models.moves;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

    public Turn(int turnNumber, Move whiteMove) {
        this(turnNumber, whiteMove, null);
    }

    public Optional<Move> getBlackMove() {
        return Optional.ofNullable(blackMove);
    }
}
