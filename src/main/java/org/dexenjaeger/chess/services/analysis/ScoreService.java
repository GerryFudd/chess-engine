package org.dexenjaeger.chess.services.analysis;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.config.BindingTag;
import org.dexenjaeger.chess.config.BindingTags;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.services.BoardService;

public class ScoreService {
    private final BoardService boardService;
    private final BigDecimal piecesWeight;
    private final BigDecimal activityWeight;

    @Inject
    public ScoreService(BoardService boardService,
        @BindingTag(BindingTags.PIECES_WEIGHT) BigDecimal piecesWeight,
        @BindingTag(BindingTags.ACTIVITY_WEIGHT) BigDecimal activityWeight) {
        this.boardService = boardService;
        this.piecesWeight = piecesWeight;
        this.activityWeight = activityWeight;
    }

    private int getMaterialScore(Board board, Side side) {
        return board.getBySide(side)
            .stream()
            .map(board::getPiece)
            .flatMap(Optional::stream)
            .map(Piece::getType)
            .mapToInt(PieceType::getValue)
            .sum();
    }

    int getRelativeMaterialScore(Board board) {
        return getMaterialScore(board, Side.WHITE) - getMaterialScore(board, Side.BLACK);
    }
    private int getPieceActivityScore(Set<Move> moves) {
        return moves.size();
    }
    int getRelativePieceActivityScore(Board board) {
        return getRelativePieceActivityScore(
            boardService.getMovesBySide(board, Side.WHITE),
            boardService.getMovesBySide(board, Side.BLACK)
        );
    }

    int getRelativePieceActivityScore(Set<Move> whiteMoves, Set<Move> blackMoves) {
        return getPieceActivityScore(whiteMoves) - getPieceActivityScore(blackMoves);
    }

    BigDecimal getWeightedScore(Board board) {
        Set<Move> whiteMoves = boardService.getMovesBySide(board, Side.WHITE);
        Set<Move> blackMoves = boardService.getMovesBySide(board, Side.BLACK);

        return piecesWeight
            .multiply(BigDecimal.valueOf(getRelativePieceActivityScore(whiteMoves, blackMoves)))
            .add(activityWeight.multiply(BigDecimal.valueOf(getRelativeMaterialScore(board))));
    }
}
