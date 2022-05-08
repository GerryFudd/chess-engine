package org.dexenjaeger.chess.models.board;

import java.util.Optional;
import lombok.Value;
import org.dexenjaeger.chess.utils.OptionalsUtil;
import org.dexenjaeger.chess.utils.Pair;

@Value
public class Square {
    FileType file;
    RankType rank;

    public String toString() {
        return String.format("%s%s", file, rank);
    }

    public Optional<Square> shift(Pair<Integer, Integer> direction) {
        return OptionalsUtil.merge(
            () -> file.shift(direction.getLeft()),
            () -> rank.shift(direction.getRight()),
            p -> new Square(p.getLeft(), p.getRight())
        );
    }
}
