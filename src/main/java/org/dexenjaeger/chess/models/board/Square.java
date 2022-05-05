package org.dexenjaeger.chess.models.board;

import lombok.Value;

@Value
public class Square {
    FileType file;
    RankType rank;

    public String toString() {
        return String.format("%s%s", file, rank);
    }
}
