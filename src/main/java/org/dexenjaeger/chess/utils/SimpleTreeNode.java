package org.dexenjaeger.chess.utils;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dexenjaeger.chess.models.NodeValue;

public class SimpleTreeNode<T extends NodeValue> extends TreeNode<T, SimpleTreeNode<T>> {

    public SimpleTreeNode(T value) {
        this(value, null, null);
    }

    public SimpleTreeNode(T value, SimpleTreeNode<T> parent, SimpleTreeNode<T> firstAncestor) {
        super(value, parent, firstAncestor, new LinkedList<>());
    }

    @Override
    protected SimpleTreeNode<T> self() {
        return this;
    }

    @Override
    protected SimpleTreeNode<T> newInstance(T value, SimpleTreeNode<T> parent, SimpleTreeNode<T> firstAncestor) {
        return new SimpleTreeNode<>(value, parent, firstAncestor);
    }

    @Override
    public String toString() {
        return Stream.concat(
            Stream.of(
                getParent().map(SimpleTreeNode::getValue).map(NodeValue::toString).orElse(null),
                value.toString()
            ),
            children.stream().map(SimpleTreeNode::getValue).map(NodeValue::toString).map(s -> "(" + s + ")")
        ).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }
}
