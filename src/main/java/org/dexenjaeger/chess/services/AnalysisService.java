package org.dexenjaeger.chess.services;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.MoveNode;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

public class AnalysisService {
    private final BoardService boardService;
    private final GameService gameService;

    @Inject
    public AnalysisService(BoardService boardService, GameService gameService) {
        this.boardService = boardService;
        this.gameService = gameService;
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

    private Game detachGameState(Game game) {
        MoveNode moveSummary = game.getMoveSummary();
        return Game.init(
            moveSummary.getTurnNumber(),
            gameService.currentSide(game),
            game.getCurrentBoard(),
            moveSummary.getFiftyMoveRuleCounter()
        );
    }

    public Set<Move> findCheckmateInOne(Game game) {
        return gameService.getAvailableMoves(game)
            .stream()
            .filter(move -> {
                Game potentialGame = gameService.applyMove(detachGameState(game), move);
                return gameService.getGameStatus(potentialGame).isCheckmate();
            })
            .collect(Collectors.toSet());
    }
}
