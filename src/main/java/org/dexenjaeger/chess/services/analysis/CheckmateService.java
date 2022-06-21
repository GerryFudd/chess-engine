package org.dexenjaeger.chess.services.analysis;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.GameStatus;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.GameSnapshot;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.services.GameService;
import org.dexenjaeger.chess.utils.TreeNode;

public class CheckmateService {
    private final GameService gameService;

    @Inject
    public CheckmateService(GameService gameService) {
        this.gameService = gameService;
    }

    public Optional<Move> findCheckmateInOne(Game game, Side targetSide) {
        if (gameService.currentSide(game) != targetSide) {
            return Optional.empty();
        }
        return gameService.getAvailableMoves(game)
            .stream()
            .filter(move -> {
                Game potentialGame = gameService.applyMove(gameService.detachGameState(game), move);
                return gameService.getGameStatus(potentialGame).isCheckmate();
            })
            .findAny();
    }

    private void mergeDescendents(Game game, Game gameToMerge) {
        if (!game.getCurrentBoard().equals(gameToMerge.getCurrentBoard())) {
            throw new RuntimeException(String.format(
                "Game board %s doesn't match the game board to merge %s.",
                game.getCurrentBoard(), gameToMerge.getCurrentBoard()
            ));
        }
        for (Move move:gameToMerge.getAttemptedMoves()) {
            if (!game.getAttemptedMoves().contains(move)) {
                gameService.applyMove(game, move);
            } else {
                game.goToAttemptedMove(move);
            }
            gameToMerge.goToAttemptedMove(move);
            mergeDescendents(game, gameToMerge);
            game.goToParentMove();
            gameToMerge.goToParentMove();
        }
    }

    private Optional<Game> findForcedCheckmateFromDetached(Game gameArg, Side startingSide, int maxMoves) {
        Game game = gameService.detachGameState(gameArg);
        if (maxMoves == 0) {
            return Optional.empty();
        }
        return findCheckmateInOne(game, startingSide)
            .map(checkmatingMove -> gameService
                .applyMove(game, checkmatingMove)
                .goToParentMove())
            .or(() -> findDownstreamCheckmate(game, startingSide, maxMoves));
    }

    private List<Game> detachedGamesStartingWithForcing(Game game) {
        return gameService.getAvailableMoves(game)
            .stream()
            .map(potentialMove -> gameService.applyMove(gameService.detachGameState(game), potentialMove))
            .sorted(Comparator.comparing(g -> gameService.getAvailableMoves(g).size()))
            .collect(Collectors.toList());
    }

    private Optional<Game> findDownstreamCheckmate(Game game, Side startingSide, int maxMoves) {
        final AtomicInteger maxRemainingMoves = new AtomicInteger(maxMoves);
        if (gameService.getGameStatus(game) == GameStatus.STALEMATE) {
            return Optional.empty();
        }
        boolean isStartingSide = gameService.currentSide(game) == startingSide;
        if (isStartingSide) {
            maxRemainingMoves.decrementAndGet();
        }
        Optional<Game> result = Optional.empty();
        for (Game potentialGame: detachedGamesStartingWithForcing(game)) {
            if (maxRemainingMoves.get() == 0) {
                return result;
            }
            Optional<Game> possibleCheckmateLine = findForcedCheckmateFromDetached(
               potentialGame, startingSide, maxRemainingMoves.get()
            );
            if (!isStartingSide && possibleCheckmateLine.isEmpty()) {
                return Optional.empty();
            }
            result = possibleCheckmateLine.map(checkmateLine -> {
                if (isStartingSide) {
                    maxRemainingMoves.decrementAndGet();
                }
                mergeDescendents(potentialGame, checkmateLine);
                mergeDescendents(game, potentialGame.goToFirstMove());
                return Optional.of(game);
            }).orElse(result);
        }
        return result;
    }

    public Optional<TreeNode<GameSnapshot>> findForcedCheckmate(Game game, int maxTurns) {
        return findForcedCheckmateFromDetached(
            game, gameService.currentSide(game), maxTurns
        )
            .map(Game::getGameNode)
            .map(TreeNode::getFirstAncestor);
    }
}
