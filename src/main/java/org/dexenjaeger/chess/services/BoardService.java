package org.dexenjaeger.chess.services;

import static java.lang.Math.abs;
import static java.lang.Math.max;

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
import org.dexenjaeger.chess.models.moves.EnPassantCapture;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.NormalMove;
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

    // This method returns any move that satisfies the piece movement rules.
    // These moves may result in the moving side placing themselves in check.
    private Set<SimpleMove> getMovesFromSourcesAndTarget(Board board, Set<Square> sources, Square target) {
        return sources
            .stream()
            .map(sq -> new SimpleMove(sq, target, board.getPiece(sq).orElseThrow()))
            .filter(mo -> pieceService.matchesPieceMovementRule(mo, board::getOccupyingSide))
            .collect(Collectors.toSet());
    }

    public boolean isSideInCheck(Board board, Side side) {
        return board.getBySideAndType(side, PieceType.KING)
            .stream()
            .anyMatch(sq -> !getMovesFromSourcesAndTarget(board, board.getBySide(side.other()), sq).isEmpty());
    }

    private Board applySingleMove(Board board, Move move) {
        if (move instanceof SimpleMove) {
            return board.movePiece((SimpleMove) move);
        }
        if (move instanceof PromotionMove) {
            return board.promote((PromotionMove) move);
        }
        if (move instanceof EnPassantCapture) {
            return board.captureEnPassant((EnPassantCapture) move);
        }
        if (move instanceof Castle) {
            return board.castle((Castle) move);
        }
        throw new NotImplementedException(move.getClass());
    }

    // This method tests the move on the given board without regard to whether the
    // move satisfies piece movement rules or other restrictions on castling
    // or capturing en passant.
    private boolean satisfiesCheckingRule(Board board, Move move) {
        return !isSideInCheck(applySingleMove(board, move), move.getSide());
    }

    // Publicly facing method that only returns legal moves
    public Set<SimpleMove> getLegalMovesForSideWithTarget(Board board, Side side, Square target) {
        return getMovesFromSourcesAndTarget(board, board.getBySide(side), target)
            .stream()
            .filter(move -> satisfiesCheckingRule(board, move))
            .collect(Collectors.toSet());
    }

    // Publicly facing method that only returns legal moves
    public Set<NormalMove> getMoves(Board board, FileType f, RankType r) {
        return getMoves(board, new Square(f, r));
    }
    public Set<NormalMove> getMoves(Board board, Square sq) {
        return board.getPiece(sq)
            .map(p -> pieceService
                .getMoves(p, sq, board::getOccupyingSide)
                .stream()
                .filter(move -> isLegal(board, move))
                .collect(Collectors.toSet())
            )
            .orElse(Set.of());
    }

    // Only returns squares corresponding to legal moves
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

    private boolean satisfiesCastlingPiecePlacementRule(Board board, Castle move) {
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

    private boolean isLegalCastle(Board board, Castle move) {
        return satisfiesCastlingPiecePlacementRule(board, move)
            && satisfiesCheckingRule(board, move);
    }

    private boolean satisfiesEnPasantPiecePlacementRule(Board board, EnPassantCapture move) {
        if (board.getPiece(move.getTo()).isPresent()) {
            return false;
        }
        if (board.getPiece(move.getCapturedSquare())
            .filter(p -> p.equals(new Piece(move.getSide().other(), PieceType.PAWN)))
            .isEmpty()) {
            return false;
        }
        return board.getPiece(move.getFrom())
            .filter(p -> p.equals(move.getPiece()))
            .isPresent();
    }

    private boolean isLegalEnPassant(Board board, EnPassantCapture move) {
        return satisfiesEnPasantPiecePlacementRule(board, move)
            && satisfiesCheckingRule(board, move);
    }

    private boolean isLegalNormal(Board board, NormalMove move) {
        return pieceService.matchesPieceMovementRule(move, board::getOccupyingSide)
            && satisfiesCheckingRule(board, move);
    }

    public boolean isLegal(Board board, Move move) {
        if (move instanceof NormalMove) {
            return isLegalNormal(board, (NormalMove) move);
        }
        if (move instanceof Castle) {
            return isLegalCastle(board, (Castle) move);
        }
        if (move instanceof EnPassantCapture) {
            return isLegalEnPassant(board, (EnPassantCapture) move);
        }
        throw new NotImplementedException(move.getClass());
    }

    public Board applyMove(Board board, Move...moves) {
        Board result = board;
        for (Move move:moves) {
            if (!isLegal(result, move)) {
                throw new ServiceException(String.format("The move %s is not available on this board.\n%s", move, result));
            }
            result = applySingleMove(result, move);
        }
        return result;
    }

    public Set<Move> getMovesBySide(Board board, Side side) {
        Set<Square> squaresForSide = board.getBySide(side);
        return squaresForSide.stream()
            .flatMap(sq -> getMoves(board, sq).stream())
            .collect(Collectors.toSet());
    }

    public int distance(Square a, Square b) {
        return max(abs(a.getRank().ordinal() - b.getRank().ordinal()), abs(a.getFile().ordinal() - b.getFile().ordinal()));
    }

    public Set<Square> getTargetSquares(Move move) {
        if (move instanceof SinglePieceMove) {
            return Set.of(((SinglePieceMove) move).getTo());
        }
        if (move instanceof Castle) {
            RankType rankType = move.getSide() == Side.WHITE ? RankType.ONE : RankType.EIGHT;
            switch (((Castle) move).getType()) {
                case LONG:
                    return Set.of(new Square(FileType.C, rankType), new Square(FileType.D, rankType));
                case SHORT:
                    return Set.of(new Square(FileType.F, rankType), new Square(FileType.G, rankType));
                default:
                    throw new NotImplementedException(((Castle) move).getType());
            }
        }
        throw new NotImplementedException(move.getClass());
    }
}
