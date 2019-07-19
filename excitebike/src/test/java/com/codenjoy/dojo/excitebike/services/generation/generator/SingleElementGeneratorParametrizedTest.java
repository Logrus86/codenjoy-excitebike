package com.codenjoy.dojo.excitebike.services.generation.generator;

import com.codenjoy.dojo.excitebike.model.items.GameElementType;
import com.codenjoy.dojo.excitebike.model.items.Shiftable;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.printer.CharElements;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Pavel Bobylev 7/19/2019
 */
@RunWith(Parameterized.class)
public class SingleElementGeneratorParametrizedTest {

    private GameElementType expectedElementType;

    public SingleElementGeneratorParametrizedTest(GameElementType expectedElementType) {
        this.expectedElementType = expectedElementType;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object> data() {
        return Lists.newArrayList(
                GameElementType.ACCELERATOR,
                GameElementType.INHIBITOR,
                GameElementType.OBSTACLE,
                GameElementType.LINE_CHANGER_UP,
                GameElementType.LINE_CHANGER_DOWN
                );
    }

    @Test
    public void generate__shouldReturnElementAtLineN__accordingToDice() {
        //given
        Dice dice = mock(Dice.class);
        int xSize = 10;
        int ySize = 10;
        int expectedLine = new Random().nextInt();
        when(dice.next(GameElementType.values().length - 2)).thenReturn(Arrays.asList(GameElementType.values()).indexOf(expectedElementType) - 2);
        when(dice.next(ySize - 2)).thenReturn(expectedLine - 1);

        //when
        Map<? extends CharElements, List<Shiftable>> result = new SingleElementGenerator(dice, xSize, ySize).generate();

        //then
        assertThat(result.values(), hasSize(1));
        assertThat(result.get(expectedElementType), hasSize(1));
        assertThat(result.get(expectedElementType).get(0).getX(), is(xSize - 1));
        assertThat(result.get(expectedElementType).get(0).getY(), is(expectedLine));
    }
}
