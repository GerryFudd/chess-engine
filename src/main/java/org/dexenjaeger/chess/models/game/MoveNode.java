package org.dexenjaeger.chess.models.game;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.dexenjaeger.chess.models.Side;
import org.dexenjaeger.chess.models.board.Board;
import org.dexenjaeger.chess.models.moves.Move;
import org.dexenjaeger.chess.models.moves.ZeroMove;
import org.dexenjaeger.chess.services.BoardService;
import org.dexenjaeger.chess.utils.MergedIterable;
import org.dexenjaeger.chess.utils.Pair;

public class MoveNode {
    public static MoveNode opening() {
        return new MoveNode(0, new ZeroMove(Side.BLACK), BoardService.standardGameBoard());
    }

    @Getter
    private final int turnNumber;
    @Getter
    private final Move value;
    @Getter
    private final Board board;
    private final MoveNode parent;
    private final MoveNode firstAncestor;
    @Getter
    private final LinkedList<MoveNode> children = new LinkedList<>();
    @Getter
    @Setter
    private String commentary;

    public MoveNode(int turnNumber, Move value, Board board) {
        this(turnNumber, value, board, null, null, null);
    }
    public MoveNode(int turnNumber, Move value, Board board, MoveNode parent, MoveNode firstAncestor, String commentary) {
        this.turnNumber = turnNumber;
        this.value = value;
        this.board = board;
        this.parent = parent;
        this.firstAncestor = firstAncestor;
        this.commentary = commentary;
    }

    public Optional<MoveNode> getParent() {
        return Optional.ofNullable(parent);
    }

    public MoveNode getFirstAncestor() {
        return Optional.ofNullable(firstAncestor).orElse(this);
    }

    public MoveNode addChild(Move child, Board board) {
        return addChild(child, board, null);
    }
    public MoveNode addChild(Move child, Board board, String commentary) {
        MoveNode newChild = new MoveNode(value.getSide() == Side.WHITE ? turnNumber : turnNumber + 1, child, board, this, getFirstAncestor(), commentary);
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
        return otherNode != null
            && Objects.equals(otherNode.getValue(), value)
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

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof MoveNode) {
            if (this == other) {
                return true;
            }
            return equalsValueAndBoard((MoveNode) other)
                && getFirstAncestor().equalsAsChildren(((MoveNode) other).getFirstAncestor());
        }
        return false;
    }

    private String firstAncestorString() {
        return getFirstAncestor().asChildString(this);
    }

    private String valueString(MoveNode currentNode) {
        if (equalsValueAndBoard(currentNode)) {
            return String.format("<%s>", value);
        }
        return value.toString();
    }

    private Optional<String> childrenString(MoveNode currentNode) {
        if (children.isEmpty()) {
            return Optional.empty();
        }
        MoveNode firstChild = children.getFirst();
        List<MoveNode> remainingChildren = children.subList(1, children.size());
        if (remainingChildren.isEmpty()) {
            return Optional.of(" " + firstChild.asChildString(currentNode));
        }
        return Optional.of(
            " "
                + firstChild.valueString(currentNode)
                + " ("
                + remainingChildren.stream().map(c -> c.asChildString(currentNode)).collect(Collectors.joining(") ("))
                + ")"
                + firstChild.childrenString(currentNode).orElse("")
        );
    }

    private String asChildString(MoveNode currentNode) {
        return childrenString(currentNode)
            .map(cStr -> String.format("%s%s", valueString(currentNode), cStr))
            .orElse(valueString(currentNode));
    }

    public String toString() {
        List<String> parts = new LinkedList<>();
        parts.add(firstAncestorString());
        parts.add(" ");
        parts.add(board.toString());
        return String.join("", parts);
    }
}
