package org.dexenjaeger.chess.config;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import org.dexenjaeger.chess.services.ThreadService;

public class BindingHolder {
    private final Set<Binding<?>> bindings;

    private BindingHolder(Set<Binding<?>> bindings) {
        this.bindings = bindings;
    }

    private <T> BindingHolder bind(Class<T> tClass, String tag, Supplier<T> supplier) {
        bindings.add(new Binding<>(tClass, tag, supplier));
        return this;
    }

    private <T> BindingHolder bind(Class<T> tClass, Supplier<T> supplier) {
        return bind(tClass, null, supplier);
    }

    private <T> BindingHolder bind(Class<T> tClass, String tag, T instance) {
        return bind(tClass, tag, (Supplier<T>) () -> instance);
    }

    private <T> BindingHolder bind(Class<T> tClass, T instance) {
        return bind(tClass, (Supplier<T>) () -> instance);
    }

    public static BindingHolder init(BindingConfig config) {
        return new BindingHolder(new HashSet<>())
            .bind(ThreadService.class, ThreadService.init(config.getNThreads()))
            .bind(BigDecimal.class, BindingTags.PIECES_WEIGHT, config.getPiecesWeight())
            .bind(BigDecimal.class, BindingTags.ACTIVITY_WEIGHT, config.getActivityWeight());
    }

    public <T> Optional<T> getBound(Class<T> tClass) {
        return getBound(tClass, null);
    }

    public <T> Optional<T> getBound(Class<T> tClass, String tag) {
        return bindings.stream()
            .flatMap(b -> b.getInstanceIfMatch(tClass, tag).stream())
            .findAny();
    }
}
