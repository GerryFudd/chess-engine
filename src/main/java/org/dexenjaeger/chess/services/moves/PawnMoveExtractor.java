package org.dexenjaeger.chess.services.moves;

import static org.dexenjaeger.chess.models.pieces.PieceType.BISHOP;
import static org.dexenjaeger.chess.models.pieces.PieceType.KNIGHT;
import static org.dexenjaeger.chess.models.pieces.PieceType.PAWN;
import static org.dexenjaeger.chess.models.pieces.PieceType.QUEEN;
import static org.dexenjaeger.chess.models.pieces.PieceType.ROOK;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.NormalMove;
import org.dexenjaeger.chess.models.moves.PromotionMove;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.Pair;

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
    private RankType startingRank() {
        return side == Side.WHITE ? RankType.TWO : RankType.SEVEN;
    }

    private RankType getPromotionRank() {
        return side == Side.WHITE ? RankType.EIGHT : RankType.ONE;
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

    private Set<Square> forwardSquares(Square squareFrom) {
        Set<Square> squares = new HashSet<>();
        nextForward(squareFrom)
            .flatMap(square -> {
                squares.add(square);
                // If the pawn is on the starting rank and the first square is clear, check the second.
                return squareFrom.getRank() == startingRank()
                    ? nextForward(square)
                    : Optional.empty();
            })
            .ifPresent(squares::add);
        return squares;
    }

    private Stream<PieceType> promotionCandidates() {
        return Stream.of(ROOK, KNIGHT, BISHOP, QUEEN);
    }

    private Set<NormalMove> forwardMoves(Square starting) {
        return forwardSquares(starting).stream()
            .flatMap(sq -> sq.getRank() == getPromotionRank()
                ? promotionCandidates().map(t -> new PromotionMove(side, sq.getFile(), t))
                : Stream.of(new SimpleMove(starting, sq, PAWN, side))
            )
            .collect(Collectors.toSet());
    }

    private boolean isCapture(Square target) {
        return evaluateOccupyingSide.getOccupyingSide(target).filter(s -> s != side).isPresent();
    }

    private Stream<NormalMove> capturingMovesForFileShift(Square starting, int fileShift) {
        return starting.shift(new Pair<>(fileShift, getDirection()))
            .filter(this::isCapture)
            .stream()
            .flatMap(sq -> sq.getRank() == getPromotionRank()
                ? promotionCandidates().map(t -> new PromotionMove(side, starting.getFile(), sq.getFile(), t))
                : Stream.of(new SimpleMove(starting, sq, PAWN, side))
            );
    }

    private Set<NormalMove> capturingMoves(Square starting) {
        return Stream.concat(
            capturingMovesForFileShift(starting, -1),
            capturingMovesForFileShift(starting, 1)
        ).collect(Collectors.toSet());
    }

    @Override
    public Set<NormalMove> moveSet(Square starting) {
        Set<NormalMove> moves = forwardMoves(starting);
        moves.addAll(capturingMoves(starting));
        return moves;
    }

    @Override
    public boolean canMove(Square from, Square to) {
        if (from.getFile() == to.getFile()) {
            return forwardSquares(from).contains(to);
        }
        if (to.getRank().ordinal() != from.getRank().ordinal() + getDirection() || !isCapture(to)) {
            return false;
        }
        return Math.abs(from.getFile().ordinal() - to.getFile().ordinal()) == 1;
    }
}
