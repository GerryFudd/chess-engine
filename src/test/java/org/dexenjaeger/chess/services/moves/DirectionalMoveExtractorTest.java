package org.dexenjaeger.chess.services.moves;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.DirectionIterableTestResult;
import org.dexenjaeger.chess.utils.Pair;
import org.junit.jupiter.api.Test;

class DirectionalMoveExtractorTest {
    @Test
    void canMove() {
        DirectionalMoveExtractor moveExtractor = new DirectionalMoveExtractor(
            Side.WHITE, PieceType.BISHOP, List.of(
                new Pair<>(1, 1),
                new Pair<>(1, -1),
                new Pair<>(-1, 1),
                new Pair<>(-1, -1)
            ),
            (s) -> DirectionIterableTestResult.CONTINUE
        );
        assertFalse(
            moveExtractor.canMove(new Square(FileType.E, RankType.THREE), new Square(FileType.E, RankType.TWO)),
            "Should not be able to move a bishop from e3 to e2."
        );
    }
}