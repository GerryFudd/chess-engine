package org.dexenjaeger.chess.config;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BindingConfig {
    @Builder.Default
    private final int nThreads = 10;
}
