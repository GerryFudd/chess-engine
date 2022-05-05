package org.dexenjaeger.chess.services.moves;

import static org.dexenjaeger.chess.models.pieces.PieceType.PAWN;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;

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

    private Optional<Square> nextForward(RankType rank, int direction) {
        Optional<RankType> nextRank = rank.shift(direction);
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

    private Set<SimpleMove> forwardMoves(int direction) {
        Set<SimpleMove> moves = new HashSet<>();
        nextForward(starting.getRank(), direction)
            .flatMap(square -> {
                moves.add(new SimpleMove(starting, square, PAWN, side));
                return nextForward(square.getRank(), direction);
            })
            .map(sq -> new SimpleMove(starting, sq, PAWN, side))
            .ifPresent(moves::add);
        return moves;
    }

    private Set<SimpleMove> capturingMoves(int direction) {
        Set<SimpleMove> moves = new HashSet<>();
        starting.getRank()
            .shift(direction)
            .ifPresent(rank -> {
                starting.getFile().shift(-1).map(f -> new Square(f, rank))
                    .filter(sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s != side).isPresent())
                    .map(sq -> new SimpleMove(starting, sq, PAWN, side))
                    .ifPresent(moves::add);
                starting.getFile().shift(1).map(f -> new Square(f, rank))
                    .filter(sq -> evaluateOccupyingSide.getOccupyingSide(sq).filter(s -> s != side).isPresent())
                    .map(sq -> new SimpleMove(starting, sq, PAWN, side))
                    .ifPresent(moves::add);
            });
        return moves;
    }

    @Override
    public Set<SimpleMove> moveSet() {
        int direction = side == Side.WHITE ? 1 : -1;
        Set<SimpleMove> moves = forwardMoves(direction);
        moves.addAll(capturingMoves(direction));
        return moves;
    }
}
