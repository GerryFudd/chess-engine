package org.dexenjaeger.chess.models.pieces;

import lombok.Getter;

public enum PieceType {
    PAWN("P", 1), ROOK("R", 5), KNIGHT("N", 3), BISHOP("B", 3), QUEEN("Q", 9), KING("K", 0);

    @Getter
    private final String representation;
    @Getter
    private final int value;

    PieceType(String representation, int value) {
        this.representation = representation;
        this.value = value;
    }
}
