package org.dexenjaeger.chess.utils;

import java.util.LinkedList;
import java.util.Optional;
import lombok.Getter;
import org.dexenjaeger.chess.models.NodeValue;

public abstract class TreeNode<T extends NodeValue, U extends TreeNode<T, U>> {
    @Getter
    protected final T value;
    protected final U parent;
    protected final U firstAncestor;
    @Getter
    protected final LinkedList<U> children;

    public TreeNode(T value) {
        this(value, null, null, new LinkedList<>());
    }

    public TreeNode(T value, U parent, U firstAncestor, LinkedList<U> children) {
        this.value = value;
        this.parent = parent;
        this.firstAncestor = firstAncestor;
        this.children = children;
    }

    protected abstract U self();
    protected abstract U newInstance(T value, U parent, U firstAncestor);

    public Optional<U> getParent() {
        return Optional.ofNullable(parent);
    }

    public U getFirstAncestor() {
        return Optional.ofNullable(firstAncestor).orElse(self());
    }

    public Optional<U> getNextSibling() {
        return getParent()
            .flatMap(parent -> {
                int i = parent.getChildren().indexOf(self());
                if (i + 1 >= parent.getChildren().size()) {
                    return Optional.empty();
                }
                return Optional.of(parent.getChildren().get(i + 1));
            });
    }

    public U addChild(T childValue) {
        U newChild = newInstance(
            childValue,
            self(),
            getFirstAncestor()
        );
        children.add(newChild);
        return newChild;
    }

    public U addBranch(U childSource) {
        U child = newInstance(
            childSource.getValue(),
            self(),
            getFirstAncestor()
        );
        children.add(child);
        addAllChildren(childSource, child, child.getValue());
        return child;
    }

    public U copy() {
        return addAllChildren(getFirstAncestor(), newInstance(getFirstAncestor().getValue(), null, null), getValue());
    }

    public U replaceNode(U newNode) {
        U result = getParent()
            .map(p -> {
                p.getChildren().remove(self());
                return p.addChild(newNode.getValue());
            })
            .orElse(newInstance(newNode.getValue(), null, null));

        return addAllChildren(newNode, result, newNode.getValue());
    }

    private U addAllChildren(U source, U target, T resultValue) {
        U sourceCursor = source;
        U cursor = target;
        U result = cursor;

        while (
            (
                (
                    cursor.getParent().isPresent()
                    && !cursor.getValue().equals(source.getValue())
                )
                && (
                    sourceCursor.getParent().isPresent()
                    && !sourceCursor.getValue().equals(source.getValue())
                )
            )
                || sourceCursor.getChildren().size() > cursor.getChildren().size()
        ) {
            if (sourceCursor.getChildren().size() == cursor.getChildren().size()) {
                cursor = cursor.getParent().get();
                sourceCursor = sourceCursor.getParent().get();
                if (!result.getValue().equals(resultValue)) {
                    result = result.getParent().get();
                }
            } else {
                sourceCursor = sourceCursor.getChildren().get(cursor.getChildren().size());
                cursor = cursor.addChild(sourceCursor.getValue());
                if (!result.getValue().equals(resultValue)) {
                    result = result.getChildren().getLast();
                }
            }
        }
        return result;
    }
}
