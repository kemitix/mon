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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A generic tree of trees and objects.
 *
 * <p>Each node may contain between 0 and n objects.</p>
 *
 * @param <T> the type of the objects help in the tree
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@EqualsAndHashCode
class GeneralisedTree<T> implements Tree<T> {

    private final T item;
    private final List<Tree<T>> subTrees;

    /**
     * Creates a new tree.
     *
     * @param item the item of this node
     * @param subTrees the sub-trees under this node
     */
    GeneralisedTree(final T item, final Collection<Tree<T>> subTrees) {
        this.item = item;
        this.subTrees = Collections.unmodifiableList(new ArrayList<>(subTrees));
    }

    /**
     * Maps the tree using the function onto a new tree.
     *
     * @param f   the function to apply
     * @param <R> the type of object held in the resulting tree
     * @return a tree
     */
    @Override
    public <R> Tree<R> map(final Function<T, R> f) {
        return Tree.of(f.apply(item), mapSubTrees(f));
    }

    @Override
    public Optional<T> item() {
        return Optional.ofNullable(item);
    }

    private <R> List<Tree<R>> mapSubTrees(final Function<T, R> f) {
        return subTrees.stream()
                .map(subTree -> subTree.map(f::apply))
                .collect(Collectors.toList());
    }

    /**
     * Counts the number of items in the subtree.
     *
     * @return the sum of the subtrees, plus 1 if there is an item in this node
     */
    @Override
    @SuppressWarnings("avoidinlineconditionals")
    public int count() {
        return (item != null ? 1 : 0)
                + subTrees.stream().mapToInt(Tree::count).sum();
    }

    /**
     * Returns a list of subtrees.
     *
     * @return a List of trees
     */
    @Override
    public List<Tree<T>> subTrees() {
        return subTrees;
    }
}
