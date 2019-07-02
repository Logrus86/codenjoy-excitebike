package com.codenjoy.dojo.excitebike.services.parse;

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

import com.codenjoy.dojo.excitebike.model.items.Accelerator;
import com.codenjoy.dojo.excitebike.model.items.Border;
import com.codenjoy.dojo.excitebike.model.items.GameElementType;
import com.codenjoy.dojo.excitebike.model.items.Inhibitor;
import com.codenjoy.dojo.excitebike.model.items.LineChanger;
import com.codenjoy.dojo.excitebike.model.items.Obstacle;
import com.codenjoy.dojo.excitebike.model.items.bike.Bike;
import com.codenjoy.dojo.excitebike.model.items.bike.BikeType;
import com.codenjoy.dojo.excitebike.model.items.springboard.Springboard;
import com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElement;
import com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.printer.CharElements;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MapParserImpl implements MapParser {

    private String map;
    private int xSize;

    public MapParserImpl(String map) {
        this.xSize = (int) Math.sqrt(map.length());
        this.map = map;
    }

    public MapParserImpl(String map, int xSize) {
        this.map = map;
        this.xSize = xSize;
    }

    @Override
    public int getXSize() {
        return xSize;
    }

    @Override
    public int getYSize() {
        return map.length() / xSize;
    }

    @Override
    public List<Bike> getFallenBikes() {
        return parseAndConvertElements(Bike::new,
                Arrays.stream(BikeType.values())
                        .filter(e -> e.name().contains(Bike.FALLEN_BIKE_SUFFIX))
                        .toArray(BikeType[]::new));
    }

    @Override
    public List<Accelerator> getAccelerators() {
        return parseAndConvertElements(Accelerator::new, GameElementType.ACCELERATOR);
    }

    @Override
    public List<Border> getBorders() {
        return parseAndConvertElements(Border::new, GameElementType.BORDER);
    }

    @Override
    public List<Inhibitor> getInhibitors() {
        return parseAndConvertElements(Inhibitor::new, GameElementType.INHIBITOR);
    }

    @Override
    public List<LineChanger> getLineUpChangers() {
        return parseAndConvertElements(point -> new LineChanger(point, true), GameElementType.LINE_CHANGER_UP);
    }

    @Override
    public List<LineChanger> getLineDownChangers() {
        return parseAndConvertElements(point -> new LineChanger(point, false), GameElementType.LINE_CHANGER_DOWN);
    }

    @Override
    public List<Obstacle> getObstacles() {
        return parseAndConvertElements(Obstacle::new, GameElementType.OBSTACLE);
    }

    private <T> List<T> parseAndConvertElements(Function<Point, T> elementConstructor, CharElements... elements) {
        return IntStream.range(0, map.length())
                .filter(index -> Arrays.stream(elements).anyMatch(e -> map.charAt(index) == e.ch()))
                .mapToObj(this::convertToPoint)
                .map(elementConstructor)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Point convertToPoint(int position) {
        return position == -1
                ? null
                : PointImpl.pt(position % xSize, (this.map.length() - position - 1) / xSize);
    }
}
