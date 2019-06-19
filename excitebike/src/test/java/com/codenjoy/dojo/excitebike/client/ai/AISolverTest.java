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
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AISolverTest {

    private Dice dice;
    private AISolver solver;

    @Before
    public void setup() {
        dice = mock(Dice.class);
        solver = new AISolver(dice);
    }

    @Test
    public void get__shouldReturnRandomDownOrUp__ifNextElementIsObstacleAndThereIsNothingBelowOrAbove() {
        //given
        Board board = toBoard(
                        "■■■■■" +
                        "     " +
                        "  o█ " +
                        "     " +
                        "■■■■■"
        );
        boolean randomBool = new Random().nextBoolean();
        when(dice.next(1)).thenReturn(randomBool ? 1 : 0);

        //when
        String result = solver.get(board);

        //then
        assertThat(result, is(randomBool ? Direction.UP.toString() : Direction.DOWN.toString()));
    }

    @Test
    public void get__shouldReturnDOWN__ifNextElementIsObstacleAndThereIsEnemyBikeAboveAndNothingBelow() {
        //given
        Board board = toBoard(
                        "■■■■■" +
                        "     " +
                        "  o█ " +
                        "  e  " +
                        "■■■■■"
        );

        //when
        String result = solver.get(board);

        //then
        assertThat(result, is(Direction.DOWN.toString()));
    }

    @Test
    public void get__shouldReturnUP__ifNextElementIsObstacleAndThereIsEnemyBikeBelowAndNothingAbove() {
        //given
        Board board = toBoard(
                        "■■■■■" +
                        "  e  " +
                        "  o█ " +
                        "     " +
                        "■■■■■"
        );

        //when
        String result = solver.get(board);

        //then
        assertThat(result, is(Direction.UP.toString()));
    }

    @Test
    public void get__shouldReturnUP__ifNextElementIsObstacleAndThereIsEnemyBikeAboveButBorderBelow() {
        //given
        Board board = toBoard(
                        "■■■■■" +
                        "   o█ " +
                        "   e  " +
                        "     " +
                        "■■■■■"
        );

        //when
        String result = solver.get(board);

        //then
        assertThat(result, is(Direction.UP.toString()));
    }

    @Test
    public void get__shouldReturnDOWN__ifNextElementIsObstacleAndThereIsEnemyBikeBelowButBorderAbove() {
        //given
        Board board = toBoard(
                        "■■■■■" +
                        "     " +
                        "  e  " +
                        "  o█ " +
                        "■■■■■"
        );

        //when
        String result = solver.get(board);

        //then
        assertThat(result, is(Direction.DOWN.toString()));
    }

    private Board toBoard(String board) {
        return (Board) new Board().forString(board);
    }

}
