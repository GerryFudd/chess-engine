package org.dexenjaeger.chess.services;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.TagType;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.moves.Turn;
import org.dexenjaeger.chess.models.pgn.PgnMove;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.services.pgn.PgnMoveExtractor;
import org.dexenjaeger.chess.utils.OptionalsUtil;

public class PgnService {
    // This service implements a parser for the spec outlined here:
    // http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm#c2
    // TODO: implement a FEN service that implements the spec in section 16

    // 8.2: Movetext section
    // The movetext section is composed of chess moves, move number indications, optional
    // annotations, and a single concluding game termination marker.
    // Because illegal moves are not real chess moves, they are not permitted in PGN
    // movetext. They may appear in commentary, however. One would hope that illegal moves
    // are relatively rare in games worthy of recording.
    // TODO: match movetext that includes annotations and the game termination marker.
    // 8.2.3.3: Basic SAN move construction
    // A basic SAN move is given by listing the moving piece letter (omitted for pawns)
    // followed by the destination square. Capture moves are denoted by the lower case
    // letter "x" immediately prior to the destination square; pawn captures include the
    // file letter of the originating square of the capturing pawn immediately prior to
    // the "x" character.
    // SAN kingside castling is indicated by the sequence "O-O"; queenside castling is
    // indicated by the sequence "O-O-O". Note that the upper case letter "O" is used, not
    // the digit zero. The use of a zero character is not only incompatible with traditional
    // text practices, but it can also confuse parsing algorithms which also have to
    // understand about move numbers and game termination markers. Also note that the use
    // of the letter "O" is consistent with the practice of having all chess move symbols
    // start with a letter; also, it follows the convention that all non-pwn move symbols
    // start with an upper case letter.
    // En passant captures do not have any special notation; they are formed as if the
    // captured pawn were on the capturing pawn's destination square. Pawn promotions
    // are denoted by the equal sign "=" immediately following the destination square
    // with a promoted piece letter (indicating one of knight, bishop, rook, or queen)
    // immediately following the equal sign. As above, the piece letter is in upper case.
    // TODO: parse pawn promotions
    // 8.2.3.4: Disambiguation
    // In the case of ambiguities (multiple pieces of the same type moving to the same
    // square), the first appropriate disambiguating step of the three following steps is
    // taken:
    //   - First, if the moving pieces can be distinguished by their originating files, the
    //     originating file letter of the moving piece is inserted immediately after the
    //     moving piece letter.
    //   - Second (when the first step fails), if the moving pieces can be distinguished by
    //     their originating ranks, the originating rank digit of the moving piece is
    //     inserted immediately after the moving piece letter.
    //   - Third (when both the first and the second steps fail), the two character square
    //     coordinate of the originating square of the moving piece is inserted immediately
    //     after the moving piece letter.
    // 8.2.3.8: SAN move suffix annotations
    // Import format PGN allows for the use of traditional suffix annotations for moves.
    // There are exactly six such annotations available: "!", "?", "!!", "!?", "?!", and
    // "??". At most one such suffix annotation may appear per move, and if present, it is
    // always the last part of the move symbol.
    // When exported, a move suffix annotation is translated into the corresponding Numeric
    // Annotation Glyph as described in a later section of this document. For example, if
    // the single move symbol "Qxa8?" appears in an import format PGN movetext, it would
    // be replaced with the two adjacent symbols "Qxa8 $2".
    // TODO: implement this spec using the table in "10: Numeric Annotation Glyphs"

    // The following are the castling indicators. They are strings rather than regex.
    private static final String CASTLE_SHORT = "O-O";
    private static final String CASTLE_LONG = "O-O-O";

    // The specification for PGN notation includes a disambiguation step that begins, "when
    // both the first and the second steps fail." This seems impossible, but it is possible if
    // there are three or more pieces of the same type where two share a file and two share a
    // rank. For example
    // White has promoted two pawns to queens, and they are on a8, a5, and d5. If the queen on
    // a5 moves to d8, then Qad8 and Q5a8 are both ambiguous, so Qa5a8 is the representation.

    // The below regex matches
    //   - an optional character indicating a starting file followed by
    //   - an optional character indicating a starting rank followed by
    //   - an optional x to indicate a capture followed by
    //   - a file indicating character followed by
    //   - a rank indicating character.
    // This regex captures the optional starting location indicator and the destination square
    // rank and file indicators.
    private static final Pattern movePattern = Pattern.compile("([RNBQK])?([a-h])?([1-8])?x?([a-h][1-8])");

