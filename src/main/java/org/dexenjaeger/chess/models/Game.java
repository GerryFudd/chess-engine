package org.dexenjaeger.chess.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.Turn;
import org.dexenjaeger.chess.utils.Pair;

@Getter
@EqualsAndHashCode
@ToString
public class Game {
    @Getter(AccessLevel.NONE)
    private final Map<TagType, String> standardTags = new HashMap<>();
    @Getter(AccessLevel.NONE)
    private final Map<String, String> nonStandardTags = new LinkedHashMap<>();
    private final Set<Castle> castlingRights = new HashSet<>();
    private final LinkedList<Turn> turnHistory = new LinkedList<>();
    private final LinkedList<Board> boardHistory = new LinkedList<>();

    public void addTag(TagType tagType, String tag) {
        standardTags.put(tagType, tag);
    }

    public void addTag(String tagType, String tag) {
        nonStandardTags.put(tagType, tag);
    }

    public List<Pair<String, String>> getTags() {
        List<Pair<String, String>> result = Stream.of(TagType.values())
            .map(
                type -> Optional.ofNullable(standardTags.get(type))
                    .map(t -> new Pair<>(type.getDisplayName(), t))
            )
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
        for (Entry<String, String> nonStandardTag:nonStandardTags.entrySet()) {
            result.add(new Pair<>(nonStandardTag.getKey(), nonStandardTag.getValue()));
        }
        return result;
    }

    public Game addCastlingRights(Collection<Castle> castleCollection) {
        castlingRights.addAll(castleCollection);
        return this;
    }

    public Game addTurn(Turn turn) {
        turnHistory.add(turn);
        return this;
    }

    public Game addBoard(Board board) {
        boardHistory.add(board);
        return this;
    }

    public Optional<Turn> lastTurn() {
        if (turnHistory.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(turnHistory.getLast());
    }

    public Board currentBoard() {
        return boardHistory.isEmpty() ? null : boardHistory.getLast();
    }
}
