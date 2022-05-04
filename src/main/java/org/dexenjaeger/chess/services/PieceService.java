package org.dexenjaeger.chess.services;

import java.util.Set;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.services.moves.EvaluateOccupyingSide;
import org.dexenjaeger.chess.services.moves.KnightMoveExtractor;
import org.dexenjaeger.chess.services.moves.PawnMoveExtractor;
import org.dexenjaeger.chess.utils.Pair;

public class PieceService {

    public Set<Pair<File, Rank>> getMoves(
        Piece piece,
        Pair<File, Rank> starting,
        EvaluateOccupyingSide evaluateOccupyingSide
    ) {
        switch (piece.getType()) {
            case PAWN:
                return new PawnMoveExtractor(
                    piece.getSide(), starting, evaluateOccupyingSide
                ).moveSet();
            case KNIGHT:
                return new KnightMoveExtractor(
                    piece.getSide(), starting, evaluateOccupyingSide
                ).moveSet();
            default:
                throw new ServiceException(String.format(
                    "Not implemented for piece type %s",
                    piece.getType()
                ));
        }
    }
}
