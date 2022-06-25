package org.dexenjaeger.chess.config;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BindingConfig {
    @Builder.Default
    private final int nThreads = 10;
    @Builder.Default
    private final BigDecimal piecesWeight = new BigDecimal("1.00");
    @Builder.Default
    private final BigDecimal activityWeight = new BigDecimal("1.00");
}
