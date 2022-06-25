package org.dexenjaeger.chess.services.analysis;

import java.math.BigDecimal;
import java.util.Optional;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.GameSnapshot;
import org.dexenjaeger.chess.utils.TreeNode;

public class AnalysisService {
    private final ScoreService scoreService;
    private final CheckmateService checkmateService;

    @Inject
    public AnalysisService(
        ScoreService scoreService,
        CheckmateService checkmateService
    ) {
        this.scoreService = scoreService;
        this.checkmateService = checkmateService;
    }

    public Optional<TreeNode<GameSnapshot>> findForcedCheckmate(Game game, int maxTurns) {
        return checkmateService.findForcedCheckmate(game, maxTurns);
    }

    public int getMaterialScore(Board board) {
        return scoreService.getRelativeMaterialScore(board);
    }

    public int getPieceActivityScore(Board board) {
        return scoreService.getRelativePieceActivityScore(board);
    }

    public BigDecimal getScore(Board board) {
        return scoreService.getWeightedScore(board);
    }
}
