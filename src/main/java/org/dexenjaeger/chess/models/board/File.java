package org.dexenjaeger.chess.models.board;

import lombok.Getter;

public enum File {
    A('a'), B('b'), C('c'), D('d'),
    E('e'), F('f'), G('g'), H('h');

    @Getter
    private final char val;

    File(char val) {
        this.val = val;
    }
}
