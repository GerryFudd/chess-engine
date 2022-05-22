package org.dexenjaeger.chess.models;

import lombok.Getter;

public enum GameStatus {
    WHITE_TO_MOVE(false), BLACK_TO_MOVE(false), STALEMATE(false), WHITE_WON(true), BLACK_WON(true);

    @Getter
    private final boolean checkmate;

    GameStatus(boolean checkmate) {
        this.checkmate = checkmate;
    }
}
