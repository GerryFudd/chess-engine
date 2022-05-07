package org.dexenjaeger.chess.config;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import org.dexenjaeger.chess.services.NotImplementedException;

public class ServiceProvider {
    private final Set<Binding<?>> bindings = new HashSet<>();

    protected <T> ServiceProvider bind(Class<T> tClass, Supplier<T> supplier) {
        bindings.add(new Binding<>(tClass, supplier));
        return this;
    }

    protected <T> ServiceProvider bind(Class<T> tClass, T instance) {
        bindings.add(new Binding<>(tClass, instance));
        return this;
    }

    protected <T, U extends T> ServiceProvider bind(Class<T> tClass, Class<U> uClass) {
        bindings.add(new Binding<T>(tClass, () -> getInjectedInstance(uClass)));
        return this;
    }

    @SneakyThrows
    private <T> T getInjectedInstance(Class<T> tClass) {
        for (Constructor<?> constructor:tClass.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return tClass.getDeclaredConstructor().newInstance();
            }
            if (constructor.getAnnotation(Inject.class) == null) {
                continue;
            }
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Constructor<T> declaredConstructor = tClass.getDeclaredConstructor(parameterTypes);
            List<Object> parameters = new LinkedList<>();
            while (parameters.size() < parameterTypes.length) {
                parameters.add(getInstance(parameterTypes[parameters.size()]));
            }
            return declaredConstructor.newInstance(parameters.toArray());
        }

        throw new NotImplementedException(tClass);
    }

    private <T> Optional<T> getBoundInstance(Class<T> tClass) {
        return bindings.stream()
            .map(b -> b.getInstanceIfMatch(tClass))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findAny();
    }

    @SneakyThrows
    public <T> T getInstance(Class<T> tClass) {
        return getBoundInstance(tClass).orElseGet(() -> getInjectedInstance(tClass));
    }
}
