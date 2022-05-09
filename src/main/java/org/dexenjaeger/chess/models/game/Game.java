package org.dexenjaeger.chess.models.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.TagType;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Castle;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.ZeroMove;
import org.dexenjaeger.chess.utils.Pair;

@Getter
@EqualsAndHashCode
public class Game {
    @Getter(AccessLevel.NONE)
    private final Map<TagType, String> standardTags = new HashMap<>();
    @Getter(AccessLevel.NONE)
    private final Map<String, String> nonStandardTags = new LinkedHashMap<>();
    private final Set<Castle> castlingRights = new HashSet<>();
    private final MoveNode moveHistory;
    @Getter(AccessLevel.NONE)
    private MoveNode currentMove;
    @Getter(AccessLevel.NONE)
    private MoveNode lastMove;

    private Game(MoveNode moveHistory) {
        this.moveHistory = moveHistory;
        currentMove = moveHistory;
        lastMove = moveHistory;
    }

    public static Game init(Board board) {
        return init(0, Side.WHITE, board);
    }
    public static Game init(int turnNumber, Side side, Board board) {
        return new Game(new MoveNode(
            turnNumber, new ZeroMove(side.other()), board
        ));
    }

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

    public Game addMove(Move move, Board board) {
        currentMove = currentMove.addChild(move, board);
        if (!lastMove.getChildren().isEmpty()) {
            lastMove = lastMove.getChildren().getFirst();
        }
        return this;
    }

    public Move getLastMove() {
        return lastMove.getValue();
    }

    public Move getCurrentMove() {
        return currentMove.getValue();
    }

    public Board getCurrentBoard() {
        return currentMove.getBoard();
    }

    public Board getLastBoard() {
        return lastMove.getBoard();
    }

    public Game parentMove() {
        currentMove = currentMove.getParent().orElse(currentMove);
        return this;
    }

    public Game childMove(int i) {
        if (i < currentMove.getChildren().size()) {
            currentMove = currentMove.getChildren().get(i);
        }
        return this;
    }

    public Game goToLastMove() {
        currentMove = lastMove;
        return this;
    }

    public String toString() {
        return String.format(
            "Game(moves=\"%s\", castlingRights=[%s])",
            currentMove, castlingRights.stream().map(Castle::toString).sorted().collect(Collectors.joining())
        );
    }
}
