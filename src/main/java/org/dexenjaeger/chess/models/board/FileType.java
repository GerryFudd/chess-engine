package org.dexenjaeger.chess.models.board;

import java.util.Optional;
import lombok.Getter;

public enum FileType {
    A('a'), B('b'), C('c'), D('d'),
    E('e'), F('f'), G('g'), H('h');

    public static Optional<FileType> fromCharVal(char val) {
        for (FileType type:FileType.values()) {
            if (val == type.getCharVal()) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }

    @Getter
    private final char charVal;

    FileType(char val) {
        this.charVal = val;
    }

    public Optional<FileType> shift(int columns) {
        int newOrdinal = ordinal() + columns;
        FileType[] files = FileType.values();
        if (newOrdinal < 0 || newOrdinal >= files.length) {
            return Optional.empty();
        }
        return Optional.of(files[newOrdinal]);
    }

    public String toString() {
        return String.valueOf(this.charVal);
    }
}
