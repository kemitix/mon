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
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A mutable {@link Tree}.
 *
 * @param <T> the type of the objects help in the tree
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@ToString
@EqualsAndHashCode
class MutableTree<T> implements Tree<T>, TreeMapper<T> {

    private final List<Tree<T>> mySubTrees = new ArrayList<>();

    private T item;

    /**
     * Create a new {@link MutableTree}.
     *
     * @param <B> the type of the {@link Tree}.
     *
     * @return the MutableTree
     */
    public static <B> MutableTree<B> create() {
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
    public static <B> MutableTree<B> leaf(final B item) {
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
    public static <B> MutableTree<B> of(final B item, final Collection<Tree<B>> subtrees) {
        return MutableTree.<B>create().set(item).subTrees(subtrees);
    }

    @Override
    public <R> MutableTree<R> map(final Function<T, R> f) {
        final MutableTree<R> mutableTree = MutableTree.create();
        return mutableTree
                .set(f.apply(item))
                .subTrees(mapTrees(f, subTrees()));
    }

    @Override
    public Optional<T> item() {
        return Optional.ofNullable(item);
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
    public List<Tree<T>> subTrees() {
        return mySubTrees;
    }

    /**
     * Sets the subTrees of the tree.
     *
     * @param subTrees the subtrees
     * @return the tree
     */
    MutableTree<T> subTrees(final Collection<Tree<T>> subTrees) {
        this.mySubTrees.clear();
        this.mySubTrees.addAll(subTrees);
        return this;
    }

}
