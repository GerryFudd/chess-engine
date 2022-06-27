package org.dexenjaeger.chess.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.dexenjaeger.chess.models.PrintableNodeValue;

public class HashablePrintableTreeNode<T extends PrintableNodeValue> extends TreeNode<T, HashablePrintableTreeNode<T>> {

    public HashablePrintableTreeNode(T value, HashablePrintableTreeNode<T> parent, HashablePrintableTreeNode<T> firstAncestor) {
        super(value, parent, firstAncestor, new LinkedList<>());
    }

    private int hashChildren() {
        return children.stream().mapToInt(HashablePrintableTreeNode::hashAsChild).sum();
    }

    private int hashAsChild() {
        return Objects.hash(value) + hashChildren();
    }

    public int hashCode() {
        return Objects.hash(value)
            + getFirstAncestor().hashAsChild();
    }

    public <U extends PrintableNodeValue> boolean equalsCurrentNodeValues(HashablePrintableTreeNode<U> otherNode) {
        return otherNode != null
            && Objects.equals(otherNode.getValue(), value);
    }

    private <U extends PrintableNodeValue> boolean equalsAsChildren(HashablePrintableTreeNode<U> otherNode) {
        return equalsCurrentNodeValues(otherNode)
            && equalsChildren(otherNode.getChildren());
    }

    private <U extends PrintableNodeValue> boolean equalsChildren(LinkedList<HashablePrintableTreeNode<U>> otherChildren) {
        if (children.size() != otherChildren.size()) {
            return false;
        }
        for (Pair<HashablePrintableTreeNode<T>, HashablePrintableTreeNode<U>> childPair:new MergedIterable<>(children, otherChildren)) {
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
        if (other instanceof HashablePrintableTreeNode) {
            if (this == other) {
                return true;
            }
            // Confirm that the two nodes represent the same move and that they have their
            // trees match if you start from the top.
            return equalsCurrentNodeValues((HashablePrintableTreeNode<?>) other)
                && getFirstAncestor().equalsAsChildren(((HashablePrintableTreeNode<?>) other).getFirstAncestor());
        }
        return false;
    }

    @Override
    protected HashablePrintableTreeNode<T> self() {
        return this;
    }

    @Override
    protected HashablePrintableTreeNode<T> newInstance(T value, HashablePrintableTreeNode<T> parent, HashablePrintableTreeNode<T> firstAncestor) {
        return new HashablePrintableTreeNode<>(value, parent, firstAncestor);
    }

    private String firstAncestorString() {
        return getFirstAncestor().asChildString(this);
    }

    private String valueString(HashablePrintableTreeNode<T> currentNode) {
        if (equalsCurrentNodeValues(currentNode)) {
            return String.format("<%s>", getValue().shortString());
        }
        return getValue().shortString();
    }

    private Optional<String> childrenString(HashablePrintableTreeNode<T> currentNode) {
        if (children.isEmpty()) {
            return Optional.empty();
        }
        HashablePrintableTreeNode<T> firstChild = getChildren().getFirst();
        List<HashablePrintableTreeNode<T>> remainingChildren = children.subList(1, children.size());
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

    private String asChildString(HashablePrintableTreeNode<T> currentNode) {
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
