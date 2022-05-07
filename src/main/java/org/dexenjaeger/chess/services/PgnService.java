package org.dexenjaeger.chess.services;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.config.Inject;
import org.dexenjaeger.chess.models.Game;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.CastleType;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.SimpleMove;
import org.dexenjaeger.chess.models.moves.Turn;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.utils.ConversionUtil;

public class PgnService {
    private static final Pattern movePattern = Pattern.compile("([a-h]|[1-8])?x?([a-h][1-8])");
    private static final Pattern turnPattern = Pattern.compile("(\\d+)\\.\\s*([^\\s]+)(?:\\s+([^\\s]+))?\\s*");
    private static final Pattern turnStartPattern = Pattern.compile("\\d+\\.");

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
    
    private PieceType typeFromIndicator(char indicator) {
        switch (indicator) {
            case 'R':
                return PieceType.ROOK;
            case 'N':
                return PieceType.KNIGHT;
            case 'B':
                return PieceType.BISHOP;
            case 'Q':
                return PieceType.QUEEN;
            case 'K':
                return PieceType.KING;
            default:
                return PieceType.PAWN;
        }
    }

    private Square squareFromString(String squareString) {
        FileType file = FileType.fromCharVal(squareString.charAt(0))
            .orElseThrow(() -> new ServiceException(String.format("%s is not a file value.", squareString.charAt(0))));

        RankType rank = RankType.fromIntVal(ConversionUtil.intFromChar(squareString.charAt(1)))
            .orElseThrow(() -> new ServiceException(String.format("%s is not a rank value.", squareString.charAt(1))));
        return new Square(file, rank);
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
    
    public Move fromPgnMove(String pgnMove, Side side, Board board) {
        if (pgnMove == null || pgnMove.length() == 0) {
            throw new ServiceException("A pgn move may not be empty.");
        }
        if ("0-0".equals(pgnMove)) {
            return new Castle(side, CastleType.SHORT);
        }
        if ("0-0-0".equals(pgnMove)) {
            return new Castle(side, CastleType.LONG);
        }

        PieceType type = typeFromIndicator(pgnMove.charAt(0));
        Matcher moveMatcher = movePattern.matcher(pgnMove);
        if (!moveMatcher.find()) {
            throw new ServiceException(String.format("%s is not a valid move in PGN format", pgnMove));
        }
        Square target = squareFromString(moveMatcher.group(2));
        List<SimpleMove> possibleMoves = boardService.getMovesBySideAndTarget(board, side, target)
            .stream()
            .filter(possible -> possible.getType() == type)
            .collect(Collectors.toList());
        if (possibleMoves.size() == 0) {
            throw impossibleMoveException(pgnMove, side, board);
        }
        if (possibleMoves.size() == 1) {
            return possibleMoves.get(0);
        }
        if (moveMatcher.group(1) == null) {
            throw ambiguousMoveException(
                pgnMove, side, possibleMoves, board
            );
        }
        char specifier = moveMatcher.group(1).charAt(0);
        Optional<FileType> originFile = FileType.fromCharVal(specifier);
        Optional<RankType> originRank = RankType.fromIntVal(ConversionUtil.intFromChar(specifier));
        possibleMoves = possibleMoves.stream()
            .filter(possible -> originFile
                .map(f -> possible.getFrom().getFile() == f)
                .orElseGet(() -> originRank
                    .map(r -> possible.getFrom().getRank() == r)
                    .orElse(true)))
            .collect(Collectors.toList());
        if (possibleMoves.size() > 1) {
            throw ambiguousMoveException(pgnMove, side, possibleMoves, board);
        }
        return possibleMoves.stream()
            .findAny()
            .orElseThrow(() -> impossibleMoveException(pgnMove, side, board));
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
        Matcher turnStartMatcher = turnStartPattern.matcher(pgn);
        while (turnStartMatcher.find(cursor)) {
            cursor = turnStartMatcher.end();
            gameService.applyTurn(game, fromPgnTurn(
                pgn.substring(turnStartMatcher.start()),
                game.getBoardHistory().getLast()
            ));
        }
        return game;
    }

    public List<Turn> fromPgnTurnList(String pgnTurnList) {
        return gameFromPgn(pgnTurnList).getTurnHistory();
    }

    public Board boardFromPgn(String pgn) {
        return gameFromPgn(pgn).getBoardHistory().getLast();
    }
}
