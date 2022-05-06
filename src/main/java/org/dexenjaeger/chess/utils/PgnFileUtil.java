package org.dexenjaeger.chess.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

public class PgnFileUtil {
    @SneakyThrows
    public static String readOpening(String openingFileName) {
        File parentDir = new File("src/main/resources/openings");

        return IOUtils.toString(
            new File(parentDir, openingFileName).toURI(), StandardCharsets.UTF_8
        );
    }
}