    // 8.2.2: Movetext move number indications
    // A move number indication is composed of one or more adjacent digits (an integer token)
    // followed by zero or more periods. The integer portion of the indication gives the
    // move number of the immediately following white move (if present) and also the
    // immediately following black move (if present).
    // 8.2.2.1: Import format move number indications
    // PGN import format does not require move number indications. It does not prohibit
    // superfluous move number indications anywhere in the movetext as long as the move
    // numbers are correct.
    // TODO: handle PGN imports that omit move number indicators
    // PGN import format move number indications may have zero or more period characters
    // following the digit sequence that gives the move number; one or more white space
    // characters may appear between the digit sequence and the period(s).

    // The below regex for a turn number will match
    //   - a number that is not preceded by letters followed by
    //   - zero or more whitespace characters followed by
    //   - zero or more periods followed by
    //   - one or more white space characters.
    private static final Pattern turnPattern = Pattern.compile("(?<!\\w)(\\d+)\\s*\\.*\\s+([^\\s]+)(?:\\s+([^\\s]+))?\\s*");
    private static final Pattern turnStartPattern = Pattern.compile("(?<!\\w)\\d+\\s*\\.*\\s+");

    // 8.1: Tag pair section
    // The tag pair section is composed of a series of zero or more tag pairs.
    // A tag pair is composed of four consecutive tokens:
    //   - a left bracket token,
    //   - a symbol token,
    //   - a string token, and
    //   - a right bracket token.
    // The symbol token is the tag name and the string token is the tag value associated with
    // the tag name.
    // For PGN import format, there may be zero or more white space characters between any
    // adjacent pair of tokens in a tag pair.
    // PGN import format may have multiple tag pairs on the same line and may even have a tag
    // pair spanning more than a single line.
    // TODO: determine whether newlines may appear within the text token in the import format.

    // The following regex matches
    //   - a left bracket followed by
    //   - zero or more whitespace characters followed by
    //   - one or more letters followed by
    //   - zero or more whitespace characters followed by
    //   - a series of characters surrounded by quotation marks followed by
    //   - zero or more whitespace characters followed by
    //   - a right bracket.
    // It captures the "symbol token" and the "string token".
    private static final Pattern tagPattern = Pattern.compile("\\[(\\w+)\\s*\"(.+)\"\\s*]");

    private final BoardService boardService;
    private final GameService gameService;

    @Inject
    public PgnService(BoardService boardService, GameService gameService) {
        this.boardService = boardService;
        this.gameService = gameService;
    }

    private String castleToPgnMove(Castle move) {
        return move.toString();
    }

    private String simpleToPgnMove(SimpleMove move, Board board) {
        if (move.getType() == PieceType.PAWN) {
            if (move.getFrom().getFile() != move.getTo().getFile()) {
                return String.format(
                    "%sx%s",
                    move.getFrom().getFile(),
                    move.getTo()
                );
            }
            return move.getTo().toString();
        }

        StringBuilder result = new StringBuilder(move.getType().getRepresentation());
        boardService
            .lookupAlternateStartForMove(move, board)
            .ifPresent(sq -> {
                if (sq.getFile() != move.getFrom().getFile()) {
                    result.append(move.getFrom().getFile());
                } else if (sq.getRank() != move.getFrom().getRank()) {
                    result.append(move.getFrom().getRank());
                }
            });

        return result.append(move.getTo()).toString();
    }
    
    public String toPgnMove(Move move, Board board) {
        if (move instanceof Castle) {
            return castleToPgnMove((Castle) move);
        }
        if (move instanceof SimpleMove) {
            return simpleToPgnMove((SimpleMove) move, board);
        }
        throw new NotImplementedException(move.getClass());
    }

    private ServiceException ambiguousMoveException(
        String pgnMove, Side side,
        List<SimpleMove> possibleMoves,
        Board board
    ) {
        return new ServiceException(String.format(
            "The PGN move %s for side %s could be any of %s on this board.\n%s",
            pgnMove, side,
            possibleMoves.stream().map(SimpleMove::toString).collect(Collectors.joining(", ")),
            board
        ));
    }

