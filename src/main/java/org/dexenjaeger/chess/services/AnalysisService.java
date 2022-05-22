package org.dexenjaeger.chess.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.GameStatus;
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

    public Game detachGameState(Game game) {
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

    private Game mergeDescendents(Game game, Game gameToMerge) {
        if (!game.getCurrentBoard().equals(gameToMerge.getCurrentBoard())) {
            throw new RuntimeException(String.format(
                "Game board %s doesn't match the game board to merge %s.",
                game.getCurrentBoard(), gameToMerge.getCurrentBoard()
            ));
        }
        for (Move move:gameToMerge.getAttemptedMoves()) {
            gameService.applyMove(game, move);
            gameToMerge.goToAttemptedMove(move);
            mergeDescendents(game, gameToMerge);
            game.goToParentMove();
            gameToMerge.goToParentMove();
        }
        return game;
    }

    private Game findForcedCheckmateFromDetached(Game game, Side startingSide, int maxMoves) {
        if (maxMoves == 0) {
            return game;
        }
        if (gameService.currentSide(game) != startingSide) {
            if (gameService.getGameStatus(game) == GameStatus.STALEMATE) {
                return game;
            }
            Set<Game> gamesWithForcedMate = new HashSet<>();
            for (Move potentialMove: gameService.getAvailableMoves(game)) {
                Game checkmatesForMove = findForcedCheckmateFromDetached(
                    gameService.applyMove(detachGameState(game), potentialMove),
                    startingSide,
                    maxMoves
                );
                if (checkmatesForMove.getAttemptedMoves().isEmpty()) {
                    return game;
                }
                gamesWithForcedMate.add(checkmatesForMove);
            }
            for (Game gameWithForcedMate:gamesWithForcedMate) {
                mergeDescendents(game, gameWithForcedMate.goToParentMove());
            }
            return game;
        }
        Set<Move> checkmatingMoves = findCheckmateInOne(game);
        if (!checkmatingMoves.isEmpty()) {
            for (Move checkmatingMove:checkmatingMoves) {
                gameService.applyMove(game, checkmatingMove)
                    .goToParentMove();
            }
        }
        if (maxMoves == 1) {
            return game;
        }
        for (Move potentialMove: gameService.getAvailableMoves(game)) {
            if (checkmatingMoves.contains(potentialMove)) {
                continue;
            }
            Game gameWithCheckmate = findForcedCheckmateFromDetached(
                gameService.applyMove(detachGameState(game), potentialMove),
                startingSide,
                maxMoves - 1
            );
            if (!gameWithCheckmate.getAttemptedMoves().isEmpty()) {
                mergeDescendents(game, gameWithCheckmate.goToParentMove());
            }
        }
        return game;
    }

    public MoveNode findForcedCheckmate(Game game, int maxTurns) {
        return findForcedCheckmateFromDetached(detachGameState(game), gameService.currentSide(game), maxTurns)
            .getMoveSummary()
            .getFirstAncestor();
    }
}
