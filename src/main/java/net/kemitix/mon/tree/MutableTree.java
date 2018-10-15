/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Paul Campbell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.kemitix.mon.tree;

import lombok.EqualsAndHashCode;
import net.kemitix.mon.maybe.Maybe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A mutable {@link Tree}.
 *
 * @param <T> the type of the objects help in the tree
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@EqualsAndHashCode
@SuppressWarnings("methodcount")
class MutableTree<T> implements Tree<T>, TreeMapper<T> {

    private final List<MutableTree<T>> mySubTrees = new ArrayList<>();

    private T item;

    /**
     * Create a new {@link MutableTree}.
     *
     * @param <B> the type of the {@link Tree}.
     *
     * @return the MutableTree
     */
    static <B> MutableTree<B> create() {
        return new MutableTree<>();
    }

    /**
     * Create a new mutable tree to hold object of a type.
     *
     * @param item the item for the leaf node
     * @param <B> the type of the object to be held in the tree
     *
     * @return a empty mutable tree
     */
    static <B> MutableTree<B> leaf(final B item) {
        return MutableTree.<B>create().set(item);
    }

    /**
     * Create a new mutable tree to hold the single item.
     *
     * @param item the item for the tree node
     * @param subtrees the subtrees of the tree node
     * @param <B> the type of the item
     * @return a leaf node of a generalised tree
     */
    static <B> MutableTree<B> of(final B item, final Collection<MutableTree<B>> subtrees) {
        return MutableTree.<B>create().set(item).subTrees(subtrees);
    }

    /**
     * Duplicate, or cast if possible, an existing Tree as a {@link MutableTree}.
     * @param tree the tree to duplicate/cast
     * @param <T> the type of the tree
     * @return the mutable tree
     */
    static <T> MutableTree<T> of(final Tree<T> tree) {
        if (tree instanceof MutableTree) {
            return (MutableTree<T>) tree;
        }
        final T item = tree.item().orElse(null);
        final List<MutableTree<T>> subtrees = tree.subTrees()
                .stream().map(MutableTree::of).collect(Collectors.toList());
        return MutableTree.of(item, subtrees);
    }

    @Override
    public <R> MutableTree<R> map(final Function<T, R> f) {
        final MutableTree<R> mutableTree = MutableTree.create();
        final List<MutableTree<R>> trees = subTreesAsMutable().stream()
                .map(subTree -> subTree.map(f))
                .collect(Collectors.toList());
        return mutableTree
                .set(f.apply(item))
                .subTrees(trees);
    }

    @Override
    public Maybe<T> item() {
        return Maybe.maybe(item);
    }

    /**
     * Sets the item of the tree.
     *
     * @param newItem the new item
     *
     * @return the tree
     */
    MutableTree<T> set(final T newItem) {
        this.item = newItem;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Tree<T>> subTrees() {
        return mySubTrees.stream()
                .<Tree<T>>map(Tree.class::cast)
                .collect(Collectors.toList());
    }

    /**
     * Sets the subTrees of the tree.
     *
     * @param subTrees the subtrees
     * @return the tree
     */
    MutableTree<T> subTrees(final Collection<MutableTree<T>> subTrees) {
        this.mySubTrees.clear();
        this.mySubTrees.addAll(subTrees);
        return this;
    }

    /**
     * Adds the subtree to the existing subtrees.
     *
     * @param subtree the subtree
     * @return the current tree
     */
    MutableTree<T> add(final Tree<T> subtree) {
        mySubTrees.add(MutableTree.of(subtree));
        return this;
    }

    /**
     * The mutable subtrees of the tree.
     *
     * @return a list of Trees
     */
    List<MutableTree<T>> subTreesAsMutable() {
        return mySubTrees;
    }
}
