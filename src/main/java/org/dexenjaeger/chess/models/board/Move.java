package org.dexenjaeger.chess.models.board;

import lombok.Value;

@Value
public class Move {
    Square from;
    Square to;
}
