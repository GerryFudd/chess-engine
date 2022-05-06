package org.dexenjaeger.chess.services;

public class NotImplementedException extends ServiceException {
    public NotImplementedException(Class<?> clazz) {
        super(String.format(
            "Not implemented for class %s",
            clazz.getName()
        ));
    }
    public <T extends Enum<T>> NotImplementedException(T type) {
        super(String.format(
            "Not implemented for type %s",
            type.name()
        ));
    }
}
