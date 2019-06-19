package com.codenjoy.dojo.excitebike.client.ai;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.excitebike.client.Board;
import com.codenjoy.dojo.excitebike.model.items.Elements;
import com.codenjoy.dojo.excitebike.model.items.bike.BikeType;
import com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardType;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.printer.CharElements;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class AISolverParametrizedTest {

    private Dice dice;
    private AISolver solver;
    private char elementAtRight;
    private char elementAbove;
    private char elementBelow;
    private Direction expectedDirection;

    public AISolverParametrizedTest(CharElements elementAtRight, CharElements elementAbove, CharElements elementBelow, Direction expectedDirection) {
        this.elementAtRight = elementAtRight.ch();
        this.elementAbove = elementAbove.ch();
        this.elementBelow = elementBelow.ch();
        this.expectedDirection = expectedDirection;
        dice = mock(Dice.class);
        solver = new AISolver(dice);
    }

    @Parameterized.Parameters(name = "Element to be evaded: {0}, element above: {1}, element below: {2}, expected direction: {3}")
    public static List<Object[]> data() {
        return Lists.newArrayList(
                // avoid obstacle - random choice
                new Object[]{Elements.OBSTACLE, Elements.NONE, Elements.NONE, null},

                // avoid obstacle - choose not border
                new Object[]{Elements.OBSTACLE, Elements.NONE, Elements.BORDER, Direction.UP},
                new Object[]{Elements.OBSTACLE, BikeType.OTHER_BIKE, Elements.BORDER, Direction.UP},
                new Object[]{Elements.OBSTACLE, BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, Direction.UP},
                new Object[]{Elements.OBSTACLE, BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, Direction.UP},

                // no way to survive
                new Object[]{Elements.OBSTACLE, BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, Direction.STOP},

                // avoid obstacle - choose not border
                new Object[]{Elements.OBSTACLE, Elements.BORDER, Elements.NONE, Direction.DOWN},
                new Object[]{Elements.OBSTACLE, Elements.BORDER, BikeType.OTHER_BIKE, Direction.DOWN},
                new Object[]{Elements.OBSTACLE, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_LEFT, Direction.DOWN},
                new Object[]{Elements.OBSTACLE, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_RIGHT, Direction.DOWN},

                // no way to survive
                new Object[]{Elements.OBSTACLE, Elements.BORDER, BikeType.OTHER_BIKE_FALLEN, Direction.STOP},

                // avoid other bike - random choice
                new Object[]{BikeType.OTHER_BIKE, Elements.NONE, Elements.NONE, null},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE, Elements.NONE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE, BikeType.OTHER_BIKE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE, BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE, BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, Direction.UP},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE, BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, Direction.STOP},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE, Elements.BORDER, Elements.NONE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE, Elements.BORDER, BikeType.OTHER_BIKE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_LEFT, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_RIGHT, Direction.DOWN},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE, Elements.BORDER, BikeType.OTHER_BIKE_FALLEN, Direction.STOP},

                // avoid other bike - random choice
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.NONE, Elements.NONE, null},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.NONE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, BikeType.OTHER_BIKE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, Direction.UP},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, Direction.STOP},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, Elements.NONE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, BikeType.OTHER_BIKE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_LEFT, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_RIGHT, Direction.DOWN},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, BikeType.OTHER_BIKE_FALLEN, Direction.STOP},

                // avoid other bike - random choice
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.NONE, Elements.NONE, null},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.NONE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, BikeType.OTHER_BIKE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, Direction.UP},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, Direction.STOP},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, Elements.NONE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, BikeType.OTHER_BIKE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_LEFT, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_RIGHT, Direction.DOWN},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, BikeType.OTHER_BIKE_FALLEN, Direction.STOP},

                // avoid other bike - random choice
                new Object[]{BikeType.OTHER_BIKE_FALLEN, Elements.NONE, Elements.NONE, null},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE_FALLEN, Elements.NONE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_FALLEN, BikeType.OTHER_BIKE, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_FALLEN, BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.BORDER, Direction.UP},
                new Object[]{BikeType.OTHER_BIKE_FALLEN, BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.BORDER, Direction.UP},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE_FALLEN, BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, Direction.STOP},

                // avoid other bike - choose not border
                new Object[]{BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, Elements.NONE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, BikeType.OTHER_BIKE, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_LEFT, Direction.DOWN},
                new Object[]{BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, BikeType.OTHER_BIKE_INCLINE_RIGHT, Direction.DOWN},

                // no way to avoid other bike / change the line
                new Object[]{BikeType.OTHER_BIKE_FALLEN, Elements.BORDER, BikeType.OTHER_BIKE_FALLEN, Direction.STOP},

                // hit the bike
                new Object[]{Elements.NONE, BikeType.OTHER_BIKE, Elements.NONE, Direction.UP},
                new Object[]{Elements.NONE, BikeType.OTHER_BIKE_INCLINE_LEFT, Elements.NONE, Direction.UP},
                new Object[]{Elements.NONE, BikeType.OTHER_BIKE_INCLINE_RIGHT, Elements.NONE, Direction.UP},
                new Object[]{Elements.NONE, Elements.NONE, BikeType.OTHER_BIKE, Direction.DOWN},
                new Object[]{Elements.NONE, Elements.NONE, BikeType.OTHER_BIKE_INCLINE_LEFT, Direction.DOWN},
                new Object[]{Elements.NONE, Elements.NONE, BikeType.OTHER_BIKE_INCLINE_RIGHT, Direction.DOWN},

                // incline the bike according to the springboard
                new Object[]{SpringboardType.LEFT_DOWN, Elements.NONE, Elements.NONE, Direction.LEFT},
                new Object[]{SpringboardType.RIGHT_DOWN, Elements.NONE, Elements.NONE, Direction.RIGHT}
        );
    }

    @Test
    public void get__shouldReturnAppropriateDirection__accordingToElementsAround() {
        //given
        Board board = toBoard(
                        "■■■■■" +
                        "  " + elementBelow + "  " +
                        "  o" + elementAtRight + " " +
                        "  " + elementAbove + "  " +
                        "■■■■■"
        );
        if (expectedDirection == null) {
            boolean randomBool = new Random().nextBoolean();
            when(dice.next(1)).thenReturn(randomBool ? 1 : 0);
            expectedDirection = randomBool ? Direction.UP : Direction.DOWN;
        }

        //when
        String result = solver.get(board);

        //then
        assertThat(result, is(expectedDirection.toString()));
    }

    private Board toBoard(String board) {
        return (Board) new Board().forString(board);
    }

}
