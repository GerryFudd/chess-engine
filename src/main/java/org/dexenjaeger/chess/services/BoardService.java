package org.dexenjaeger.chess.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

public class BoardService {

    private static Map<Square, Piece> defaultBoardState() {
        Map<Square, Piece> pieceMap = new HashMap<>();
        for (FileType file: FileType.values()) {
            pieceMap.put(
                new Square(file, RankType.TWO),
                new Piece(Side.WHITE, PieceType.PAWN)
            );
            pieceMap.put(
                new Square(file, RankType.SEVEN),
                new Piece(Side.BLACK, PieceType.PAWN)
            );
        }

        pieceMap.put(
            new Square(FileType.A, RankType.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(FileType.H, RankType.ONE),
            new Piece(Side.WHITE, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(FileType.A, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );
        pieceMap.put(
            new Square(FileType.H, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.ROOK)
        );

        pieceMap.put(
            new Square(FileType.B, RankType.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(FileType.G, RankType.ONE),
            new Piece(Side.WHITE, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(FileType.B, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );
        pieceMap.put(
            new Square(FileType.G, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.KNIGHT)
        );

        pieceMap.put(
            new Square(FileType.C, RankType.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(FileType.F, RankType.ONE),
            new Piece(Side.WHITE, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(FileType.C, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );
        pieceMap.put(
            new Square(FileType.F, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.BISHOP)
        );

        pieceMap.put(
            new Square(FileType.D, RankType.ONE),
            new Piece(Side.WHITE, PieceType.QUEEN)
        );
        pieceMap.put(
            new Square(FileType.E, RankType.ONE),
            new Piece(Side.WHITE, PieceType.KING)
        );
        pieceMap.put(
            new Square(FileType.D, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.QUEEN)
        );
        pieceMap.put(
            new Square(FileType.E, RankType.EIGHT),
            new Piece(Side.BLACK, PieceType.KING)
        );

        return pieceMap;
    }

    public static Board standardGameBoard() {
        return new Board(defaultBoardState());
    }

    private final PieceService pieceService;

    public BoardService(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    public Set<SimpleMove> getMoves(Board board, FileType f, RankType r) {
        return getMoves(board, new Square(f, r));
    }

    public Set<SimpleMove> getMoves(Board board, Square sq) {
        return board.getPiece(sq)
            .map(p -> pieceService.getMoves(p, sq, board::getOccupyingSide))
            .orElse(Set.of());
    }

    public Optional<Square> lookupAlternateStartForMove(SimpleMove simpleMove, Board board) {
        return board.getBySideAndType(simpleMove.getSide(), simpleMove.getType())
            .stream()
            .filter(sq -> getMoves(
                board, sq.getFile(), sq.getRank()
            )
                .stream()
                .anyMatch(m -> m.getTo().equals(simpleMove.getTo()) && !m.getFrom().equals(simpleMove.getFrom()))
            )
            .findAny();
    }

    private Board applySingleSimpleMove(Board board, SimpleMove move) {
        if (!getMoves(board, move.getFrom()).contains(move)) {
            throw new ServiceException(String.format("The move %s is not available on this board.\n%s", move, board));
        }
        return board.movePiece(move);
    }

    public Board applySimpleMove(Board board, SimpleMove...moves) {
        Board result = board;
        for (SimpleMove move:moves) {
            result = applySingleSimpleMove(board, move);
        }
        return result;
    }

    public Set<SimpleMove> getMovesBySideAndTarget(Board board, Side side, Square target) {
        return board.getBySide(side)
            .stream()
            .map(sq -> new SimpleMove(sq, target, board.getPiece(sq).orElseThrow()))
            .filter(mo -> pieceService.isLegal(mo, board::getOccupyingSide))
            .collect(Collectors.toSet());
    }
}
