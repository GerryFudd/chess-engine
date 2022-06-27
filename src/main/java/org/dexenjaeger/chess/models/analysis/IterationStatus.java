package org.dexenjaeger.chess.models.analysis;

import lombok.Getter;

public enum IterationStatus {
    FAILURE(true, true), SUCCESS(true, true), DEFERRED_OPPONENT(false, true),
    DEFERRED(false, true), UNEXPLORED_OPPONENT(false, false), UNEXPLORED(false, false);
    @Getter
    private final boolean complete;
    @Getter
    private final boolean explored;

    IterationStatus(boolean complete, boolean explored) {
        this.complete = complete;
        this.explored = explored;
    }
}
