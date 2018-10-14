package net.kemitix.mon.tree;

import lombok.RequiredArgsConstructor;
import net.kemitix.mon.maybe.Maybe;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class TreeBuilderTest {

    @Test
    void whenEmptyBuilderBuildThenTreeIsAnEmptyLeaf() {
        //when
        final Tree<Node> result = Tree.builder(Node.class).build();
        //then
        assertThat(result.count()).isZero();
        assertThat(result.item().isNothing()).isTrue();
    }

    @Test
    void whenAddLeafThenTreeHasLeaf() {
        //given
        final Node node = createANode();
        //when
        final Tree<Node> result =
                Tree.builder(Node.class)
                        .item(node).build();
        //then
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.item().toOptional()).contains(node);
    }

    @Test
    void whenAddSubTreeThenTreeHasSubTree() {
        //given
        final Tree<Node> subtree = MutableTree.leaf(createANode());
        //when
        final Tree<Node> result =
                Tree.builder(Node.class)
                        .item(createANode())
                        .add(subtree)
                        .build();
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
        //when
        final TreeBuilder<Node> rootBuilder =
                Tree.builder(Node.class)
                        .item(rootNode)
                        .addChild(childNode);
        rootBuilder.select(childNode)
                .map(childBuilder -> childBuilder.addChild(grandchildNode));
        final Tree<Node> result = rootBuilder.build();
        //then
        assertThat(result.count()).isEqualTo(3);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(
                MutableTree.of(rootNode, Collections.singleton(
                        MutableTree.of(childNode, Collections.singleton(
                                MutableTree.leaf(grandchildNode))))));
    }

    @Test
    void whenAddMultipleChildrenThenTreeHasAllChildren() {
        //given
        final Node rootNode = new Node("root");
        final Node child1Node = createANode();
        final Node child2Node = createANode();
        final Node child3Node = createANode();
        //when
        final Tree<Node> result =
                Tree.builder(Node.class)
                        .item(rootNode)
                        .addChildren(asList(child1Node, child2Node, child3Node))
                        .build();
        //then
        assertThat(result.count()).isEqualTo(4);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(
                MutableTree.of(rootNode, asList(
                        MutableTree.leaf(child1Node),
                        MutableTree.leaf(child2Node),
                        MutableTree.leaf(child3Node))));
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
