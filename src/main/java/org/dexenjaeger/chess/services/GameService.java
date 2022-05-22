package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.Side.WHITE;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.GameStatus;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.EnPassantCapture;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.SinglePieceMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

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
        return Game.init(BoardService.standardGameBoard())
            .addCastlingRights(getCastlingTypes());
    }

    public Side currentSide(Game game) {
        return game.getPreviousMove().getSide().other();
    }

    private Set<EnPassantCapture> enPassantCaptures(
        Board board, Supplier<Optional<SinglePieceMove>> getPreviousMove, Side side
    ) {
        return getPreviousMove.get()
            .filter(previous -> previous.getType() == PieceType.PAWN)
            .filter(previous -> {
                if (side == WHITE) {
                    return previous.getFrom().getRank() == RankType.SEVEN
                        && previous.getTo().getRank() == RankType.FIVE;
                }
                return previous.getFrom().getRank() == RankType.TWO
                    && previous.getTo().getRank() == RankType.FOUR;
            })
            .stream()
            .flatMap(previous -> Stream.concat(
                    previous.getFrom().getFile().shift(-1).stream(),
                    previous.getFrom().getFile().shift(1).stream()
            )
                .map(f -> new Square(f, previous.getTo().getRank()))
                .filter(
                    sq -> board
                        .getPiece(sq)
                        .filter(p -> p.equals(new Piece(side, PieceType.PAWN)))
                        .isPresent()
                )
                .map(sq -> new EnPassantCapture(
                    side, sq.getFile(), previous.getFrom().getFile()
                )))
            .filter(move -> boardService.isLegal(board, move))
            .collect(Collectors.toSet());
    }

    public Set<Move> getAvailableMoves(Game game) {
        Board board = game.getCurrentBoard();
        Side side = currentSide(game);
        Set<Move> result = boardService.getMovesBySide(
            board, side
        );

        result.addAll(enPassantCaptures(
            board,
            () -> {
                Move m = game.getPreviousMove();
                if (m instanceof SinglePieceMove) {
                    return Optional.of((SinglePieceMove) m);
                }
                return Optional.empty();
            },
            side
        ));

        result.addAll(game.getCastlingRights().stream()
            .filter(c -> boardService.isLegal(board, c))
            .collect(Collectors.toSet()));

        return result;
    }

    public GameStatus getGameStatus(Game game) {
        Side side = currentSide(game);
        if (getAvailableMoves(game).isEmpty()) {
            if (boardService.isSideInCheck(
                game.getCurrentBoard(), side
            )) {
                return side == WHITE ? GameStatus.BLACK_WON : GameStatus.WHITE_WON;
            }
            return GameStatus.STALEMATE;
        }
        return side == WHITE ? GameStatus.WHITE_TO_MOVE : GameStatus.BLACK_TO_MOVE;
    }

    public Game applyMove(Game game, Move move) {
        return game.addMove(move, boardService.applyMove(game.getCurrentBoard(), move));
    }
}
