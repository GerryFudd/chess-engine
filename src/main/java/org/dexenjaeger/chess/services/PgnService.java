package org.dexenjaeger.chess.services;

import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.PieceType;

public class PgnService {
    private final BoardService boardService;

    public PgnService(BoardService boardService) {
        this.boardService = boardService;
    }

    public String toPgnMove(Castle move) {
        return move.toString();
    }

    public String toPgnMove(SimpleMove move, Board board) {
        if (move.getType() == PieceType.PAWN) {
            if (move.getFrom().getFile() != move.getTo().getFile()) {
                return String.format(
                    "%sx%s",
                    move.getFrom().getFile(),
                    move.getTo()
                );
            }
            return move.getTo().toString();
        }

        StringBuilder result = new StringBuilder(move.getType().getRepresentation());
        boardService
            .getOtherPieceLocation(move, board)
            .ifPresent(sq -> {
                if (sq.getFile() != move.getFrom().getFile()) {
                    result.append(move.getFrom().getFile());
                }
                if (sq.getRank() != move.getFrom().getRank()) {
                    result.append(move.getFrom().getRank());
                }
            });

        return result.append(move.getTo()).toString();
    }
}
