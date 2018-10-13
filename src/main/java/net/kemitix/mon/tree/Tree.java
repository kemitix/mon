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

import net.kemitix.mon.Functor;
import net.kemitix.mon.maybe.Maybe;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * A tree of objects.
 *
 * @param <T> the type of the objects help in the tree
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public interface Tree<T> extends Functor<T, Tree<?>> {

    /**
     * Create a new generalised tree to hold object of a type.
     *
     * @param item the item for the leaf node
     * @param <R> the type of the object to be held in the tree
     *
     * @return a empty generalised tree
     */
    public static <R> Tree<R> leaf(final R item) {
        return new GeneralisedTree<>(item, Collections.emptyList());
    }

    /**
     * Create a new generalised tree to hold the single item.
     *
     * @param item the item for the tree node
     * @param subtrees the subtrees of the tree node
     * @param <R> the type of the item
     * @return a leaf node of a generalised tree
     */
    public static <R> Tree<R> of(final R item, final Collection<Tree<R>> subtrees) {
        return new GeneralisedTree<>(item, subtrees);
    }

    /**
     * Create a new {@link TreeBuilder} starting with an empty tree.
     *
     * @param type the type
     * @param <B> the type of the tree
     *
     * @return a TreeBuilder
     */
    public static <B> TreeBuilder<B> builder(final Class<B> type) {
        return new MutableTreeBuilder<>();
    }

    /**
     * Create a new {@link TreeBuilder} for the given tree.
     *
     * @param tree the tree to build upon
     * @param <B> the type of the tree
     *
     * @return a TreeBuilder
     */
    public static <B> TreeBuilder<B> builder(final MutableTree<B> tree) {
        return new MutableTreeBuilder<>(tree);
    }

    @Override
    public abstract <R> Tree<R> map(Function<T, R> f);

    /**
     * Return the item within the node of the tree, if present.
     *
     * @return a Maybe containing the item
     */
    public abstract Maybe<T> item();

    /**
     * Count the number of item in the tree, including subtrees.
     *
     * @return the sum of the subtrees, plus 1 if there is an item in this node
     */
    public default int count() {
        return item().matchValue(x -> 1, () -> 0)
                + subTrees().stream().mapToInt(Tree::count).sum();
    }

    /**
     * The subtrees of the tree.
     *
     * @return a list of Trees
     */
    public abstract List<Tree<T>> subTrees();
}
