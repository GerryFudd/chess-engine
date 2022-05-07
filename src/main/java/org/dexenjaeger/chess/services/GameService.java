package org.dexenjaeger.chess.services;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.GameStatus;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.Turn;

public class GameService {
    private final BoardService boardService;

    @Inject
    public GameService(BoardService boardService) {
        this.boardService = boardService;
    }

    public Set<Castle> getCastlingTypes() {
        return Stream.of(Side.values())
            .flatMap(s -> Stream.of(CastleType.values()).map(t -> new Castle(s, t)))
            .collect(Collectors.toSet());
    }

    public Game startGame() {
        Game result = new Game()
            .addBoard(BoardService.standardGameBoard());

        result.getCastlingRights().addAll(getCastlingTypes());

        return result;
    }

    public Side currentSide(Game game) {
        if (game.getTurnHistory().isEmpty()) {
            return Side.WHITE;
        }
        return game.getTurnHistory().getLast().getBlackMove().isPresent()
            ? Side.WHITE
            : Side.BLACK;
    }

    public Set<Move> getAvailableMoves(Game game) {
        Board board = game.getBoardHistory().getLast();
        Set<Move> result = boardService.getMovesBySide(
            board,
            currentSide(game)
        );

        result.addAll(game.getCastlingRights().stream()
            .filter(c -> boardService.isLegal(board, c))
            .collect(Collectors.toSet()));

        return result;
    }

    public GameStatus getGameStatus(Game game) {
        Side side = currentSide(game);
        if (getAvailableMoves(game).isEmpty()) {
            if (boardService.isSideInCheck(
                game.getBoardHistory().getLast(), side
            )) {
                return side == Side.WHITE ? GameStatus.BLACK_WON : GameStatus.WHITE_WON;
            }
            return GameStatus.STALEMATE;
        }
        return side == Side.WHITE ? GameStatus.WHITE_TO_MOVE : GameStatus.BLACK_TO_MOVE;
    }

    public void applyTurn(Game game, Turn turn) {
        Board currentBoard = boardService.applyMove(
            game.getBoardHistory().getLast(),
            turn.getWhiteMove()
        );
        game.addBoard(currentBoard);
        turn.getBlackMove().ifPresent(m -> game.addBoard(
            boardService.applyMove(currentBoard, m)
        ));
        game.getTurnHistory().add(turn);
    }
}
