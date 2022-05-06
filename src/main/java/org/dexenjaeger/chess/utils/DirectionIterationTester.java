package org.dexenjaeger.chess.utils;

import org.dexenjaeger.chess.models.board.Square;

@FunctionalInterface
public interface DirectionIterationTester {
    DirectionIterableTestResult testIteration(Square square);
}
