package org.dexenjaeger.chess.utils;

import lombok.Value;

@Value
public class Pair<T, U> {
    T left;
    U right;
}
