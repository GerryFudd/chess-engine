package org.dexenjaeger.chess.models;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.services.NotImplementedException;

public class Game {

    private final Set<Castle> castlingRights;
    private Side activeSide;
    private final List<Move> moveHistory;
    private final Board board;

    public Game(
        Map<Square, Piece> initialPieceLocations,
        Set<Castle> castlingRights, Side initialActiveSide,
        List<Move> moveHistory
    ) {
        this.castlingRights = castlingRights;
        this.activeSide = initialActiveSide;
        this.moveHistory = moveHistory;
        this.board = new Board(initialPieceLocations);
    }

    public Game applyMove(Move move) {
        if (move instanceof SimpleMove) {
            board.movePiece((SimpleMove) move);
        } else if (move instanceof Castle) {
            board.castle((Castle) move);
        } else {
            throw new NotImplementedException(move.getClass());
        }
        moveHistory.add(move);
        return this;
    }

    public Game switchActiveSide() {
        activeSide = activeSide.other();
        return this;
    }

    public Game revokeCastlingRight(Side side) {
        return this.revokeCastlingRight(new Castle(side, CastleType.LONG))
            .revokeCastlingRight(new Castle(side, CastleType.SHORT));
    }

    public Game revokeCastlingRight(Castle castle) {
        castlingRights.remove(castle);
        return this;
    }
}
