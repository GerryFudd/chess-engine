package org.dexenjaeger.chess.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.PromotionMove;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.moves.SinglePieceMove;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

public class BoardService {

    private static Map<Square, Piece> defaultBoardState() {
        Map<Square, Piece> pieceMap = new HashMap<>();
        for (FileType file: FileType.values()) {
            for (RankType rank:RankType.values()) {
                if (rank.ordinal() > 1 && rank.ordinal() < 6) {
                    continue;
                }
                Side side = rank.ordinal() <= 1
                    ? Side.WHITE
                    : Side.BLACK;
                PieceType type;
                if (rank == RankType.TWO || rank == RankType.SEVEN) {
                    type = PieceType.PAWN;
                } else if (file == FileType.A || file == FileType.H) {
                    type = PieceType.ROOK;
                } else if (file == FileType.B || file == FileType.G) {
                    type = PieceType.KNIGHT;
                } else if (file == FileType.C || file == FileType.F) {
                    type = PieceType.BISHOP;
                } else if (file == FileType.D) {
                    type = PieceType.QUEEN;
                } else {
                    type = PieceType.KING;
                }
                pieceMap.put(
                    new Square(file, rank),
                    new Piece(side, type)
                );
            }
        }

        return pieceMap;
    }

    public static Board standardGameBoard() {
        return new Board(defaultBoardState());
    }

    private final PieceService pieceService;

    @Inject
    public BoardService(PieceService pieceService) {
        this.pieceService = pieceService;
    }

    public Set<SinglePieceMove> getMoves(Board board, FileType f, RankType r) {
        return getMoves(board, new Square(f, r));
    }

    public Set<SinglePieceMove> getMoves(Board board, Square sq) {
        return board.getPiece(sq)
            .map(p -> pieceService
                .getMoves(p, sq, board::getOccupyingSide)
                .stream()
                .filter(mo -> {
                    Board testBoard;
                    if (mo instanceof SimpleMove) {
                        testBoard = board.movePiece((SimpleMove) mo);
                    } else if (mo instanceof PromotionMove) {
                        testBoard = board.promote((PromotionMove) mo);
                    } else {
                        throw new NotImplementedException(mo.getClass());
                    }
                    return !isSideInCheck(testBoard, mo.getSide());
                })
                .collect(Collectors.toSet())
            )
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

    private boolean hasPiece(Board board, FileType file, RankType rank, Piece piece) {
        return board.getPiece(file, rank)
            .filter(p -> p.equals(piece))
            .isPresent();
    }

    public boolean isLegal(Board board, Castle move) {
        RankType rank = move.getSide() == Side.WHITE ? RankType.ONE : RankType.EIGHT;
        Set<FileType> middleFiles = move.getType() == CastleType.LONG
            ? Set.of(FileType.B, FileType.C, FileType.D)
            : Set.of(FileType.F, FileType.G);
        if (middleFiles.stream().anyMatch(f -> board.getPiece(f, rank).isPresent())) {
            return false;
        }
        if (!hasPiece(board, move.getType().getRookFileFrom(), rank, new Piece(move.getSide(), PieceType.ROOK))) {
            return false;
        }
        return hasPiece(board, FileType.E, rank, new Piece(move.getSide(), PieceType.KING));
    }

    private boolean isSideInCheck(Board board, Side side) {
        return board.getBySideAndType(side, PieceType.KING)
            .stream()
            .anyMatch(sq -> !getMovesBySideAndTarget(board, side.other(), sq).isEmpty());
    }

    private Board applySingleCastle(Board board, Castle move) {
        if (!isLegal(board, move)) {
            throw new ServiceException(String.format("The move %s is not available on this board.\n%s", move, board));
        }
        return board.castle(move);
    }

    private Board applySinglePieceMove(Board board, SinglePieceMove move) {
        if (!pieceService.isLegal(move, board::getOccupyingSide)) {
            throw new ServiceException(String.format("The move %s is not available on this board.\n%s", move, board));
        }
        if (move instanceof SimpleMove) {
            return board.movePiece((SimpleMove) move);
        }
        return board.promote((PromotionMove) move);
    }

    private Board applySingleMove(Board board, Move move) {
        if (move instanceof SinglePieceMove) {
            return applySinglePieceMove(board, (SinglePieceMove) move);
        }
        if (move instanceof Castle) {
            return applySingleCastle(board, (Castle) move);
        }
        throw new NotImplementedException(move.getClass());
    }

    public Board applyMove(Board board, Move...moves) {
        Board result = board;
        for (Move move:moves) {
            result = applySingleMove(result, move);
            if (isSideInCheck(result, move.getSide())) {
                throw new ServiceException(String.format(
                    "The move %s is not allowed for %s because it would put %s in check.",
                    move, move.getSide(), move.getSide()
                ));
            }
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

    public Set<Move> getMovesBySide(Board board, Side side) {
        Set<Square> squaresForSide = board.getBySide(side);
        return squaresForSide.stream()
            .flatMap(sq -> getMoves(board, sq).stream())
            .collect(Collectors.toSet());
    }
}
