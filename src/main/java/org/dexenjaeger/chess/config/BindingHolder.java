package org.dexenjaeger.chess.config;

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

    private <T> BindingHolder bind(Class<T> tClass, Supplier<T> supplier) {
        bindings.add(new Binding<>(tClass, supplier));
        return this;
    }

    private <T> BindingHolder bind(Class<T> tClass, T instance) {
        bindings.add(new Binding<>(tClass, instance));
        return this;
    }

    public static BindingHolder init(BindingConfig config) {
        return new BindingHolder(new HashSet<>())
            .bind(ThreadService.class, ThreadService.init(config.getNThreads()));
    }

    public <T> Optional<T> getBound(Class<T> tClass) {
        return bindings.stream()
            .flatMap(b -> b.getInstanceIfMatch(tClass).stream())
            .findAny();
    }
}
