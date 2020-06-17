package net.kemitix.mon.tree;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class MutableTreeTest implements WithAssertions {

    @Test
    void canCreateAnEmptyLeaf() {
        //when
        final Tree<String> leaf = MutableTree.create();
        //then
        assertThat(leaf.item().isNothing()).isTrue();
    }

    @Test
    void canCreateANonEmptyLeaf() {
        //given
        final String item = "item";
        //when
        final MutableTree<String> leaf = MutableTree.create();
        leaf.set(item);
        //then
        assertThat(leaf.item().toOptional()).contains(item);
    }

    @Test
    void emptyLeafHasCountZero() {
        //given
        final Tree<Object> tree = MutableTree.create();
        //when
        final int count = tree.count();
        //then
        assertThat(count).isZero();
    }

    @Test
    void nonEmptyLeafHasCountOne() {
        //given
        final MutableTree<String> tree = MutableTree.create();
        tree.set("value");
        //when
        final int count = tree.count();
        //then
        assertThat(count).isEqualTo(1);
    }

    @Test
    void canCreateTreeWithSubTrees() {
        //given
        final String leafItem = "leaf";
        final MutableTree<String> leaf = MutableTree.leaf(leafItem);
        final MutableTree<String> tree = MutableTree.create();
        //when
        tree.subTrees(singletonList(leaf));
        //then
        assertThat(tree.subTrees()).containsExactly(leaf);
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
        final MutableTree<UUID> tree = MutableTree.of(
                uid1,
                asList(
                        MutableTree.leaf(uid2),
                        MutableTree.leaf(uid3)));
        //when
        final Tree<String> result = tree.map(UUID::toString);
        //then
        final MutableTree<String> expectedTree = MutableTree.of(
                sid1,
                asList(
                        MutableTree.leaf(sid2),
                        MutableTree.leaf(sid3)));
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedTree);
    }

    @Test
    void canCloneNonMutableTree() {
        //given
        final UUID rootItem = UUID.randomUUID();
        final UUID leafItem = UUID.randomUUID();
        final Tree<UUID> immutableTree = Tree.of(rootItem, singletonList(Tree.leaf(leafItem)));
        //when
        final MutableTree<UUID> mutableTree = MutableTree.of(immutableTree);
        //then
        assertThat(mutableTree.count()).isEqualTo(2);
        assertThat(mutableTree.item().toOptional()).contains(rootItem);
        final List<Tree<UUID>> subTrees = mutableTree.subTrees();
        assertThat(subTrees).hasSize(1);
        assertThat(subTrees.get(0).item().toOptional()).contains(leafItem);
    }

}