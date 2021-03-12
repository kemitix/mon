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

import net.kemitix.mon.maybe.Maybe;

import java.util.List;

/**
 * Mutable builder for a {@link Tree}.
 *
 * @param <T> the type of the tree to build
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
public interface TreeBuilder<T> {

    /**
     * Create the immutable {@link Tree}.
     *
     * @return a {@link Tree}
     */
    public abstract Tree<T> build();

    /**
     * Set the current {@link Tree}'s item.
     *
     * @param item the item for the current {@link Tree}
     *
     * @return the TreeBuilder
     */
    public abstract TreeBuilder<T> item(T item);

    /**
     * Adds the subtree to the current tree.
     *
     * @param subtree the tree to add
     *
     * @return the TreeBuilder
     */
    public abstract TreeBuilder<T> add(Tree<T> subtree);

    /**
     * Add the Child item as a subTree.
     *
     * @param childItem the item to add as a subtree
     * @return the TreeBuilder
     */
    public abstract TreeBuilder<T> addChild(T childItem);

    /**
     * Add all the child items as subTrees.
     *
     * @param children the items to add as a subtree
     * @return the TreeBuilder
     */
    public default TreeBuilder<T> addChildren(List<T> children) {
        children.forEach(this::addChild);
        return this;
    }

    /**
     * Create a TreeBuilder for the subTree of the current Tree that has the childItem.
     *
     * @param childItem the item of search the subtrees for
     * @return a Maybe containing the TreeBuilder for the subtree, or Nothing if there child item is not found
     */
    public abstract Maybe<TreeBuilder<T>> select(T childItem);
}
