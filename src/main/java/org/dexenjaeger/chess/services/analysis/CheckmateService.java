package org.dexenjaeger.chess.services.analysis;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.GameStatus;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.MoveNode;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.services.GameService;

public class CheckmateService {
    private final GameService gameService;

    @Inject
    public CheckmateService(GameService gameService) {
        this.gameService = gameService;
    }

    public Optional<Move> findCheckmateInOne(Game game) {
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
            gameService.applyMove(game, move);
            gameToMerge.goToAttemptedMove(move);
            mergeDescendents(game, gameToMerge);
            game.goToParentMove();
            gameToMerge.goToParentMove();
        }
    }

    private Stream<Game> detachedGamesStartingWithForcing(Game game) {
        return gameService.getAvailableMoves(game)
            .stream()
            .map(potentialMove -> gameService.applyMove(gameService.detachGameState(game), potentialMove))
            .sorted(Comparator.comparing(g -> gameService.getAvailableMoves(g).size()));
    }

    private Optional<Game> findForcedCheckmateFromDetached(Game game, Side startingSide, int maxMoves) {
        if (maxMoves == 0) {
            return Optional.empty();
        }
        if (gameService.currentSide(game) == startingSide) {
            Optional<Game> potentialMateInOne = findCheckmateInOne(game)
                .map(checkmatingMove -> gameService
                    .applyMove(game, checkmatingMove)
                    .goToParentMove());
            if (potentialMateInOne.isPresent()) {
                return potentialMateInOne;
            }
            return findDownstreamCheckmate(game, maxMoves);
        }
        return findDownstreamCheckmatesForOpponent(game, maxMoves);
    }

    private Optional<Game> findDownstreamCheckmatesForOpponent(Game game, int maxMoves) {
        if (gameService.getGameStatus(game) == GameStatus.STALEMATE) {
            return Optional.empty();
        }
        Set<Game> gamesWithForcedMate = new HashSet<>();
        for (Move potentialMove: gameService.getAvailableMoves(game)) {
            Optional<Game> potentialGameWithCheckmate = findForcedCheckmateFromDetached(
                gameService.applyMove(gameService.detachGameState(game), potentialMove),
                gameService.currentSide(game).other(),
                maxMoves
            );
            if (potentialGameWithCheckmate.isEmpty()) {
                return Optional.empty();
            }
            gamesWithForcedMate.add(potentialGameWithCheckmate.get());
        }

        for (Game gameWithForcedMate:gamesWithForcedMate) {
            mergeDescendents(game, gameWithForcedMate.goToParentMove());
        }
        return Optional.of(game);
    }

    private Optional<Game> findDownstreamCheckmate(Game game, int maxMoves) {
        return detachedGamesStartingWithForcing(game)
            .flatMap(potentialGame -> findForcedCheckmateFromDetached(
                potentialGame, gameService.currentSide(game), maxMoves - 1
            ).stream())
            .findFirst();
    }

    public Optional<MoveNode> findForcedCheckmate(Game game, int maxTurns) {
        return findForcedCheckmateFromDetached(
            gameService.detachGameState(game),
            gameService.currentSide(game), maxTurns
        )
            .map(Game::getMoveSummary)
            .map(MoveNode::getFirstAncestor);
    }
}
