package org.dexenjaeger.chess.models.game;


import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.services.BoardService;

class MoveLine {
    private final BoardService service;
    private MoveNode tail;

    MoveLine(BoardService service, MoveNode tail) {
        this.service = service;
        this.tail = tail;
    }

    private MoveNode getMoveResult(Move move) {
        return tail.addChild(move, service.applyMove(tail.getBoard(), move));
    }

    public MoveNode applyMoves(Move... moves) {
        for (Move move:moves) {
            tail = getMoveResult(move);
        }
        return tail;
    }
}
