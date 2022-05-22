package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.Side.BLACK;
import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.moves.Move;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class AnalysisServiceTest {
    private final ServiceProvider serviceProvider = new ServiceProvider();
    private final AnalysisService analysisService = serviceProvider.getInstance(AnalysisService.class);
    private final FenService fenService = serviceProvider.getInstance(FenService.class);
    private final PgnService pgnService = serviceProvider.getInstance(PgnService.class);
    private final GameService gameService = serviceProvider.getInstance(GameService.class);

    @ParameterizedTest
    @EnumSource(Side.class)
    void getMaterialScore_startingPosition(Side side) {
        assertEquals(
            39,
            analysisService.getMaterialScore(BoardService.standardGameBoard(), side)
        );
    }

    @Test
    void getMaterialScore_asymmetricPosition() {
        Board board = fenService.readPieceLocations("r2qnrk1/pp3pbp/2nN2p1/4Pb2/2P2P2/4B3/PP4PP/R2QKBNR");
        assertEquals(
            38,
            analysisService.getMaterialScore(board, WHITE)
        );
        assertEquals(
            36,
            analysisService.getMaterialScore(board, BLACK)
        );
    }

    @ParameterizedTest
    @EnumSource(Side.class)
    void getPieceActivityScore_startingPosition(Side side) {
        assertEquals(
            20,
            analysisService.getPieceActivityScore(BoardService.standardGameBoard(), side)
        );
    }

    @Test
    void getPieceActivityScore_asymmetricPosition() {
        Board board = fenService.readPieceLocations("r2qnrk1/pp3pbp/2nN2p1/4Pb2/2P2P2/4B3/PP4PP/R2QKBNR");
        assertEquals(
            47,
            analysisService.getPieceActivityScore(board, WHITE)
        );
        assertEquals(
            44,
            analysisService.getPieceActivityScore(board, BLACK)
        );
    }

    @Test
    void getRelativeMaterialScore_startingPosition() {
        assertEquals(
            0,
            analysisService.getRelativeMaterialScore(BoardService.standardGameBoard())
        );
    }

    @Test
    void getRelativeMaterialScore_asymmetricPosition() {
        Board board = fenService.readPieceLocations("r2qnrk1/pp3pbp/2nN2p1/4Pb2/2P2P2/4B3/PP4PP/R2QKBNR");
        assertEquals(
            2,
            analysisService.getRelativeMaterialScore(board)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "3qk3/3ppp2/8/8/2B5/2K2Q2/8/8 w - - 15 38,Qf7",
        "6rk/6pp/7P/6N1/6K1/8/8/8 w - - 15 38,Nf7",
        "R7/8/7k/2r5/5n2/8/6Q1/5K2 w - - 13 49,Rh8",
        "2rb4/2k5/5N2/1Q6/3K4/8/8/8 w - - 13 49,Ne8",
    })
    void findCheckmateInOne(String fen, String solutionPgn) {
        Game game = fenService.getGame(fen);
        Move solution = pgnService.fromPgnMove(solutionPgn, gameService.currentSide(game), game.getCurrentBoard());
        assertEquals(
            Set.of(solution),
            analysisService.findCheckmateInOne(game)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "3qk3/3ppp2/8/8/2B5/2K2Q2/8/8 w - - 15 38,Qf7",
        "6rk/6pp/7P/6N1/6K1/8/8/8 w - - 15 38,Nf7",
        "R7/8/7k/2r5/5n2/8/6Q1/5K2 w - - 13 49,Rh8",
        "2rb4/2k5/5N2/1Q6/3K4/8/8/8 w - - 13 49,Ne8",
    })
    void findForcedCheckmate_inOne(String fen, String solutionPgn) {
        Game game = fenService.getGame(fen);
        Move solution = pgnService.fromPgnMove(solutionPgn, gameService.currentSide(game), game.getCurrentBoard());
        assertEquals(
            gameService.applyMove(analysisService.detachGameState(game), solution).getMoveSummary().getFirstAncestor(),
            analysisService.findForcedCheckmate(game, 1)
        );
    }

    @ParameterizedTest
    @CsvSource({
        "2r1r1k1/5ppp/8/8/4R3/8/5PPP/4R1K1 w - - 15 38,Re8 Rxe8 Rxe8",
        "5r2/2R3b1/P4r2/2p2Nkp/2b3pN/6P1/4PP2/6K1 w - - 15 38,Rg7 Rg6 Rxg6",
    })
    void findForcedCheckmate(String fen, String solutionPgns) {
        Game game = fenService.getGame(fen);
        Game detachedGame = analysisService.detachGameState(game);
        for (String solutionPgn:solutionPgns.split(" ")) {
            Move newMove = pgnService.fromPgnMove(solutionPgn, gameService.currentSide(detachedGame), detachedGame.getCurrentBoard());
            gameService.applyMove(detachedGame, newMove);
        }
        assertEquals(
            detachedGame.getMoveSummary().getFirstAncestor(),
            analysisService.findForcedCheckmate(game, 2)
        );
    }
}