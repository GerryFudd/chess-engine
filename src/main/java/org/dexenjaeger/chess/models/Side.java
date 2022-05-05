package org.dexenjaeger.chess.models;

import lombok.Getter;

public enum Side {
    WHITE("w"), BLACK("b");

    @Getter
    private final String representation;

    Side(String representation) {
        this.representation = representation;
    }

    public Side other() {
        return Side.values()[(ordinal() + 1) % 2];
    }
}
