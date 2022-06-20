package org.dexenjaeger.chess.models.game;


import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.services.BoardService;
import org.dexenjaeger.chess.utils.TreeNode;

class MoveLine {
    private final BoardService service;
    private TreeNode<MoveSummary> tail;

    MoveLine(BoardService service, TreeNode<MoveSummary> tail) {
        this.service = service;
        this.tail = tail;
    }

    private TreeNode<MoveSummary> getMoveResult(Move move) {
        MoveSummary previousMove = tail.getValue();
        return tail.addChild(new MoveSummary(
            move.getSide() == Side.WHITE ? tail.getValue().getTurnNumber() + 1 : tail.getValue().getTurnNumber(),
            move,
            service.applyMove(previousMove.getBoard(), move),
            0,
            null
        ));
    }

    public TreeNode<MoveSummary> applyMoves(Move... moves) {
        for (Move move:moves) {
            tail = getMoveResult(move);
        }
        return tail;
    }
}
