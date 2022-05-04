package org.dexenjaeger.chess.models.board;

import lombok.Getter;

public enum Rank {
    ONE(1), TWO(2), THREE(3),
    FOUR(4), FIVE(5), SIX(6),
    SEVEN(7), EIGHT(8);

    @Getter
    private final int asNumber;

    Rank(int asNumber) {
        this.asNumber = asNumber;
    }
}
