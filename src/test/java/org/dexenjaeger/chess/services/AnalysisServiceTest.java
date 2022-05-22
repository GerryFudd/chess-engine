package org.dexenjaeger.chess.services;

import static org.dexenjaeger.chess.models.Side.BLACK;
import static org.dexenjaeger.chess.models.Side.WHITE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.dexenjaeger.chess.config.ServiceProvider;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AnalysisServiceTest {
    private final ServiceProvider serviceProvider = new ServiceProvider();
    private final AnalysisService analysisService = serviceProvider.getInstance(AnalysisService.class);
    private final FenService fenService = serviceProvider.getInstance(FenService.class);

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
}