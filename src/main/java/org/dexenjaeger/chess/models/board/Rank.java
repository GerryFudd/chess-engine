package org.dexenjaeger.chess.models.board;

import java.util.Optional;
import lombok.Getter;

public enum Rank {
    ONE(1), TWO(2), THREE(3),
    FOUR(4), FIVE(5), SIX(6),
    SEVEN(7), EIGHT(8);

    @Getter
    private final int asNumber;

    public Optional<Rank> shift(int rows) {
        int newOrdinal = ordinal() + rows;
        Rank[] ranks = Rank.values();
        if (newOrdinal < 0 || newOrdinal >= ranks.length) {
            return Optional.empty();
        }
        return Optional.of(ranks[newOrdinal]);
    }

    Rank(int asNumber) {
        this.asNumber = asNumber;
    }
}
