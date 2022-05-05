package org.dexenjaeger.chess.models.pieces;

import lombok.Getter;

public enum PieceType {
    PAWN(""), ROOK("R"), KNIGHT("N"), BISHOP("B"), QUEEN("Q"), KING("K");

    @Getter
    private final String representation;

    PieceType(String representation) {
        this.representation = representation;
    }
}
