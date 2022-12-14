package org.dexenjaeger.chess.io;

import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

public class PgnFileReader {
    public static final String NIMZO = "NimzoIndianDefenseKasparov.pgn";
    public static final String KOLMOV = "KolmovGambit.pgn";
    public static final String QGD_CLASSICAL = "QGDClassical.pgn";
    public static final String SAMPLE_FISHER_GAME = "MartnerVsFisher.pgn";

    @SneakyThrows
    public static String readOpening(String openingFileName) {
        File parentDir = new File("src/main/resources/openings");

        return IOUtils.toString(
            new File(parentDir, openingFileName).toURI(), StandardCharsets.UTF_8
        );
    }

    @SneakyThrows
    public static String readGame(String openingFileName) {
        File parentDir = new File("src/main/resources/games");

        return IOUtils.toString(
            new File(parentDir, openingFileName).toURI(), StandardCharsets.UTF_8
        );
    }
}
