package org.dexenjaeger.chess.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.moves.Turn;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.OptionalsUtil;

public class FenService {
    // http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm#c16

    // 16.1.3: Data fields
    // FEN specifies the piece placement, the active color, the castling
    // availability, the en passant target square, the halfmove clock, and the
    // fullmove number. These can all fit on a single text line in an easily
    // read format. The length of a FEN position description varies somewhat
    // according to the position. In some cases, the description could be
    // eighty or more characters in length and so may not fit conveniently on
    // some displays. However, these positions aren't too common.
    //
    // A FEN description has six fields. Each field is composed only of
    // non-blank printing ASCII characters. Adjacent fields are separated by a
    // single ASCII space character.
    // 16.1.3.1: Piece placement data
    // The first field represents the placement of the pieces on the board.
    // The board contents are specified starting with the eighth rank and
    // ending with the first rank. For each rank, the squares are specified
    // from file a to file h. White pieces are identified by uppercase SAN
    // piece letters ("PNBRQK") and black pieces are identified by lowercase
    // SAN piece letters ("pnbrqk"). Empty squares are represented by the
    // digits one through eight; the digit used represents the count of
    // contiguous empty squares along a rank. A solidus character "/" is used
    // to separate data of adjacent ranks.
    private Optional<Piece> pieceFromChar(char c) {
        switch (c) {
            case 'r':
                return Optional.of(new Piece(Side.BLACK, PieceType.ROOK));
            case 'R':
                return Optional.of(new Piece(Side.WHITE, PieceType.ROOK));
            case 'n':
                return Optional.of(new Piece(Side.BLACK, PieceType.KNIGHT));
            case 'N':
                return Optional.of(new Piece(Side.WHITE, PieceType.KNIGHT));
            case 'b':
                return Optional.of(new Piece(Side.BLACK, PieceType.BISHOP));
            case 'B':
                return Optional.of(new Piece(Side.WHITE, PieceType.BISHOP));
            case 'q':
                return Optional.of(new Piece(Side.BLACK, PieceType.QUEEN));
            case 'Q':
                return Optional.of(new Piece(Side.WHITE, PieceType.QUEEN));
            case 'k':
                return Optional.of(new Piece(Side.BLACK, PieceType.KING));
            case 'K':
                return Optional.of(new Piece(Side.WHITE, PieceType.KING));
            case 'p':
                return Optional.of(new Piece(Side.BLACK, PieceType.PAWN));
            case 'P':
                return Optional.of(new Piece(Side.WHITE, PieceType.PAWN));
            default:
                return Optional.empty();
        }
    }
    private int getIntFromChar(char c) {
        return c - 48;
    }
    public Map<Square, Piece> readPiecesFromRank(RankType rank, String fenRank) {
        Map<Square, Piece> pieceMap = new HashMap<>();
        FileType file = FileType.A;
        for (char c:fenRank.toCharArray()) {
            if (file == null) {
                break;
            }
            Optional<Piece> maybePiece = pieceFromChar(c);
            int shiftBy = 1;
            if (maybePiece.isPresent()) {
                pieceMap.put(new Square(file, rank), maybePiece.get());
            } else {
                shiftBy = getIntFromChar(c);
                if (shiftBy < 1 || shiftBy > 8) {
                    throw new ServiceException(String.format(
                        "FEN rank includes invalid character %s", c
                    ));
                }
            }
            file = file.shift(shiftBy).orElse(null);
        }
        return pieceMap;
    }
    public Board readPieceLocations(String fenPieceLocations) {
        Map<Square, Piece> pieceMap = new HashMap<>();
        RankType rank = RankType.EIGHT;
        for (String line:fenPieceLocations.split("/")) {
            if (rank == null) {
                break;
            }
            pieceMap.putAll(readPiecesFromRank(rank, line.trim()));
            rank = rank.shift(-1).orElse(null);
        }
        return new Board(pieceMap);
    }

