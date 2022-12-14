package org.dexenjaeger.chess.utils;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalsUtil {
    public static <T, U extends Enum<U>> Predicate<T> emptyOrMatches(Supplier<Optional<U>> maybeVal, Function<T, U> getter) {
        return obj -> maybeVal.get()
            .filter(val -> val != getter.apply(obj))
            .isEmpty();
    }

    public static <T, U, V> Optional<V> merge(Supplier<Optional<T>> maybeT, Supplier<Optional<U>> maybeU, Function<Pair<T, U>, V> processor) {
        return maybeT.get()
            .flatMap(t -> maybeU.get().map(u -> new Pair<>(t, u)).map(processor));
    }
}
