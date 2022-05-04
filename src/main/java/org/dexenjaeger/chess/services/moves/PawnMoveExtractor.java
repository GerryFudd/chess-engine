package org.dexenjaeger.chess.services.moves;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Move;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.board.Square;

public class PawnMoveExtractor implements MoveExtractor {
    private final Side side;
    private final Square starting;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public PawnMoveExtractor(
        Side side,
        Square starting,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        this.side = side;
        this.starting = starting;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }

    private Optional<Square> nextForward(Rank rank, int direction) {
        Optional<Rank> nextRank = rank.shift(direction);
        if (
            nextRank.isPresent()
                && evaluateOccupyingSide.getOccupyingSide(
                new Square(starting.getFile(), nextRank.get())
            ).isEmpty()
        ) {
            return Optional.of(new Square(starting.getFile(), nextRank.get()));
        }
        return Optional.empty();
    }

    private Set<Move> forwardMoves(int direction) {
        Set<Move> moves = new HashSet<>();
        nextForward(starting.getRank(), direction)
            .flatMap(square -> {
                moves.add(new Move(starting, square));
                return nextForward(square.getRank(), direction);
            })
            .map(sq -> new Move(starting, sq))
            .ifPresent(moves::add);
        return moves;
    }

    private Set<Move> capturingMoves(int direction) {
        Set<Move> moves = new HashSet<>();
        starting.getRank()
            .shift(direction)
            .ifPresent(rank -> {
                starting.getFile().shift(-1).map(f -> new Square(f, rank))
                    .filter(sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s != side).isPresent())
                    .map(sq -> new Move(starting, sq))
                    .ifPresent(moves::add);
                starting.getFile().shift(1).map(f -> new Square(f, rank))
                    .filter(sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s != side).isPresent())
                    .map(sq -> new Move(starting, sq))
                    .ifPresent(moves::add);
            });
        return moves;
    }

    @Override
    public Set<Move> moveSet() {
        int direction = side == Side.WHITE ? 1 : -1;
        Set<Move> moves = forwardMoves(direction);
        moves.addAll(capturingMoves(direction));
        return moves;
    }
}
