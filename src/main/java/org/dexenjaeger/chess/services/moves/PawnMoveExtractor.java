package org.dexenjaeger.chess.services.moves;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.utils.Pair;

public class PawnMoveExtractor implements MoveExtractor {
    private final Side side;
    private final Pair<File, Rank> starting;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public PawnMoveExtractor(
        Side side,
        Pair<File, Rank> starting,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        this.side = side;
        this.starting = starting;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }

    private Optional<Pair<File, Rank>> nextForward(Rank rank, int direction) {
        Optional<Rank> nextRank = rank.shift(direction);
        if (
            nextRank.isPresent()
                && evaluateOccupyingSide.getOccupyingSide(
                new Pair<>(starting.getLeft(), nextRank.get())
            ).isEmpty()
        ) {
            return Optional.of(new Pair<>(starting.getLeft(), nextRank.get()));
        }
        return Optional.empty();
    }

    private Set<Pair<File, Rank>> forwardMoves(int direction) {
        Set<Pair<File, Rank>> moves = new HashSet<>();
        nextForward(starting.getRight(), direction)
            .flatMap(square -> {
                moves.add(square);
                return nextForward(square.getRight(), direction);
            })
            .ifPresent(moves::add);
        return moves;
    }

    private Set<Pair<File, Rank>> capturingMoves(int direction) {
        Set<Pair<File, Rank>> moves = new HashSet<>();
        starting.getRight()
            .shift(direction)
            .ifPresent(rank -> {
                starting.getLeft().shift(-1).map(f -> new Pair<>(f, rank))
                    .filter(sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s != side).isPresent())
                    .ifPresent(moves::add);
                starting.getLeft().shift(1).map(f -> new Pair<>(f, rank))
                    .filter(sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s != side).isPresent())
                    .ifPresent(moves::add);
            });
        return moves;
    }

    @Override
    public Set<Pair<File, Rank>> moveSet() {
        int direction = side == Side.WHITE ? 1 : -1;
        Set<Pair<File, Rank>> moves = forwardMoves(direction);
        moves.addAll(capturingMoves(direction));
        return moves;
    }
}
