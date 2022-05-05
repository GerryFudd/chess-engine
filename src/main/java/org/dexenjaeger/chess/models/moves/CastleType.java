package org.dexenjaeger.chess.models.moves;

import lombok.Getter;
import org.dexenjaeger.chess.models.board.FileType;

@Getter
public enum CastleType {
    SHORT(FileType.H, FileType.F, FileType.G), LONG(FileType.A, FileType.D, FileType.C);

    private final FileType rookFileFrom;
    private final FileType rookFileTo;
    private final FileType kingFileTo;

    CastleType(FileType rookFileFrom, FileType rookFileTo, FileType kingFileTo) {
        this.rookFileFrom = rookFileFrom;
        this.rookFileTo = rookFileTo;
        this.kingFileTo = kingFileTo;
    }
}
