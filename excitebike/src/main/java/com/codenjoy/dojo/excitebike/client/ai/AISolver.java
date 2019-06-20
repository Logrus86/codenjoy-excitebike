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


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.excitebike.client.Board;
import com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardType;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.algs.DeikstraFindWay;

import java.util.Arrays;
import java.util.List;

import static com.codenjoy.dojo.excitebike.model.items.Elements.BORDER;
import static com.codenjoy.dojo.excitebike.model.items.Elements.LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.Elements.LINE_CHANGER_UP;
import static com.codenjoy.dojo.excitebike.model.items.Elements.OBSTACLE;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.OTHER_BIKE;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.OTHER_BIKE_FALLEN;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.OTHER_BIKE_INCLINE_LEFT;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.OTHER_BIKE_INCLINE_RIGHT;
import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.LEFT;
import static com.codenjoy.dojo.services.Direction.RIGHT;
import static com.codenjoy.dojo.services.Direction.STOP;
import static com.codenjoy.dojo.services.Direction.UP;

/**
 * Это алгоритм твоего бота. Он будет запускаться в игру с первым
 * зарегистрировавшимся игроком, чтобы ему не было скучно играть самому.
 * Реализуй его как хочешь, хоть на Random (только используй для этого
 * {@see Dice} что приходит через конструктор).
 * Для его запуска воспользуйся методом {@see ApofigSolver#main}
 */
public class AISolver implements Solver<Board> {

    private Dice dice;

    public AISolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(final Board board) {
        if (board.isGameOver()) return "";
        Direction result = getCommand(board);
        return result.toString();
    }

    private Direction getCommand(Board board) {
        Direction command = STOP;

        if (board.checkNearMe(RIGHT, OBSTACLE, OTHER_BIKE, OTHER_BIKE_INCLINE_LEFT, OTHER_BIKE_INCLINE_RIGHT, OTHER_BIKE_FALLEN)) {
            command = evade(board);
        } else if (board.checkNearMe(UP, OTHER_BIKE, OTHER_BIKE_INCLINE_LEFT, OTHER_BIKE_INCLINE_RIGHT)) {
            command = UP;
        } else if (board.checkNearMe(DOWN, OTHER_BIKE, OTHER_BIKE_INCLINE_LEFT, OTHER_BIKE_INCLINE_RIGHT)) {
            command = DOWN;
        } else if (board.checkNearMe(RIGHT, SpringboardType.LEFT_DOWN)) {
            command = LEFT;
        } else if (board.checkNearMe(RIGHT, SpringboardType.RIGHT_DOWN)) {
            command = RIGHT;
        }
        return command;
    }

    private Direction evade(Board board) {
        Direction command = null;
        if (board.checkNearMe(UP, BORDER)) {
            if (noLineChangerCurrently(board)) {
                command = DOWN;
            }
        } else if (board.checkNearMe(DOWN, BORDER)) {
            if (noLineChangerCurrently(board)) {
                command = UP;
            }
        } else if (board.checkNearMe(UP, OTHER_BIKE, OTHER_BIKE_INCLINE_LEFT, OTHER_BIKE_INCLINE_RIGHT)) {
            if (noLineChangerCurrently(board)) {
                command = DOWN;
            }
        } else if (board.checkNearMe(DOWN, OTHER_BIKE, OTHER_BIKE_INCLINE_LEFT, OTHER_BIKE_INCLINE_RIGHT)) {
            if (noLineChangerCurrently(board)) {
                command = UP;
            }
        } else if (noLineChangerCurrently(board)) {
            command = randomBoolean() ? UP : DOWN;
        }
        return command;
    }

    private boolean noLineChangerCurrently(Board board) {
        //TODO refactor after adding elements like 'BikeAtLineChanger'
        return !board.checkAtMe(LINE_CHANGER_DOWN) && !board.checkAtMe(LINE_CHANGER_UP);
    }

    private boolean randomBoolean() {
        return dice.next(1) == 1;
    }

}
