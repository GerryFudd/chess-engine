package org.dexenjaeger.chess.config;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.dexenjaeger.chess.services.NotImplementedException;

public class ServiceProvider {
    private final BindingHolder bindingHolder;

    public ServiceProvider() {
        this(BindingHolder.init(BindingConfig.builder().build()));
    }

    public ServiceProvider(BindingHolder bindingHolder) {
        this.bindingHolder = bindingHolder;
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
        return bindingHolder.getBound(tClass);
    }

    @SneakyThrows
    public <T> T getInstance(Class<T> tClass) {
        return getBoundInstance(tClass).orElseGet(() -> getInjectedInstance(tClass));
    }
}
