/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Paul Campbell
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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.EqualsAndHashCode;
import net.kemitix.mon.maybe.Maybe;

import java.util.*;
import java.util.function.Function;

/**
 * A generic tree of trees and objects.
 *
 * <p>Each node may contain between 0 and n objects.</p>
 *
 * @param <T> the type of the objects help in the tree
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
@SuppressFBWarnings("USBR_UNNECESSARY_STORE_BEFORE_RETURN")
@EqualsAndHashCode
class GeneralisedTree<T> implements Tree<T>, TreeMapper<T> {

    private transient final T item;
    private transient final List<Tree<T>> subTrees;

    /**
     * Creates a new tree.
     *
     * @param item the item of this node
     * @param subTrees the sub-trees under this node
     */
    GeneralisedTree(final T item, final Collection<Tree<T>> subTrees) {
        this.item = item;
        this.subTrees = new ArrayList<>(subTrees);
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
        return new GeneralisedTree<>(f.apply(item), mapTrees(f, subTrees()));
    }

    @Override
    public Maybe<T> item() {
        return Maybe.maybe(item);
    }

    /**
     * Returns a list of subtrees.
     *
     * @return a List of trees
     */
    @Override
    public List<Tree<T>> subTrees() {
        return new ArrayList<>(subTrees);
    }
}
