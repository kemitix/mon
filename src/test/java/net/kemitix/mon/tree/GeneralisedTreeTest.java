package net.kemitix.mon.tree;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class GeneralisedTreeTest implements WithAssertions {

    @Test
    void canCreateAnEmptyLeaf() {
        //when
        final Tree<String> leaf = Tree.leaf(null);
        //then
        assertThat(leaf.item().isNothing()).isTrue();
    }

    @Test
    void canCreateANonEmptyLeaf() {
        //given
        final String item = "item";
        //when
        final Tree<String> leaf = Tree.leaf(item);
        //then
        assertThat(leaf.item().toOptional()).contains(item);
    }

    @Test
    void emptyLeafHasCountZero() {
        //given
        final Tree<Object> tree = Tree.leaf(null);
        //when
        final int count = tree.count();
        //then
        assertThat(count).isZero();
    }

    @Test
    void nonEmptyLeafHasCountOne() {
        //given
        final Tree<String> tree = Tree.leaf("value");
        //when
        final int count = tree.count();
        //then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void canCreateTreeWithSubTrees() {
        //given
        final String treeItem = "tree";
        final String leafItem = "leaf";
        //when
        final Tree<String> tree = Tree.of(treeItem, singletonList(Tree.leaf(leafItem)));
        //then
        assertThat(tree.subTrees()).containsExactly(Tree.leaf(leafItem));
    }

    @Test
    void canMapNestedTrees() {
        //given
        final UUID uid1 = UUID.randomUUID();
        final UUID uid2 = UUID.randomUUID();
        final UUID uid3 = UUID.randomUUID();
        final String sid1 = uid1.toString();
        final String sid2 = uid2.toString();
        final String sid3 = uid3.toString();
        final Tree<UUID> tree = Tree.of(
                uid1,
                asList(
                        Tree.leaf(uid2),
                        Tree.leaf(uid3)));
        //when
        final Tree<String> result = tree.map(UUID::toString);
        //then
        assertThat(result).isEqualTo(Tree.of(
                sid1,
                asList(
                        Tree.leaf(sid2),
                        Tree.leaf(sid3)))
        );
    }

}