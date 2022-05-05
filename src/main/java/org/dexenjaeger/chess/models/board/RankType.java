package org.dexenjaeger.chess.models.board;

import java.util.Optional;
import lombok.Getter;

public enum RankType {
    ONE(1), TWO(2), THREE(3),
    FOUR(4), FIVE(5), SIX(6),
    SEVEN(7), EIGHT(8);

    @Getter
    private final int asNumber;

    public Optional<RankType> shift(int rows) {
        int newOrdinal = ordinal() + rows;
        RankType[] ranks = RankType.values();
        if (newOrdinal < 0 || newOrdinal >= ranks.length) {
            return Optional.empty();
        }
        return Optional.of(ranks[newOrdinal]);
    }

    RankType(int asNumber) {
        this.asNumber = asNumber;
    }

    public String toString() {
        return String.valueOf(this.asNumber);
    }
}
