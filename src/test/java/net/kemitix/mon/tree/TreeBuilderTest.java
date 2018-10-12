package net.kemitix.mon.tree;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TreeBuilderTest {

    @Test
    void whenEmptyBuilderBuildThenTreeIsAnEmptyLeaf() {
        //given
        final TreeBuilder<String> builder = Tree.builder(String.class);
        //when
        final Tree<String> result = builder.build();
        //then
        assertThat(result.count()).isZero();
        assertThat(result.item()).isEmpty();
    }

}
