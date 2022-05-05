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
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.services.moves.DirectionalMoveExtractor;
import org.dexenjaeger.chess.services.moves.EvaluateOccupyingSide;
import org.dexenjaeger.chess.services.moves.FixedMoveExtractor;
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

    public Set<SimpleMove> getMoves(
        Piece piece,
        Square starting,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        switch (piece.getType()) {
            case PAWN:
                return new PawnMoveExtractor(
                    piece.getSide(), starting, evaluateOccupyingSide
                ).moveSet();
            case KNIGHT:
                return new FixedMoveExtractor(
                    piece.getSide(), KNIGHT, starting,
                    List.of(
                        new Pair<>(-2, -1),
                        new Pair<>(-2, 1),
                        new Pair<>(-1, -2),
                        new Pair<>(-1, 2),
                        new Pair<>(2, -1),
                        new Pair<>(2, 1),
                        new Pair<>(1, -2),
                        new Pair<>(1, 2)
                    ), evaluateOccupyingSide
                ).moveSet();
            case KING:
                return new FixedMoveExtractor(
                    piece.getSide(), KING, starting,
                    queenDirections(), evaluateOccupyingSide
                ).moveSet();
            case BISHOP:
                return new DirectionalMoveExtractor(
                    piece.getSide(),
                    BISHOP, BISHOP_DIRECTIONS,
                    starting,
                    evaluateOccupyingSide
                ).moveSet();
            case ROOK:
                return new DirectionalMoveExtractor(
                    piece.getSide(),
                    ROOK, ROOK_DIRECTIONS,
                    starting,
                    evaluateOccupyingSide
                ).moveSet();
            case QUEEN:
                return new DirectionalMoveExtractor(
                    piece.getSide(),
                    QUEEN, queenDirections(),
                    starting,
                    evaluateOccupyingSide
                ).moveSet();
            default:
                throw new ServiceException(String.format(
                    "Not implemented for piece type %s",
                    piece.getType()
                ));
        }
    }
}
