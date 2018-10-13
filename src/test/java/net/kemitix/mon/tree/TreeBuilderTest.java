package net.kemitix.mon.tree;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

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
        assertThat(result.item()).isEmpty();
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
        assertThat(result.item()).contains(node);
    }

    @Test
    void whenAddSubTreeThenTreeHasSubTree() {
        //given
        final TreeBuilder<Node> builder = Tree.builder(Node.class);
        /// add subtree
        final Tree<Node> subtree = Tree.leaf(createANode());
        builder.item(createANode());
        builder.add(subtree);
        //when
        final Tree<Node> result = builder.build();
        //then
        assertThat(result.count()).isEqualTo(2);
        assertThat(result.subTrees()).contains(subtree);
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
