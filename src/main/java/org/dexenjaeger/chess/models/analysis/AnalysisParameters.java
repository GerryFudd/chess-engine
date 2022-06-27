package org.dexenjaeger.chess.models.analysis;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Value;
import org.dexenjaeger.chess.models.game.Game;

@Value
public class AnalysisParameters {
    Game game;
    AtomicInteger maxTurns;
    int iterationNumber;
}
