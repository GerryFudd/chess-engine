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
import org.dexenjaeger.chess.utils.HashablePrintableTreeNode;
import org.dexenjaeger.chess.utils.Pair;

@Getter
@EqualsAndHashCode
public class Game {
    @Getter(AccessLevel.NONE)
    private final Map<TagType, String> standardTags = new HashMap<>();
    @Getter(AccessLevel.NONE)
    private final Map<String, String> nonStandardTags = new LinkedHashMap<>();
    private final Set<Castle> castlingRights = new HashSet<>();
    private HashablePrintableTreeNode<GameSnapshot> gameNode;

    private Game(HashablePrintableTreeNode<GameSnapshot> gameNode) {
        this.gameNode = gameNode;
    }

    public static Game init(HashablePrintableTreeNode<GameSnapshot> gameNode) {
        return new Game(gameNode.copy());
    }

    public static Game init(Board board) {
        return init(1, Side.WHITE, board, 0);
    }

    public static Game init(
        int turnNumber, Side side, Board board,
        int fiftyMoveRuleCounter
    ) {
        return init(
            new GameSnapshot(
                side == Side.WHITE ? turnNumber - 1 : turnNumber, new ZeroMove(side.other()), board, fiftyMoveRuleCounter, null
            )
        );
    }

    public static Game init(
        GameSnapshot moveSummary
    ) {
        return new Game(
            new HashablePrintableTreeNode<>(
                moveSummary, null, null
            )
        );
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
        return gameNode.getValue().getMove();
    }

    public List<Move> getAttemptedMoves() {
        return gameNode.getChildren().stream()
            .map(HashablePrintableTreeNode::getValue)
            .map(GameSnapshot::getMove)
            .collect(Collectors.toList());
    }

    public Board getCurrentBoard() {
        return gameNode.getValue().getBoard();
    }

    public Game addMove(GameSnapshot moveSummary) {
        gameNode = gameNode.addChild(moveSummary);
        return this;
    }

    public Game goToParentMove() {
        gameNode = gameNode.getParent().orElse(gameNode);
        return this;
    }

    public void goToAttemptedMove(Move childMove) {
        gameNode = gameNode.getChildren()
            .stream()
            .filter(ch -> ch.getValue().getMove().equals(childMove))
            .findAny()
            .orElse(gameNode);
    }

    public void goToNextMainLineMove() {
        if (!gameNode.getChildren().isEmpty()) {
            gameNode = gameNode.getChildren().getFirst();
        }
    }

    public Game goToFirstMove() {
        gameNode = gameNode.getFirstAncestor();
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
            gameNode, castlingRights.stream().map(Castle::toFen).sorted().collect(Collectors.joining())
        );
    }
}
