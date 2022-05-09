package org.dexenjaeger.chess.models.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.utils.MergedIterable;
import org.dexenjaeger.chess.utils.Pair;

@AllArgsConstructor
public class MoveNode {
    @Getter
    private final int turnNumber;
    @Getter
    private final Move value;
    @Getter
    private final Board board;
    private final MoveNode parent;
    @Getter
    private final LinkedList<MoveNode> children = new LinkedList<>();
    @Getter
    @Setter
    private String commentary;

    public MoveNode(int turnNumber, Move value, Board board) {
        this(turnNumber, value, board, null);
    }
    public MoveNode(int turnNumber, Move value, Board board, MoveNode parent) {
        this(turnNumber, value, board, parent, null);
    }

    public Optional<MoveNode> getParent() {
        return Optional.ofNullable(parent);
    }

    public MoveNode addChild(Move child, Board board) {
        return addChild(child, board, null);
    }
    public MoveNode addChild(Move child, Board board, String commentary) {
        MoveNode newChild = new MoveNode(value.getSide() == Side.WHITE ? turnNumber : turnNumber + 1, child, board, this, commentary);
        children.add(newChild);
        return newChild;
    }

    private int hashChildren() {
        return children.stream().mapToInt(MoveNode::hashAsChild).sum();
    }

    private int hashAsChild() {
        return Objects.hash(value, board) + hashChildren();
    }

    private int hashParent() {
        return Optional.ofNullable(parent).map(MoveNode::hashAsParent).orElse(0);
    }

    private int hashAsParent() {
        return Objects.hash(value, board) + hashParent();
    }

    public int hashCode() {
        return Objects.hash(value, board)
            + hashChildren()
            + hashParent();
    }

    private boolean equalsValueAndBoard(MoveNode otherNode) {
        return Objects.equals(otherNode.getValue(), value)
            && Objects.equals(otherNode.getBoard(), board);
    }

    private boolean equalsAsChildren(MoveNode otherNode) {
        return equalsValueAndBoard(otherNode)
            && equalsChildren(otherNode.getChildren());
    }

    private boolean equalsChildren(LinkedList<MoveNode> otherChildren) {
        if (children.size() != otherChildren.size()) {
            return false;
        }
        for (Pair<MoveNode, MoveNode> childPair:new MergedIterable<>(children, otherChildren)) {
            if (!childPair.getLeft().equalsAsChildren(childPair.getRight())) {
                return false;
            }
        }
        return true;
    }

    private boolean equalsAsParent(MoveNode otherNode) {
        return equalsValueAndBoard(otherNode)
            && equalsParent(otherNode.getParent().orElse(null));
    }

    private boolean equalsParent(MoveNode otherParent) {
        if (parent == otherParent) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        return parent.equalsAsParent(otherParent);
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof MoveNode) {
            if (this == other) {
                return true;
            }
            return equalsValueAndBoard((MoveNode) other)
                && equalsParent(((MoveNode) other).getParent().orElse(null))
                && equalsChildren(((MoveNode) other).getChildren());
        }
        return false;
    }

    private Optional<String> parentString() {
        return getParent().map(MoveNode::asParentString);
    }

    private String asParentString() {
        return parentString()
            .map(pStr -> String.format("%s %s", pStr, value))
            .orElse(value.toString());
    }

    private Optional<String> childrenString() {
        if (children.isEmpty()) {
            return Optional.empty();
        }
        MoveNode firstChild = children.getFirst();
        List<MoveNode> remainingChildren = children.subList(1, children.size());
        if (remainingChildren.isEmpty()) {
            return Optional.of(firstChild.asChildString());
        }
        return Optional.of(
            firstChild.value.toString()
                + "... (" + children.subList(1, children.size()).stream().map(MoveNode::asChildString).collect(Collectors.joining(" "))
                + ")"
                + firstChild.childrenString().map(cStr -> " " + cStr).orElse(""));
    }

    private String asChildString() {
        return childrenString()
            .map(cStr -> String.format("%s %s", value, cStr))
            .orElse(value.toString());
    }

    public String toString() {
        List<String> parts = new LinkedList<>();
        parentString().ifPresent(parts::add);
        parts.add(value.toString());
        childrenString().ifPresent(parts::add);
        return String.join(" ", parts);
    }
}
