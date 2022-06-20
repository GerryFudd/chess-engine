package org.dexenjaeger.chess.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import org.dexenjaeger.chess.models.NodeValue;

public class TreeNode<T extends NodeValue> {
    @Getter
    private final T value;
    private final TreeNode<T> parent;
    private final TreeNode<T> firstAncestor;
    @Getter
    private final LinkedList<TreeNode<T>> children = new LinkedList<>();

    public TreeNode(T value, TreeNode<T> parent, TreeNode<T> firstAncestor) {
        this.value = value;
        this.parent = parent;
        this.firstAncestor = firstAncestor;
    }

    public Optional<TreeNode<T>> getParent() {
        return Optional.ofNullable(parent);
    }

    public TreeNode<T> getFirstAncestor() {
        return Optional.ofNullable(firstAncestor).orElse(this);
    }

    public TreeNode<T> addChild(T childValue) {
        TreeNode<T> newChild = new TreeNode<>(
            childValue,
            this,
            getFirstAncestor()
        );
        children.add(newChild);
        return newChild;
    }

    private int hashChildren() {
        return children.stream().mapToInt(TreeNode::hashAsChild).sum();
    }

    private int hashAsChild() {
        return Objects.hash(value) + hashChildren();
    }

    public int hashCode() {
        return Objects.hash(value)
            + getFirstAncestor().hashAsChild();
    }

    public <U extends NodeValue> boolean equalsCurrentNodeValues(TreeNode<U> otherNode) {
        return otherNode != null
            && Objects.equals(otherNode.getValue(), value);
    }

    private <U extends NodeValue> boolean equalsAsChildren(TreeNode<U> otherNode) {
        return equalsCurrentNodeValues(otherNode)
            && equalsChildren(otherNode.getChildren());
    }

    private <U extends NodeValue> boolean equalsChildren(LinkedList<TreeNode<U>> otherChildren) {
        if (children.size() != otherChildren.size()) {
            return false;
        }
        for (Pair<TreeNode<T>, TreeNode<U>> childPair:new MergedIterable<>(children, otherChildren)) {
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
        if (other instanceof TreeNode) {
            if (this == other) {
                return true;
            }
            // Confirm that the two nodes represent the same move and that they have their
            // trees match if you start from the top.
            return equalsCurrentNodeValues((TreeNode<?>) other)
                && getFirstAncestor().equalsAsChildren(((TreeNode<?>) other).getFirstAncestor());
        }
        return false;
    }

    private String firstAncestorString() {
        return getFirstAncestor().asChildString(this);
    }

    private String valueString(TreeNode<T> currentNode) {
        if (equalsCurrentNodeValues(currentNode)) {
            return String.format("<%s>", getValue().shortString());
        }
        return getValue().shortString();
    }

    private Optional<String> childrenString(TreeNode<T> currentNode) {
        if (children.isEmpty()) {
            return Optional.empty();
        }
        TreeNode<T> firstChild = children.getFirst();
        List<TreeNode<T>> remainingChildren = children.subList(1, children.size());
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

    private String asChildString(TreeNode<T> currentNode) {
        return childrenString(currentNode)
            .map(cStr -> String.format("%s%s", valueString(currentNode), cStr))
            .orElse(valueString(currentNode));
    }

    public String toString() {
        return String.format(
            "%s %s",
            firstAncestorString(), value.longString()
        );
    }
}
