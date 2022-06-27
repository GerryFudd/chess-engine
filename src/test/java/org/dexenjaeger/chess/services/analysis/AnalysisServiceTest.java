package org.dexenjaeger.chess.services.analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.dexenjaeger.chess.config.BindingConfig;
import org.dexenjaeger.chess.config.BindingHolder;
import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.game.Game;
import org.dexenjaeger.chess.models.game.GameSnapshot;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.services.BoardService;
import org.dexenjaeger.chess.services.FenService;
import org.dexenjaeger.chess.services.GameService;
import org.dexenjaeger.chess.services.PgnService;
import org.dexenjaeger.chess.utils.HashablePrintableTreeNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AnalysisServiceTest {
    private final ServiceProvider serviceProvider = new ServiceProvider(BindingHolder.init(
        BindingConfig.builder()
            .activityWeight(new BigDecimal("0.10"))
            .piecesWeight(new BigDecimal("0.40"))
            .build()
    ));
    private final AnalysisService analysisService = serviceProvider.getInstance(AnalysisService.class);
    private final FenService fenService = serviceProvider.getInstance(FenService.class);
    private final PgnService pgnService = serviceProvider.getInstance(PgnService.class);
    private final GameService gameService = serviceProvider.getInstance(GameService.class);

    @Test
    void getMaterialScore_startingPosition() {
        assertEquals(
            0,
            analysisService.getMaterialScore(BoardService.standardGameBoard())
        );
    }

    @Test
    void getMaterialScore_asymmetricPosition() {
        Board board = fenService.readPieceLocations("r2qnrk1/pp3pbp/2nN2p1/4Pb2/2P2P2/4B3/PP4PP/R2QKBNR");
        assertEquals(
            2,
            analysisService.getMaterialScore(board)
        );
    }

    @Test
    void getPieceActivityScore_startingPosition() {
        assertEquals(
            0,
            analysisService.getPieceActivityScore(BoardService.standardGameBoard())
        );
    }

    @Test
    void getPieceActivityScore_asymmetricPosition() {
        Board board = fenService.readPieceLocations("r2qnrk1/pp3pbp/2nN2p1/4Pb2/2P2P2/4B3/PP4PP/R2QKBNR");
        assertEquals(
            3,
            analysisService.getPieceActivityScore(board)
        );
    }

    @Test
    void getRelativeScore_asymmetricPosition() {
        Board board = fenService.readPieceLocations("r2qnrk1/pp3pbp/2nN2p1/4Pb2/2P2P2/4B3/PP4PP/R2QKBNR");
        assertEquals(
            new BigDecimal("2.80"),
            analysisService.getScore(board)
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
            gameService.applyMove(gameService.detachGameState(game), solution).getGameNode().getFirstAncestor(),
            analysisService.findForcedCheckmate(game, 1).orElseThrow()
        );
    }

    @ParameterizedTest
    @CsvSource({
        "2r1r1k1/5ppp/8/8/4R3/8/5PPP/4R1K1 w - - 15 38,Re8 Rxe8 Rxe8",
        "5r2/2R3b1/P4r2/2p2Nkp/2b3pN/6P1/4PP2/6K1 w - - 15 38,Rg7 Rg6 Rxg6",
        "8/7k/p6p/4B1p1/3P1pQ1/7P/P5PK/5q2 w - - 15 38,Qf5 Kg8 Qg6 Kf8 Bd6",
    })
    void findForcedCheckmate(String fen, String solutionPgns) {
        Game game = fenService.getGame(fen);
        Game detachedGame = gameService.detachGameState(game);
        for (String solutionPgn:solutionPgns.split(" ")) {
            Move newMove = pgnService.fromPgnMove(solutionPgn, gameService.currentSide(detachedGame), detachedGame.getCurrentBoard());
            gameService.applyMove(detachedGame, newMove);
        }
        HashablePrintableTreeNode<GameSnapshot> expected = detachedGame.getGameNode().getFirstAncestor();
        HashablePrintableTreeNode<GameSnapshot> result = analysisService.findForcedCheckmate(game, 3).orElseThrow();
        assertEquals(
            expected,
            result
        );
    }

    @Test
    void findForcedCheckmateForQueenVsNothing() {
        Game game = fenService.getGame("4k3/Q7/8/8/8/4K3/8/8 w - - 0 1");
        HashablePrintableTreeNode<GameSnapshot> result = analysisService.findForcedCheckmate(game, 6).orElseThrow();
        assertEquals("", result.toString());
    }
}