    // 16.1.3.2: Active color
    // The second field represents the active color. A lower case "w" is used
    // if White is to move; a lower case "b" is used if Black is the active
    // player.
    public Side readSide(String representation) {
        for (Side side:Side.values()) {
            if (side.getRepresentation().equals(representation)) {
                return side;
            }
        }
        return null;
    }
    // 16.1.3.3: Castling availability
    // The third field represents castling availability. This indicates
    // potential future castling that may or may not be possible at the moment
    // due to blocking pieces or enemy attacks. If there is no castling
    // availability for either side, the single character symbol "-" is used.
    // Otherwise, a combination of from one to four characters are present.
    // If White has kingside castling availability, the uppercase letter "K"
    // appears. If White has queenside castling availability, the uppercase
    // letter "Q" appears. If Black has kingside castling availability, the
    // lowercase letter "k" appears. If Black has queenside castling
    // availability, then the lowercase letter "q" appears. Those letters
    // which appear will be ordered first uppercase before lowercase and
    // second kingside before queenside. There is no white space between the
    // letters.
    private Optional<Castle> castlingTypeFromChar(char c) {
        switch (c) {
            case 'Q':
                return Optional.of(new Castle(Side.WHITE, CastleType.LONG));
            case 'K':
                return Optional.of(new Castle(Side.WHITE, CastleType.SHORT));
            case 'q':
                return Optional.of(new Castle(Side.BLACK, CastleType.LONG));
            case 'k':
                return Optional.of(new Castle(Side.BLACK, CastleType.SHORT));
            default:
                return Optional.empty();
        }
    }
    public Set<Castle> getCastlingRights(String fenCastlingTypes) {
        Set<Castle> result = new HashSet<>();
        for (char c:fenCastlingTypes.toCharArray()) {
            castlingTypeFromChar(c).ifPresent(result::add);
        }
        return result;
    }
    // 16.1.3.4: En passant target square
    // The fourth field is the en passant target square. If there is no en
    // passant target square then the single character symbol "-" appears.
    // If there is an en passant target square then is represented by a
    // lowercase file character immediately followed by a rank digit.
    // Obviously, the rank digit will be "3" following a white pawn double
    // advance (Black is the active color) or else be the digit "6" after a
    // black pawn double advance (White being the active color).
    //
    // An en passant target square is given if and only if the last move was
    // a pawn advance of two squares. Therefore, an en passant target square
    // field may have a square name even if there is no pawn of the opposing
    // side that may immediately execute the en passant capture.

    public Optional<Square> getEnPassantSquare(String s) {
        if (s == null || s.length() != 2) {
            return Optional.empty();
        }
        return OptionalsUtil.merge(
            () -> FileType.fromString(s.substring(0, 1)),
            () -> RankType.fromString(s.substring(1)),
            p -> new Square(p.getLeft(), p.getRight())
        );
    }

    public Game getGame(String fen) {
        String[] tokens = fen.split(" ");
        if (tokens.length < 6) {
            throw new ServiceException(String.format("Invalid FEN: %s", fen));
        }
        Board board = readPieceLocations(tokens[0]);
        Side side = readSide(tokens[1]);
        Set<Castle> castlingRights = getCastlingRights(tokens[2]);
        Optional<Square> enPassantTarget = getEnPassantSquare(tokens[3]);
        int turnNumber = Integer.parseInt(tokens[5]);
        return enPassantTarget
            .map(sq -> {
                Side previousSide = side.other();
                RankType startingRank = previousSide == Side.WHITE ? RankType.TWO : RankType.SEVEN;
                RankType endingRank = previousSide == Side.WHITE ? RankType.FOUR : RankType.FIVE;
                // Create a fake move to unwind
                Board previousBoard = board.movePiece(new SimpleMove(
                    new Square(sq.getFile(), endingRank),
                    new Square(sq.getFile(), startingRank),
                    PieceType.PAWN, previousSide
                ));
                Turn previousTurn = previousSide == Side.WHITE
                    ? new Turn(turnNumber, new SimpleMove(
                        new Square(sq.getFile(), startingRank),
                        new Square(sq.getFile(), endingRank),
                        PieceType.PAWN, previousSide
                    ))
                    : new Turn(turnNumber - 1, null, new SimpleMove(
                        new Square(sq.getFile(), startingRank),
                        new Square(sq.getFile(), endingRank),
                        PieceType.PAWN, previousSide
                    ));
                return new Game()
                    .addBoard(previousBoard)
                    .addBoard(board)
                    .addTurn(previousTurn)
                    .addCastlingRights(castlingRights)
                    .setTurnNumber(turnNumber);
            })
            .orElseGet(() -> {
                Game result = new Game()
                    .addBoard(board)
                    .addCastlingRights(castlingRights)
                    .setTurnNumber(turnNumber);
                if (side == Side.BLACK) {
                    return result.addTurn(new Turn(turnNumber, null));
                }
                return result;
            });
    }
}
