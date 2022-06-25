package org.dexenjaeger.chess.services.analysis;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.GameStatus;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.analysis.AnalysisParameters;
import org.dexenjaeger.chess.models.analysis.ResultHolder;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.GameSnapshot;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.services.BoardService;
import org.dexenjaeger.chess.services.GameService;
import org.dexenjaeger.chess.services.ThreadService;
import org.dexenjaeger.chess.utils.TreeNode;

@Slf4j
public class CheckmateService {
    private final GameService gameService;
    private final ThreadService threadService;
    private final ScoreService scoreService;
    private final BoardService boardService;

    @Inject
    public CheckmateService(GameService gameService, ThreadService threadService, ScoreService scoreService, BoardService boardService) {
        this.gameService = gameService;
        this.threadService = threadService;
        this.scoreService = scoreService;
        this.boardService = boardService;
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

    private Game mergeDescendents(Game game, Game gameToMerge) {
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
        return game;
    }

    private int distanceToOpposingKing(Game g) {
        Square opposingKingSquare = g.getCurrentBoard()
            .getBySideAndType(gameService.currentSide(g), PieceType.KING)
            .stream().findAny()
            .orElseThrow();

        return boardService.getTargetSquares(g.getPreviousMove())
            .stream()
            .mapToInt(s -> boardService.distance(s, opposingKingSquare))
            .min()
            .orElse(Integer.MAX_VALUE);
    }

    private int gameComparator(Game g1, Game g2) {
        boolean isCheck1 = gameService.isInCheck(g1);
        boolean isCheck2 = gameService.isInCheck(g2);
        if (isCheck1 && !isCheck2) {
            return 1;
        }
        if (isCheck2 && !isCheck1) {
            return -1;
        }

        int d1 = distanceToOpposingKing(g1);
        int d2 = distanceToOpposingKing(g2);
        if (d1 > d2) {
            return 1;
        }
        if (d1 < d2) {
            return -1;
        }
        return scoreService.getWeightedScore(g1.getCurrentBoard()).compareTo(scoreService.getWeightedScore(g2.getCurrentBoard()));
    }

    private List<Game> detachedGamesStartingWithForcing(Game game) {
        Comparator<Game> comparator = this::gameComparator;
        if (gameService.currentSide(game) == Side.WHITE) {
            comparator = comparator.reversed();
        }

        return gameService.getAvailableMoves(game)
            .stream()
            .map(potentialMove -> gameService.applyMove(gameService.detachGameState(game), potentialMove))
            .sorted(comparator)
            .collect(Collectors.toList());
    }

    private boolean isLast(AtomicInteger counter) {
        return counter.decrementAndGet() <= 0;
    }

    private boolean isCancelled(AnalysisParameters parameters) {
        return parameters.getMaxTurns().get() <= parameters.getIterationNumber();
    }

    private Game getResult(Game original, Game candidate, boolean isStartingSide) {
        if (isStartingSide) {
            return mergeDescendents(gameService.detachGameState(original), candidate);
        }
        return mergeDescendents(original, candidate);
    }

    @SneakyThrows
    private Optional<Game> findDownstreamCheckmate(AnalysisParameters parameters) {
        if (isCancelled(parameters)) {
            return Optional.empty();
        }
        if (gameService.getGameStatus(parameters.getGame()) == GameStatus.STALEMATE) {
            return Optional.empty();
        }
        boolean isStartingSide = gameService.currentSide(parameters.getGame()) == parameters.getStartingSide();
        final int nextIterationNumber;
        if (isStartingSide) {
            nextIterationNumber = parameters.getIterationNumber() + 1;
        } else {
            nextIterationNumber = parameters.getIterationNumber();
        }
        ResultHolder<Game> resultHolder = new ResultHolder<>();
        CompletableFuture<Optional<Game>> futureResult = new CompletableFuture<>();
        AtomicInteger linesRunningInParallel = new AtomicInteger();
        for (Game potentialGame: detachedGamesStartingWithForcing(parameters.getGame())) {
            if (parameters.getIterationNumber() == 0) {
                linesRunningInParallel.incrementAndGet();
                threadService.run(() -> {
                    log.info("Exploring variations starting from {}", potentialGame.getPreviousMove());
                    if (isCancelled(parameters)) {
                        linesRunningInParallel.decrementAndGet();
                        futureResult.complete(resultHolder.get());
                        return;
                    }
                    Optional<Game> possibleCheckmateLine = findForcedCheckmateAndMerge(
                        new AnalysisParameters(
                            potentialGame, parameters.getStartingSide(), parameters.getMaxTurns(), nextIterationNumber
                        )
                    );
                    if (possibleCheckmateLine.isPresent()) {
                        int moveCount = gameService.countMainlineMoves(possibleCheckmateLine.get());
                        if (resultHolder.set(
                            () -> getResult(parameters.getGame(), possibleCheckmateLine.get(), isStartingSide),
                            -1 * moveCount, !isStartingSide
                        ) && isStartingSide) {
                            if (moveCount + parameters.getIterationNumber() < parameters.getMaxTurns().get()) {
                                parameters.getMaxTurns().set(moveCount + parameters.getIterationNumber());
                                log.info("Found better line {}. Max turns are now {}.", possibleCheckmateLine.get(), parameters.getMaxTurns().get());
                            } else if (moveCount + parameters.getIterationNumber() == parameters.getMaxTurns().get()) {
                                log.info("Found line {}.", possibleCheckmateLine.get());
                            }
                        }
                    } else if (!isStartingSide) {
                        futureResult.complete(Optional.empty());
                    }
                    if (isLast(linesRunningInParallel)) {
                        futureResult.complete(resultHolder.get());
                    }
                });
                continue;
            }
            if (isCancelled(parameters)) {
                return resultHolder.get();
            }
            Optional<Game> possibleCheckmateLine = findForcedCheckmateAndMerge(
                new AnalysisParameters(
                    potentialGame, parameters.getStartingSide(), parameters.getMaxTurns(), nextIterationNumber
                )
            );
            if (possibleCheckmateLine.isPresent()) {
                int moveCount = gameService.countMainlineMoves(possibleCheckmateLine.get());
                if (resultHolder.set(
                    () -> getResult(parameters.getGame(), possibleCheckmateLine.get(), isStartingSide),
                    -1 * moveCount, !isStartingSide
                ) && isStartingSide && moveCount + parameters.getIterationNumber() < parameters.getMaxTurns().get()) {
                    parameters.getMaxTurns().set(moveCount + parameters.getIterationNumber());
                }
            } else if (!isStartingSide) {
                return Optional.empty();
            }
        }
        if (parameters.getIterationNumber() == 0) {
            return futureResult.get();
        }
        return resultHolder.get();
    }

    private Optional<Game> findForcedCheckmateFromDetached(AnalysisParameters parameters) {
        if (isCancelled(parameters)) {
            return Optional.empty();
        }
        Game detachedGame = gameService.detachGameState(parameters.getGame());
        return findCheckmateInOne(detachedGame, parameters.getStartingSide())
            .map(checkmatingMove -> gameService
                .applyMove(detachedGame, checkmatingMove)
                .goToParentMove())
            .or(() -> findDownstreamCheckmate(new AnalysisParameters(
                detachedGame, parameters.getStartingSide(), parameters.getMaxTurns(), parameters.getIterationNumber()
            )));
    }

    private Optional<Game> findForcedCheckmateAndMerge(AnalysisParameters parameters) {
        return findForcedCheckmateFromDetached(parameters)
            .map(checkmateLine -> mergeDescendents(parameters.getGame(), checkmateLine).goToFirstMove());
    }

    public Optional<TreeNode<GameSnapshot>> findForcedCheckmate(Game game, int maxTurns) {
        return findForcedCheckmateAndMerge(new AnalysisParameters(
            game, gameService.currentSide(game), new AtomicInteger(maxTurns), 0
        )).map(Game::getGameNode);
    }
}
