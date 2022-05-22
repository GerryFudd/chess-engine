package org.dexenjaeger.chess.services;

import java.util.Optional;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

public class AnalysisService {
    public int getMaterialScore(Board board, Side side) {
        return board.getBySide(side)
            .stream()
            .map(board::getPiece)
            .flatMap(Optional::stream)
            .map(Piece::getType)
            .mapToInt(PieceType::getValue)
            .sum();
    }

    public int getRelativeMaterialScore(Board board) {
        return getMaterialScore(board, Side.WHITE) - getMaterialScore(board, Side.BLACK);
    }
}
