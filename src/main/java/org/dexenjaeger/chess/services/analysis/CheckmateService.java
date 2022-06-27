package org.dexenjaeger.chess.services.analysis;

import java.util.Comparator;
import java.util.LinkedList;
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
import org.dexenjaeger.chess.models.analysis.IterationResult;
import org.dexenjaeger.chess.models.analysis.IterationStatus;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.GameSnapshot;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.services.BoardService;
import org.dexenjaeger.chess.services.GameService;
import org.dexenjaeger.chess.services.ThreadService;
import org.dexenjaeger.chess.utils.HashablePrintableTreeNode;
import org.dexenjaeger.chess.utils.SimpleTreeNode;

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
        int factor = gameService.currentSide(g1) == Side.WHITE ? -1 : 1;

        boolean isCheck1 = gameService.isInCheck(g1);
        boolean isCheck2 = gameService.isInCheck(g2);
        if (isCheck1 && !isCheck2) {
            return factor;
        }
        if (isCheck2 && !isCheck1) {
            return factor * -1;
        }

        int d1 = distanceToOpposingKing(g1);
        int d2 = distanceToOpposingKing(g2);
        if (d1 > d2) {
            return factor;
        }
        if (d1 < d2) {
            return factor * -1;
        }
        return factor * scoreService.getWeightedScore(g1.getCurrentBoard()).compareTo(scoreService.getWeightedScore(g2.getCurrentBoard()));
    }

    private boolean isLast(AtomicInteger counter) {
        return counter.decrementAndGet() <= 0;
    }

    private Game getGame(IterationResult nodeValue) {
        return Game.init(
            nodeValue.getTurnNumber() + 1,
            nodeValue.getMove().getSide().other(),
            nodeValue.getBoard(),
            nodeValue.getFiftyMoveRuleCounter()
        );
    }

    private Game getGame(SimpleTreeNode<IterationResult> node) {
        SimpleTreeNode<IterationResult> cursor = node.getFirstAncestor();
        Game result = getGame(cursor.getValue());
        while (cursor.getParent().isPresent() || cursor.getChildren().size() > result.getAttemptedMoves().size()) {
            if (cursor.getChildren().size() == result.getAttemptedMoves().size()) {
                cursor = cursor.getParent().get();
                result.goToParentMove();
            } else {
                cursor = cursor.getChildren().get(result.getAttemptedMoves().size());
                result = gameService.applyMove(result, cursor.getValue().getMove());
            }
        }
        return result.goToFirstMove();
    }

    private SimpleTreeNode<IterationResult> findCheckmateForNextTurn(SimpleTreeNode<IterationResult> iterationNode) {
        if (
            iterationNode.getValue().getStatus() != IterationStatus.UNEXPLORED
                || !iterationNode.getChildren().isEmpty()
        ) {
            throw new RuntimeException(String.format(
                "This method is only applicable to leaf nodes for %s, instead got %s",
                IterationStatus.UNEXPLORED,
                iterationNode
            ));
        }
        int newTurn = iterationNode.getValue().getTurns() + 1;
        if (iterationNode.getValue().getMaxTurns().get() < newTurn) {
            return iterationNode.replaceNode(new SimpleTreeNode<>(iterationNode.getValue().copyToStatus(IterationStatus.FAILURE)));
        }
        SimpleTreeNode<IterationResult> result = new SimpleTreeNode<>(iterationNode.getValue().copyToStatus(IterationStatus.DEFERRED));
        List<Game> candidateGames = gameService.getAvailableMoves(getGame(result.getValue()))
            .stream()
            .map(potentialMove -> gameService.applyMove(getGame(result.getValue()), potentialMove))
            .sorted(this::gameComparator)
            .collect(Collectors.toList());
        List<Game> gamesToRemove = new LinkedList<>();
        for (Game candidateGame: candidateGames) {
            GameStatus gameStatus = gameService.getGameStatus(candidateGame);
            if (gameStatus.isCheckmate()) {
                SimpleTreeNode<IterationResult> successResult = new SimpleTreeNode<>(result.getValue().copyToStatus(IterationStatus.SUCCESS));
                successResult.addChild(result.getValue().copyFromGame(
                    IterationStatus.SUCCESS,
                    newTurn,
                    candidateGame
                ));
                return iterationNode.replaceNode(successResult);
            }
            if (gameStatus == GameStatus.STALEMATE) {
                gamesToRemove.add(candidateGame);
            }
        }
        candidateGames.removeAll(gamesToRemove);
        for (Game candidateGame: candidateGames) {
            List<Game> candidateDeferredGames = gameService.getAvailableMoves(candidateGame)
                .stream()
                .map(m -> gameService.applyMove(gameService.copy(candidateGame), m))
                .collect(Collectors.toList());
            if (candidateDeferredGames.stream()
                .map(gameService::getGameStatus)
                .allMatch(status -> !status.isCheckmate() && status != GameStatus.STALEMATE)
                && result.getValue().getMaxTurns().get() > newTurn
            ) {
                SimpleTreeNode<IterationResult> candidateOpponentResult = result.addChild(result.getValue().copyFromGame(
                    IterationStatus.UNEXPLORED_OPPONENT, newTurn, candidateGame
                ));
                for (Game candidateOpponentGame:candidateDeferredGames) {
                    candidateOpponentResult.addChild(result.getValue().copyFromGame(
                        IterationStatus.UNEXPLORED, newTurn, candidateOpponentGame
                    ));
                }
            }
        }
        if (result.getChildren().isEmpty()) {
            return iterationNode.replaceNode(new SimpleTreeNode<>(result.getValue().copyToStatus(IterationStatus.FAILURE)));
        }
        result.getChildren().sort(Comparator.comparing(c -> c.getChildren().size()));
        return iterationNode.replaceNode(result);
    }

    @SneakyThrows
    private SimpleTreeNode<IterationResult> findNextTurnCheckmateFomUnexploredOpponent(
        SimpleTreeNode<IterationResult> sourceNode
    ) {
        if (
            sourceNode.getValue().getStatus() != IterationStatus.UNEXPLORED_OPPONENT
        ) {
            throw new RuntimeException(String.format(
                "This method is only implemented for %s type iterations. Instead got %s",
                IterationStatus.UNEXPLORED_OPPONENT.name(), sourceNode
            ));
        }
        if (sourceNode.getChildren().isEmpty()) {
            return sourceNode.replaceNode(new SimpleTreeNode<>(sourceNode.getValue().copyToStatus(IterationStatus.FAILURE)));
        }
        CompletableFuture<Void> allRun = new CompletableFuture<>();
        AtomicInteger threadsRunning = new AtomicInteger();
        SimpleTreeNode<IterationResult> result = new SimpleTreeNode<>(sourceNode.getValue().copyToStatus(IterationStatus.DEFERRED_OPPONENT));

        List<SimpleTreeNode<IterationResult>> resultsToCheck = new LinkedList<>();
        for (SimpleTreeNode<IterationResult> child:sourceNode.getChildren()) {
            if (child.getValue().getStatus() != IterationStatus.UNEXPLORED) {
                throw new RuntimeException(String.format(
                    "This child should be unexplored. Instead got %s",
                    child
                ));
            }
            resultsToCheck.add(new SimpleTreeNode<>(child.getValue()));
        }

        List<SimpleTreeNode<IterationResult>> newChildren = new LinkedList<>();
        if (resultsToCheck.isEmpty()) {
            throw new RuntimeException("K");
        }
        for (SimpleTreeNode<IterationResult> resultToCheck:resultsToCheck) {
            threadsRunning.incrementAndGet();
            threadService.run(() -> {
                newChildren.add(findCheckmateForNextTurn(resultToCheck));
                if (isLast(threadsRunning)) {
                    allRun.complete(null);
                }
            });
        }
        allRun.get();
        if (newChildren.isEmpty()) {
            throw new RuntimeException("K");
        }
        try {
            newChildren.sort(Comparator.comparing(c -> c.getChildren().size()));
        } catch (RuntimeException e) {
            log.info("wtf");
        }
        Optional<SimpleTreeNode<IterationResult>> failureResult = newChildren.stream()
            .filter(c -> c.getValue().getStatus() == IterationStatus.FAILURE)
            .findAny();
        if (failureResult.isPresent()) {
            return sourceNode.replaceNode(failureResult.get());
        }

        if (newChildren.stream().map(SimpleTreeNode::getValue).map(IterationResult::getStatus).allMatch(s -> s == IterationStatus.SUCCESS)) {
            SimpleTreeNode<IterationResult> successResult = newChildren.stream().min(Comparator.comparing(n -> n.getValue().getTurns())).orElseThrow();
            result = new SimpleTreeNode<>(sourceNode.getValue().copyToStatus(IterationStatus.SUCCESS));
            result.addBranch(successResult);
            return sourceNode.replaceNode(result);
        }
        newChildren.forEach(result::addBranch);
        SimpleTreeNode<IterationResult> mergedValue = sourceNode.replaceNode(result);
        mergedValue.getParent().orElseThrow().getChildren().sort(Comparator.comparing(c -> c.getChildren().size()));
        return mergedValue;
    }

    private Optional<SimpleTreeNode<IterationResult>> findNextUnexploredNode(SimpleTreeNode<IterationResult> iterationNode, AtomicInteger firstUnexploredTurn) {
        SimpleTreeNode<IterationResult> cursor = iterationNode.getFirstAncestor();
        int loopCount = 0;
        while (loopCount < 100) {
            loopCount += 1;
            if (firstUnexploredTurn.get() > cursor.getValue().getMaxTurns().get()) {
                return Optional.empty();
            }
            if (cursor.getValue().getTurns() > firstUnexploredTurn.get()) {
                cursor = cursor.getParent().orElseThrow();
                continue;
            }
            if (!cursor.getValue().getStatus().isExplored() && !cursor.getValue().getStatus().isComplete()) {
                return Optional.of(cursor);
            }
            if (cursor.getValue().getStatus().isComplete()) {
                if (cursor.getParent().isEmpty()) {
                    return Optional.of(cursor).filter(c -> c.getValue().getStatus() == IterationStatus.SUCCESS);
                }
                if (
                    (
                        cursor.getValue().getStatus() == IterationStatus.SUCCESS
                        && cursor.getParent().get().getValue().getStatus() == IterationStatus.DEFERRED
                    )
                    || (
                        cursor.getValue().getStatus() == IterationStatus.FAILURE
                        && cursor.getParent().get().getValue().getStatus() == IterationStatus.DEFERRED_OPPONENT
                    )
                ) {
                    return Optional.of(cursor);
                }
                if (cursor.getNextSibling().isPresent()) {
                    cursor = cursor.getNextSibling().get();
                    continue;
                }
                if (cursor.getParent().get().getChildren().stream().allMatch(c -> c.getValue().getStatus().isComplete())) {
                    return Optional.of(cursor);
                }
                if (cursor.getParent().flatMap(SimpleTreeNode::getNextSibling).isPresent()) {
                    cursor = cursor.getParent().flatMap(SimpleTreeNode::getNextSibling).get();
                    continue;
                }
                break;
            }

            if (cursor.getValue().getTurns() == firstUnexploredTurn.get()) {
                if (cursor.getNextSibling().isPresent()) {
                    cursor = cursor.getNextSibling().get();
                    continue;
                }
                log.info("Starting to look at move number {}", firstUnexploredTurn.incrementAndGet());
                continue;
            }
            cursor = cursor.getChildren().getFirst();
        }
        throw new RuntimeException(String.format("What happened? %s", cursor));
    }

    private SimpleTreeNode<IterationResult> resolveParent(SimpleTreeNode<IterationResult> completed) {
        if (completed.getValue().getStatus() == IterationStatus.SUCCESS) {
            log.info("Found success {}", completed);
        }
        SimpleTreeNode<IterationResult> parent = completed.getParent().filter(p -> p.getValue().getStatus().isExplored()).orElseThrow();
        IterationStatus anyOneStatus = parent.getValue().getStatus() == IterationStatus.DEFERRED
            ? IterationStatus.SUCCESS
            : IterationStatus.FAILURE;
        IterationStatus allStatus = anyOneStatus == IterationStatus.SUCCESS
            ? IterationStatus.FAILURE
            : IterationStatus.SUCCESS;
        boolean anyIncomplete = false;
        SimpleTreeNode<IterationResult> allStatusReplacement = new SimpleTreeNode<>(parent.getValue().copyToStatus(allStatus));
        for (SimpleTreeNode<IterationResult> child:parent.getChildren()) {
            if (child.getValue().getStatus() == anyOneStatus) {
                SimpleTreeNode<IterationResult> replacement = new SimpleTreeNode<>(parent.getValue().copyToStatus(anyOneStatus));
                if (anyOneStatus == IterationStatus.SUCCESS) {
                    replacement.addBranch(child);
                }
                log.info("Resolved parent {}", replacement);
                return parent.replaceNode(replacement);
            }
            if (!child.getValue().getStatus().isComplete()) {
                anyIncomplete = true;
            }
            if (!anyIncomplete && allStatus == IterationStatus.SUCCESS) {
                allStatusReplacement.addBranch(child);
            }
        }
        if (anyIncomplete) {
            return parent;
        }
        log.info("Resolved parent {}", allStatusReplacement);
        return parent.replaceNode(allStatusReplacement);
    }

    @SneakyThrows
    private Optional<Game> findForcedCheckmateUntilDone(SimpleTreeNode<IterationResult> iterationNode, AtomicInteger firstUnexploredTurn) {
        SimpleTreeNode<IterationResult> cursor = iterationNode;
        while (true) {
            if (cursor.getFirstAncestor().getValue().getStatus().isComplete()) {
                if (cursor.getFirstAncestor().getValue().getStatus() == IterationStatus.SUCCESS) {
                    return Optional.of(getGame(cursor));
                }
                return Optional.empty();
            }
            if (cursor.getValue().getStatus().isComplete()) {
                cursor = resolveParent(cursor);
                continue;
            }
            Optional<SimpleTreeNode<IterationResult>> r = findNextUnexploredNode(cursor, firstUnexploredTurn)
                .map(n -> {
                    if (n.getValue().getStatus() == IterationStatus.UNEXPLORED) {
                        return findCheckmateForNextTurn(n);
                    } else if (n.getValue().getStatus() == IterationStatus.UNEXPLORED_OPPONENT) {
                        return findNextTurnCheckmateFomUnexploredOpponent(n);
                    } else if (n.getValue().getStatus().isComplete()) {
                        return n;
                    }
                    throw new RuntimeException("This shouldn't happen");
                });
            if (r.isPresent()) {
                cursor = r.get();
                continue;
            }
            return Optional.empty();
        }
    }

    public Optional<HashablePrintableTreeNode<GameSnapshot>> findForcedCheckmate(Game game, int maxTurns) {
        return findForcedCheckmateUntilDone(new SimpleTreeNode<>(new IterationResult(
            IterationStatus.UNEXPLORED,
            new AtomicInteger(maxTurns),
            0,
            game.getGameNode().getValue().getFiftyMoveRuleCounter(),
            game.getGameNode().getValue().getTurnNumber(),
            game.getPreviousMove(),
            game.getCurrentBoard()
        )), new AtomicInteger()).map(Game::getGameNode);
    }
}
