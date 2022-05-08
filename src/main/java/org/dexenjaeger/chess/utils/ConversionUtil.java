package org.dexenjaeger.chess.utils;

import java.util.Optional;

public class ConversionUtil {
    public static Optional<Pair<Integer, Integer>> directionFromShifts(int xShift, int yShift) {
        if (xShift == 0) {
            if (yShift == 0) {
                return Optional.empty();
            }
            return Optional.of(new Pair<>(0, yShift > 0 ? 1 : -1));
        }
        if (yShift == 0) {
            return Optional.of(new Pair<>(xShift > 0 ? 1 : -1, 0));
        }

        if (Math.abs(xShift) != Math.abs(yShift)) {
            return Optional.empty();
        }

        return Optional.of(new Pair<>(
            xShift / Math.abs(xShift),
            yShift / Math.abs(yShift)
        ));
    }
}
