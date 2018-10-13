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

import net.kemitix.mon.maybe.Maybe;

import java.util.function.Function;

/**
 * Builder for a {@link Tree}.
 *
 * @param <T> the type of the tree
 *
 * @author Paul Campbell (pcampbell@kemitix.net)
 */
class MutableTreeBuilder<T> implements TreeBuilder<T> {

    private final MutableTree<T> root;

    /**
     * Create empty tree builder.
     */
    MutableTreeBuilder() {
        root = MutableTree.create();
    }

    /**
     * Create a tree builder to work with the given tree.
     *
     * @param tree the tree to build upon
     */
    MutableTreeBuilder(final MutableTree<T> tree) {
        root = tree;
    }

    @Override
    public Tree<T> build() {
        return root.map(Function.identity());
    }

    @Override
    public TreeBuilder<T> item(final T item) {
        root.set(item);
        return this;
    }

    @Override
    public TreeBuilder<T> add(final Tree<T> subtree) {
        root.add(subtree);
        return this;
    }

    @Override
    public TreeBuilder<T> addChild(final T childItem) {
        root.add(MutableTree.leaf(childItem));
        return this;
    }

    @Override
    public Maybe<TreeBuilder<T>> select(final T childItem) {
        return Maybe.findFirst(
                root.subTreesAsMutable()
                        .stream()
                        .filter((MutableTree<T> tree) -> matchesItem(childItem, tree))
                        .map(Tree::builder));
    }

    private Boolean matchesItem(final T childItem, final MutableTree<T> tree) {
        return tree.item().map(childItem::equals).orElse(false);
    }

}
