package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.pieces.PieceType.BISHOP;
import static org.dexenjaeger.chess.models.pieces.PieceType.KING;
import static org.dexenjaeger.chess.models.pieces.PieceType.KNIGHT;
import static org.dexenjaeger.chess.models.pieces.PieceType.QUEEN;
import static org.dexenjaeger.chess.models.pieces.PieceType.ROOK;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SinglePieceMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.services.moves.CheckAvailability;
import org.dexenjaeger.chess.services.moves.DirectionalMoveExtractor;
import org.dexenjaeger.chess.services.moves.EvaluateOccupyingSide;
import org.dexenjaeger.chess.services.moves.FixedMoveExtractor;
import org.dexenjaeger.chess.services.moves.MoveExtractor;
import org.dexenjaeger.chess.services.moves.PawnMoveExtractor;
import org.dexenjaeger.chess.utils.Pair;

public class PieceService {
    private static final List<Pair<Integer, Integer>> BISHOP_DIRECTIONS = List.of(
        new Pair<>(-1, -1),
        new Pair<>(-1, 1),
        new Pair<>(1, -1),
        new Pair<>(1, 1)
    );
    private static final List<Pair<Integer, Integer>> ROOK_DIRECTIONS = List.of(
        new Pair<>(-1, 0),
        new Pair<>(0, -1),
        new Pair<>(0, 1),
        new Pair<>(1, 0)
    );
    private static List<Pair<Integer, Integer>> queenDirections() {
        return Stream.concat(
            BISHOP_DIRECTIONS.stream(),
            ROOK_DIRECTIONS.stream()
        ).collect(Collectors.toList());
    }

    private MoveExtractor getMoveExtractor(Piece piece, EvaluateOccupyingSide evaluateOccupyingSide) {
        CheckAvailability checkAvailability = new CheckAvailability(evaluateOccupyingSide, piece.getSide());
        switch (piece.getType()) {
            case PAWN:
                return new PawnMoveExtractor(
                    piece.getSide(), evaluateOccupyingSide
                );
            case KNIGHT:
                return new FixedMoveExtractor(
                    piece.getSide(), KNIGHT,
                    List.of(
                        new Pair<>(-2, -1),
                        new Pair<>(-2, 1),
                        new Pair<>(-1, -2),
                        new Pair<>(-1, 2),
                        new Pair<>(2, -1),
                        new Pair<>(2, 1),
                        new Pair<>(1, -2),
                        new Pair<>(1, 2)
                    ), checkAvailability
                );
            case KING:
                return new FixedMoveExtractor(
                    piece.getSide(), KING,
                    queenDirections(), checkAvailability
                );
            case BISHOP:
                return new DirectionalMoveExtractor(
                    piece.getSide(),
                    BISHOP, BISHOP_DIRECTIONS,
                    checkAvailability
                );
            case ROOK:
                return new DirectionalMoveExtractor(
                    piece.getSide(),
                    ROOK, ROOK_DIRECTIONS,
                    checkAvailability
                );
            case QUEEN:
                return new DirectionalMoveExtractor(
                    piece.getSide(),
                    QUEEN, queenDirections(),
                    checkAvailability
                );
            default:
                throw new NotImplementedException(piece.getType());
        }
    }

    public Set<SinglePieceMove> getMoves(
        Piece piece,
        Square starting,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        return getMoveExtractor(piece, evaluateOccupyingSide).moveSet(starting);
    }

    public boolean isLegal(SinglePieceMove move, EvaluateOccupyingSide evaluateOccupyingSide) {
        return getMoveExtractor(move.getPiece(), evaluateOccupyingSide)
            .canMove(move.getFrom(), move.getTo());
    }
}
