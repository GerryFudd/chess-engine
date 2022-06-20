package org.dexenjaeger.chess.services.analysis;

import java.util.Optional;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.MoveSummary;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.services.BoardService;
import org.dexenjaeger.chess.utils.TreeNode;

public class AnalysisService {
    private final BoardService boardService;
    private final CheckmateService checkmateService;

    @Inject
    public AnalysisService(
        BoardService boardService,
        CheckmateService checkmateService
    ) {
        this.boardService = boardService;
        this.checkmateService = checkmateService;
    }

    public int getMaterialScore(Board board, Side side) {
        return board.getBySide(side)
            .stream()
            .map(board::getPiece)
            .flatMap(Optional::stream)
            .map(Piece::getType)
            .mapToInt(PieceType::getValue)
            .sum();
    }

    public int getRelativeMaterialScore(Board board) {
        return getMaterialScore(board, Side.WHITE) - getMaterialScore(board, Side.BLACK);
    }

    public int getPieceActivityScore(Board board, Side side) {
        return boardService.getMovesBySide(board, side).size();
    }

    public Optional<TreeNode<MoveSummary>> findForcedCheckmate(Game game, int maxTurns) {
        return checkmateService.findForcedCheckmate(game, maxTurns);
    }
}
