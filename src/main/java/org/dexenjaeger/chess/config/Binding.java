package org.dexenjaeger.chess.config;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

public class Binding<T> {
    private final Class<T> tClass;
    private final String tag;
    private final Supplier<T> supplier;

    public Binding(Class<T> tClass, String tag, Supplier<T> supplier) {
        this.tClass = tClass;
        this.tag = tag;
        this.supplier = supplier;
    }


    public boolean equals(Object other) {
        if (other instanceof Binding) {
            return ((Binding<?>) other).tClass == tClass && Objects.equals(((Binding<?>) other).tag, tag);
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(tClass.hashCode(), tag);
    }

    public <U> Optional<U> getInstanceIfMatch(Class<U> otherClass, String tag) {
        if (otherClass == tClass && Objects.equals(this.tag, tag)) {
            //noinspection unchecked
            return Optional.of((U) supplier.get());
        }
        return Optional.empty();
    }
}
