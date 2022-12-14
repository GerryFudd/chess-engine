package org.dexenjaeger.chess.models.board;

import java.util.Optional;
import lombok.Getter;

public enum RankType {
    ONE(1), TWO(2), THREE(3),
    FOUR(4), FIVE(5), SIX(6),
    SEVEN(7), EIGHT(8);

    public static Optional<RankType> fromString(String val) {
        if (val == null) {
            return Optional.empty();
        }
        try {
            int asNum = Integer.parseInt(val);
            if (asNum < 1 || asNum > 8) {
                return Optional.empty();
            }
            return Optional.of(RankType.values()[asNum - 1]);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Getter
    private final int asNumber;

    RankType(int asNumber) {
        this.asNumber = asNumber;
    }

    public Optional<RankType> shift(int rows) {
        int newOrdinal = ordinal() + rows;
        RankType[] ranks = RankType.values();
        if (newOrdinal < 0 || newOrdinal >= ranks.length) {
            return Optional.empty();
        }
        return Optional.of(ranks[newOrdinal]);
    }

    public String toString() {
        return String.valueOf(this.asNumber);
    }
}
