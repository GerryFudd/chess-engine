package org.dexenjaeger.chess.models;

import java.util.Optional;
import lombok.Getter;

public enum TagType {
    EVENT("Event"), SITE("Site"), DATE("Date"),
    ROUND("Round"), WHITE("White"), BLACK("Black"),
    RESULT("Result");
    
    @Getter
    private final String displayName;

    TagType(String displayName) {
        this.displayName = displayName;
    }

    public static Optional<TagType> fromLabel(String label) {
        for (TagType type:values()) {
            if (type.getDisplayName().equals(label)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
}
