package org.dexenjaeger.chess.models.game;


import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.services.BoardService;
import org.dexenjaeger.chess.utils.HashablePrintableTreeNode;

class MoveLine {
    private final BoardService service;
    private HashablePrintableTreeNode<GameSnapshot> tail;

    MoveLine(BoardService service, HashablePrintableTreeNode<GameSnapshot> tail) {
        this.service = service;
        this.tail = tail;
    }

    private HashablePrintableTreeNode<GameSnapshot> getMoveResult(Move move) {
        GameSnapshot previousMove = tail.getValue();
        return tail.addChild(new GameSnapshot(
            move.getSide() == Side.WHITE ? tail.getValue().getTurnNumber() + 1 : tail.getValue().getTurnNumber(),
            move,
            service.applyMove(previousMove.getBoard(), move),
            0,
            null
        ));
    }

    public HashablePrintableTreeNode<GameSnapshot> applyMoves(Move... moves) {
        for (Move move:moves) {
            tail = getMoveResult(move);
        }
        return tail;
    }
}
