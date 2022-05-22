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
    private MoveNode moveSummary;

    private Game(MoveNode moveSummary) {
        this.moveSummary = moveSummary;
    }

    public static Game init(Board board) {
        return init(1, Side.WHITE, board);
    }
    public static Game init(int turnNumber, Side side, Board board) {
        return new Game(new MoveNode(
            turnNumber - 1, new ZeroMove(side.other()), board
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

    public Move getPreviousMove() {
        return moveSummary.getValue();
    }

    public List<Move> getAttemptedMoves() {
        return moveSummary.getChildren().stream().map(MoveNode::getValue).collect(Collectors.toList());
    }

    public Board getCurrentBoard() {
        return moveSummary.getBoard();
    }

    public Game addMove(Move move, Board board) {
        moveSummary = moveSummary.addChild(move, board);
        return this;
    }

    public Game goToParentMove() {
        moveSummary = moveSummary.getParent().orElse(moveSummary);
        return this;
    }

    public Game goToAttemptedMove(Move childMove) {
        moveSummary = moveSummary.getChildren()
            .stream()
            .filter(ch -> ch.getValue().equals(childMove))
            .findAny()
            .orElse(moveSummary);
        return this;
    }

    public Game goToNextMainLineMove() {
        if (!moveSummary.getChildren().isEmpty()) {
            moveSummary = moveSummary.getChildren().getFirst();
        }
        return this;
    }

    public Game goToFirstMove() {
        moveSummary = moveSummary.getFirstAncestor();
        return this;
    }

    public Game goToLastMove() {
        goToFirstMove();
        while (!getAttemptedMoves().isEmpty()) {
            goToNextMainLineMove();
        }
        return this;
    }

    public String toString() {
        return String.format(
            "Game(moves=\"%s\", castlingRights=[%s])",
            moveSummary.getFirstAncestor(), castlingRights.stream().map(Castle::toFen).sorted().collect(Collectors.joining())
        );
    }
}
