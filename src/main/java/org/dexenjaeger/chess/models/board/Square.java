package org.dexenjaeger.chess.models.board;

import lombok.Value;

@Value
public class Square {
    File file;
    Rank rank;
}
