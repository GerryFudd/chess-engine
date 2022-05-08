package org.dexenjaeger.chess.services.pgn;

import java.util.Optional;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pgn.PgnMove;
import org.dexenjaeger.chess.models.pieces.PieceType;
import org.dexenjaeger.chess.services.ServiceException;

public class PgnMoveExtractor {
    private final PgnMove pgnMove;

    public PgnMoveExtractor(PgnMove pgnMove) {
        this.pgnMove = pgnMove;
    }

    public Optional<FileType> getOriginFile() {
        return pgnMove.getFileFrom().flatMap(FileType::fromString);
    }

    public Optional<RankType> getOriginRank() {
        return pgnMove.getRankFrom().flatMap(RankType::fromString);
    }

    public PieceType getPieceType() {
        switch (pgnMove.getPiece().orElse("")) {
            case "R":
                return PieceType.ROOK;
            case "N":
                return PieceType.KNIGHT;
            case "B":
                return PieceType.BISHOP;
            case "Q":
                return PieceType.QUEEN;
            case "K":
                return PieceType.KING;
            default:
                return PieceType.PAWN;
        }
    }

    public Square getTarget() {
        String targetFile = pgnMove.getTarget().substring(0, 1);
        FileType file = FileType.fromString(targetFile)
            .orElseThrow(() -> new ServiceException(String.format("%s is not a file value.", targetFile)));

        String targetRank = pgnMove.getTarget().substring(1);
        RankType rank = RankType.fromString(targetRank)
            .orElseThrow(() -> new ServiceException(String.format("%s is not a rank value.", targetRank)));
        return new Square(file, rank);
    }
}
