package com.codenjoy.dojo.excitebike.services.generation.generator;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 - 2019 Codenjoy
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

import com.codenjoy.dojo.excitebike.model.items.Shiftable;
import com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElement;
import com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType;
import com.codenjoy.dojo.services.Dice;
import com.google.common.collect.Lists;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType.SPRINGBOARD_LEFT;
import static com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType.SPRINGBOARD_LEFT_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType.SPRINGBOARD_LEFT_UP;
import static com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType.SPRINGBOARD_RIGHT;
import static com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType.SPRINGBOARD_RIGHT_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType.SPRINGBOARD_RIGHT_UP;
import static com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType.SPRINGBOARD_TOP;

public class SpringboardGenerator implements Generator {

    private static final int CLEAR_LINES_AROUND_SPRINGBOARD = 1;
    private static final int SPRINGBOARD_TOP_MAX_WIDTH = 5;
    private int width;
    private int x0;
    private int ySize;
    private Dice dice;
    private Map<SpringboardElementType, List<Shiftable>> elements;

    public SpringboardGenerator(Dice dice, int xSize, int ySize) {
        this.x0 = xSize - 1 + CLEAR_LINES_AROUND_SPRINGBOARD;
        this.ySize = ySize;
        this.dice = dice;
    }

    @Override
    public Map<SpringboardElementType, List<Shiftable>> generate() {
        elements = new EnumMap<>(SpringboardElementType.class);

        width = dice.next(SPRINGBOARD_TOP_MAX_WIDTH) + 2;

        generateRiseLine(ySize);
        for (int i = x0 + 1; i < x0 + width - 1; i++) {
            generateSpringBoardStep(SPRINGBOARD_TOP, SPRINGBOARD_TOP, SPRINGBOARD_LEFT, i, ySize);
        }
        generateDescentLine(x0 + width, ySize);

        return elements;
    }

    private void generateRiseLine(int linesNumber) {
        generateSpringBoardStep(SPRINGBOARD_LEFT_UP, SPRINGBOARD_LEFT, SPRINGBOARD_LEFT_DOWN, x0, linesNumber);
    }

    private void generateDescentLine(int x, int linesNumber) {
        generateSpringBoardStep(SPRINGBOARD_RIGHT_UP, SPRINGBOARD_RIGHT, SPRINGBOARD_RIGHT_DOWN, x - 1, linesNumber);
    }

    private void generateSpringBoardStep(SpringboardElementType up, SpringboardElementType middle, SpringboardElementType down, int x, int lines) {
        mergeNewElementToElementsMap(down, x, 1);
        int y = 2;
        for (; y < lines - 1; y++) {
            mergeNewElementToElementsMap(middle, x, y);
        }
        mergeNewElementToElementsMap(up, x, y);
    }

    private void mergeNewElementToElementsMap(SpringboardElementType type, int x, int y) {
        elements.merge(type,
                new LinkedList<>(Lists.newArrayList(new SpringboardElement(x, y, type))),
                (elementsFromMap, newElements) -> {
                    elementsFromMap.addAll(newElements);
                    return elementsFromMap;
                });
    }

    @Override
    public int generationLockSize() {
        return width + CLEAR_LINES_AROUND_SPRINGBOARD * 2;
    }
}
