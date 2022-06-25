package org.dexenjaeger.chess.models.analysis;

import java.util.Optional;
import java.util.function.Supplier;

public class ResultHolder<T> {
    private T value;
    private Integer score;

    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    public synchronized boolean set(Supplier<T> value, int score, boolean force) {
        if (this.score == null || (force && score >= this.score) || score > this.score) {
            if (this.score == null || score > this.score) {
                this.score = score;
            }
            this.value = value.get();
            return true;
        }
        return false;
    }
}
