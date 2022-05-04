package org.dexenjaeger.chess.models.board;

import java.util.Optional;
import lombok.Getter;

public enum File {
    A('a'), B('b'), C('c'), D('d'),
    E('e'), F('f'), G('g'), H('h');

    @Getter
    private final char val;

    public Optional<File> shift(int columns) {
        int newOrdinal = ordinal() + columns;
        File[] files = File.values();
        if (newOrdinal < 0 || newOrdinal >= files.length) {
            return Optional.empty();
        }
        return Optional.of(files[newOrdinal]);
    }

    File(char val) {
        this.val = val;
    }
}
