package org.dexenjaeger.chess.models.pgn;

import java.util.Optional;
import lombok.Getter;

public class PgnMove {
    private final String piece;
    private final String fileFrom;
    private final String rankFrom;
    @Getter
    private final String target;


    public PgnMove(String piece, String fileFrom, String rankFrom, String target) {
        this.piece = piece;
        this.fileFrom = fileFrom;
        this.rankFrom = rankFrom;
        this.target = target;
    }

    public Optional<String> getPiece() {
        return Optional.ofNullable(piece);
    }

    public Optional<String> getFileFrom() {
        return Optional.ofNullable(fileFrom);
    }

    public Optional<String> getRankFrom() {
        return Optional.ofNullable(rankFrom);
    }
}
