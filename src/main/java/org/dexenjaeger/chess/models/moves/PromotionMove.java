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
public class PromotionMove implements NormalMove {
    @Getter
    private final Side side;
    private final FileType fileFrom;
    private final FileType fileTo;
    @Getter
    private final PieceType result;

    public PromotionMove(Side side, FileType file, PieceType result) {
        this(side, file, file, result);
    }

    public PromotionMove(Side side, FileType fileFrom, FileType fileTo, PieceType result) {
        this.side = side;
        this.fileFrom = fileFrom;
        this.fileTo = fileTo;
        this.result = result;
    }

    @Override
    public Piece getPiece() {
        return new Piece(side, PieceType.PAWN);
    }

    public PieceType getType() {
        return PieceType.PAWN;
    }

    public Square getFrom() {
        return new Square(fileFrom, side == Side.WHITE ? RankType.SEVEN : RankType.TWO);
    }

    public Square getTo() {
        return new Square(fileTo, side == Side.WHITE ? RankType.EIGHT : RankType.ONE);
    }

    public String toString() {
        return String.format("%s%s%s=%s", new Piece(side, PieceType.PAWN), getFrom(), getTo(), getResult().getRepresentation());
    }
}