    private ServiceException impossibleMoveException(
        String pgnMove, Side side,
        Board board
    ) {
        return new ServiceException(String.format(
            "There are no possible moves matching %s for side %s on this board.\n%s",
            pgnMove, side, board
        ));
    }
    
    public Move fromPgnMove(String pgnMoveString, Side side, Board board) {
        if (pgnMoveString == null || pgnMoveString.length() == 0) {
            throw new ServiceException("A pgn move may not be empty.");
        }
        if (CASTLE_SHORT.equals(pgnMoveString)) {
            return new Castle(side, CastleType.SHORT);
        }
        if (CASTLE_LONG.equals(pgnMoveString)) {
            return new Castle(side, CastleType.LONG);
        }

        Matcher moveMatcher = movePattern.matcher(pgnMoveString);
        if (!moveMatcher.find()) {
            throw new ServiceException(String.format("%s is not a valid move in PGN format", pgnMoveString));
        }
        PgnMoveExtractor pgnMoveExtractor = new PgnMoveExtractor(new PgnMove(
            moveMatcher.group(1),
            moveMatcher.group(2),
            moveMatcher.group(3),
            moveMatcher.group(4)
        ));
        List<SimpleMove> possibleMoves = boardService.getLegalMovesForSideWithTarget(board, side, pgnMoveExtractor.getTarget())
            .stream()
            .filter(possible -> possible.getType() == pgnMoveExtractor.getPieceType())
            .collect(Collectors.toList());
        if (possibleMoves.size() == 0) {
            throw impossibleMoveException(pgnMoveString, side, board);
        }
        if (possibleMoves.size() == 1) {
            return possibleMoves.get(0);
        }
        possibleMoves = possibleMoves.stream()
            .filter(OptionalsUtil.emptyOrMatches(
                pgnMoveExtractor::getOriginFile,
                possible -> possible.getFrom().getFile()
            ))
            .filter(OptionalsUtil.emptyOrMatches(
                pgnMoveExtractor::getOriginRank,
                possible -> possible.getFrom().getRank()
            ))
            .collect(Collectors.toList());
        if (possibleMoves.size() > 1) {
            throw ambiguousMoveException(pgnMoveString, side, possibleMoves, board);
        }
        return possibleMoves.stream()
            .findAny()
            .orElseThrow(() -> impossibleMoveException(pgnMoveString, side, board));
    }

    public Turn fromPgnTurn(String pgnTurn, Board board) {
        Matcher turnMatcher = turnPattern.matcher(pgnTurn);
        if (!turnMatcher.find()) {
            throw new ServiceException(String.format("%s is not a valid turn in PGN format.", pgnTurn));
        }
        int turnNumber = Integer.parseInt(turnMatcher.group(1));
        Move whiteMove = fromPgnMove(turnMatcher.group(2), Side.WHITE, board);
        if (turnMatcher.group(3) == null) {
            return new Turn(turnNumber, whiteMove);
        }
        Move blackMove = fromPgnMove(
            turnMatcher.group(3), Side.BLACK,
            boardService.applyMove(board, whiteMove)
        );
        return new Turn(turnNumber, whiteMove, blackMove);
    }

    public Game gameFromPgn(String pgn) {
        Game game = gameService.startGame();
        int cursor = 0;
        Matcher tagMatcher = tagPattern.matcher(pgn);
        while (tagMatcher.find()) {
            cursor = tagMatcher.end();
            String tagLabel = tagMatcher.group(1);
            TagType.fromLabel(tagLabel)
                .ifPresentOrElse(
                    tagType -> game.addTag(tagType, tagMatcher.group(2)),
                    () -> game.addTag(tagLabel, tagMatcher.group(2))
                );
        }
        Matcher turnStartMatcher = turnStartPattern.matcher(pgn);
        while (turnStartMatcher.find(cursor)) {
            cursor = turnStartMatcher.end();
            gameService.applyTurn(game, fromPgnTurn(
                pgn.substring(turnStartMatcher.start()),
                game.currentBoard()
            ));
        }
        return game;
    }

    public List<Turn> fromPgnTurnList(String pgnTurnList) {
        return gameFromPgn(pgnTurnList).getTurnHistory();
    }

    public Board boardFromPgn(String pgn) {
        return gameFromPgn(pgn).currentBoard();
    }
}
