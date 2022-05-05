package org.dexenjaeger.chess.models.board;

import java.util.Optional;
import lombok.Getter;

public enum FileType {
    A('a'), B('b'), C('c'), D('d'),
    E('e'), F('f'), G('g'), H('h');

    @Getter
    private final char val;

    public Optional<FileType> shift(int columns) {
        int newOrdinal = ordinal() + columns;
        FileType[] files = FileType.values();
        if (newOrdinal < 0 || newOrdinal >= files.length) {
            return Optional.empty();
        }
        return Optional.of(files[newOrdinal]);
    }

    FileType(char val) {
        this.val = val;
    }

    public String toString() {
        return String.valueOf(this.val);
    }
}
