package org.dexenjaeger.chess.models;

public enum Side {
    WHITE, BLACK;

    public Side other() {
        return Side.values()[(ordinal() + 1) % 2];
    }
}
