package org.dexenjaeger.chess.services.moves;

import static org.dexenjaeger.chess.models.pieces.PieceType.PAWN;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;

public class PawnMoveExtractor implements MoveExtractor {
    private final Side side;
    private final EvaluateOccupyingSide evaluateOccupyingSide;

    public PawnMoveExtractor(
        Side side,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        this.side = side;
        this.evaluateOccupyingSide = evaluateOccupyingSide;
    }

    private int getDirection() {
        return side == Side.WHITE ? 1 : -1;
    }

    private Optional<Square> nextForward(Square square) {
        Optional<RankType> nextRank = square.getRank().shift(getDirection());
        if (
            nextRank.isPresent()
            && evaluateOccupyingSide.getOccupyingSide(new Square(square.getFile(), nextRank.get())).isEmpty()
        ) {
            return Optional.of(new Square(square.getFile(), nextRank.get()));
        }
        return Optional.empty();
    }

    private Set<Square> forwardSquares(Square starting) {
        Set<Square> squares = new HashSet<>();
        nextForward(starting)
            .flatMap(square -> {
                squares.add(square);
                return nextForward(square);
            })
            .ifPresent(squares::add);
        return squares;
    }

    private Set<SimpleMove> forwardMoves(Square starting) {
        return forwardSquares(starting).stream()
            .map(sq -> new SimpleMove(starting, sq, PAWN, side))
            .collect(Collectors.toSet());
    }

    private boolean isCapture(Square target) {
        return evaluateOccupyingSide.getOccupyingSide(target).filter(s -> s != side).isPresent();
    }

    private Set<SimpleMove> capturingMoves(Square starting) {
        Set<SimpleMove> moves = new HashSet<>();
        starting.getRank()
            .shift(getDirection())
            .ifPresent(rank -> {
                starting.getFile().shift(-1).map(f -> new Square(f, rank))
                    .filter(this::isCapture)
                    .map(sq -> new SimpleMove(starting, sq, PAWN, side))
                    .ifPresent(moves::add);
                starting.getFile().shift(1).map(f -> new Square(f, rank))
                    .filter(this::isCapture)
                    .map(sq -> new SimpleMove(starting, sq, PAWN, side))
                    .ifPresent(moves::add);
            });
        return moves;
    }

    @Override
    public Set<SimpleMove> moveSet(Square starting) {
        Set<SimpleMove> moves = forwardMoves(starting);
        moves.addAll(capturingMoves(starting));
        return moves;
    }

    @Override
    public boolean canMove(Square from, Square to) {
        if (from.getFile() == to.getFile()) {
            return forwardSquares(from).contains(to);
        }
        if (to.getRank().ordinal() != from.getRank().ordinal() + 1 || !isCapture(to)) {
            return false;
        }
        return Math.abs(from.getFile().ordinal() - to.getFile().ordinal()) == 1;
    }
}
