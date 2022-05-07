package org.dexenjaeger.chess.config;

import java.util.Optional;
import java.util.function.Supplier;

public class Binding<T> {
    private final Class<T> tClass;
    private final Supplier<T> supply;

    public Binding(Class<T> tClass, Supplier<T> supply) {
        this.tClass = tClass;
        this.supply = supply;
    }

    public Binding(Class<T> tClass, T instance) {
        this(tClass, () -> instance);
    }

    public boolean equals(Object other) {
        if (other instanceof Binding) {
            return ((Binding<?>) other).tClass == tClass;
        }
        return false;
    }

    public int hashCode() {
        return tClass.hashCode();
    }

    public <U> Optional<U> getInstanceIfMatch(Class<U> otherClass) {
        if (otherClass == tClass) {
            //noinspection unchecked
            return Optional.of((U) supply.get());
        }
        return Optional.empty();
    }
}
