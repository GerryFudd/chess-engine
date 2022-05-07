package org.dexenjaeger.chess.models;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.Turn;

@Getter
@EqualsAndHashCode
@ToString
public class Game {
    private final Set<Castle> castlingRights = new HashSet<>();
    private final LinkedList<Turn> turnHistory = new LinkedList<>();
    private final LinkedList<Board> boardHistory = new LinkedList<>();

    public Game addBoard(Board board) {
        boardHistory.add(board);
        return this;
    }
}
