package net.kemitix.mon.tree;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.kemitix.mon.maybe.Maybe;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TreeBuilderTest {

    @Test
    void whenEmptyBuilderBuildThenTreeIsAnEmptyLeaf() {
        //given
        final TreeBuilder<Node> builder = Tree.builder(Node.class);
        //when
        final Tree<Node> result = builder.build();
        //then
        assertThat(result.count()).isZero();
        assertThat(result.item().isNothing()).isTrue();
    }

    @Test
    void whenAddLeafThenTreeHasLeaf() {
        //given
        final TreeBuilder<Node> builder = Tree.builder(Node.class);
        final Node node = createANode();
        builder.item(node);
        //when
        final Tree<Node> result = builder.build();
        //then
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.item().toOptional()).contains(node);
    }

    @Test
    void whenAddSubTreeThenTreeHasSubTree() {
        //given
        final TreeBuilder<Node> builder = Tree.builder(Node.class);
        /// add subtree
        final Tree<Node> subtree = MutableTree.leaf(createANode());
        builder.item(createANode());
        builder.add(subtree);
        //when
        final Tree<Node> result = builder.build();
        //then
        assertThat(result.count()).isEqualTo(2);
        assertThat(result.subTrees()).contains(subtree);
    }

    @Test
    void whenAddGrandChildThenTreeHasGrandChild() {
        //given
        final Node rootNode = new Node("root");
        final Node childNode = new Node("child");
        final Node grandchildNode = new Node("grandchild");
        final TreeBuilder<Node> rootBuilder = Tree.builder(Node.class)
                .item(rootNode)
                .addChild(childNode);
        final Maybe<TreeBuilder<Node>> select = rootBuilder.select(childNode);
        select.map(childBuilder -> childBuilder.addChild(grandchildNode));
        //when
        final Tree<Node> result = rootBuilder.build();
        //then
        assertThat(result.count()).isEqualTo(3);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(
                MutableTree.of(rootNode, Collections.singleton(
                        MutableTree.of(childNode, Collections.singleton(
                                MutableTree.leaf(grandchildNode))))));
    }

    private Node createANode() {
        return new Node(createAName());
    }

    private String createAName() {
        return UUID.randomUUID().toString();
    }

    @RequiredArgsConstructor
    private static class Node {
        private final String name;
    }

}
