package org.dexenjaeger.chess.utils;

import java.util.Iterator;
import org.dexenjaeger.chess.models.board.File;
import org.dexenjaeger.chess.models.board.Rank;
import org.dexenjaeger.chess.utils.Pair;

public class SquareIterator implements Iterator<Pair<File, Rank>> {

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Pair<File, Rank> next() {
        return null;
    }
}
