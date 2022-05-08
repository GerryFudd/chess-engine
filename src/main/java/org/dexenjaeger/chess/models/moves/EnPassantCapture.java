package org.dexenjaeger.chess.models.moves;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.FileType;
import org.dexenjaeger.chess.models.board.RankType;
import org.dexenjaeger.chess.models.board.Square;
import org.dexenjaeger.chess.models.pieces.Piece;
import org.dexenjaeger.chess.models.pieces.PieceType;

@EqualsAndHashCode
public class EnPassantCapture implements SinglePieceMove {
    @Getter
    private final Side side;
    private final FileType fileFrom;
    private final FileType fileTo;

    public EnPassantCapture(Side side, FileType fileFrom, FileType fileTo) {
        this.side = side;
        this.fileFrom = fileFrom;
        this.fileTo = fileTo;
    }

    public Piece getPiece() {
        return new Piece(side, PieceType.PAWN);
    }

    @Override
    public PieceType getType() {
        return PieceType.PAWN;
    }

    public Square getFrom() {
        return new Square(
            fileFrom, side == Side.WHITE ? RankType.FIVE : RankType.FOUR
        );
    }

    public Square getTo() {
        return new Square(
            fileTo, side == Side.WHITE ? RankType.SIX : RankType.THREE
        );
    }

    public Square getCapturedSquare() {
        return new Square(
            fileTo, side == Side.WHITE ? RankType.FIVE : RankType.FOUR
        );
    }

    public String toString() {
        return String.format(
            "%sx%s%s",
            fileFrom, fileTo, getTo().getRank()
        );
    }
}
