package com.codenjoy.dojo.excitebike.model;

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


import com.codenjoy.dojo.excitebike.model.items.Fence;
import com.codenjoy.dojo.excitebike.model.items.Shiftable;
import com.codenjoy.dojo.excitebike.model.items.bike.Bike;
import com.codenjoy.dojo.excitebike.model.items.springboard.SpringboardElementType;
import com.codenjoy.dojo.excitebike.services.Events;
import com.codenjoy.dojo.excitebike.services.generation.GenerationOption;
import com.codenjoy.dojo.excitebike.services.generation.TrackStepGenerator;
import com.codenjoy.dojo.excitebike.services.generation.WeightedRandomBag;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.printer.BoardReader;
import com.codenjoy.dojo.services.printer.CharElements;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.codenjoy.dojo.excitebike.model.items.GameElementType.ACCELERATOR;
import static com.codenjoy.dojo.excitebike.model.items.GameElementType.INHIBITOR;
import static com.codenjoy.dojo.excitebike.model.items.GameElementType.LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.GameElementType.LINE_CHANGER_UP;
import static com.codenjoy.dojo.excitebike.model.items.GameElementType.OBSTACLE;
import static com.codenjoy.dojo.excitebike.model.items.bike.Bike.OTHER_BIKE_PREFIX;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN;
import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


public class GameFieldImpl implements GameField {

    private final MapParser mapParser;
    private final Map<CharElements, List<Shiftable>> allShiftableElements = new HashMap<>();
    private final List<Player> players = new LinkedList<>();
    private final List<Fence> fences;
    private final TrackStepGenerator trackStepGenerator;

    public GameFieldImpl(MapParser mapParser, Dice dice, WeightedRandomBag<GenerationOption> weightedRandomBag) {
        this.mapParser = mapParser;

        fences = mapParser.getFences();

        allShiftableElements.put(ACCELERATOR, new ArrayList<>(mapParser.getAccelerators()));
        allShiftableElements.put(INHIBITOR, new ArrayList<>(mapParser.getInhibitors()));
        allShiftableElements.put(OBSTACLE, new ArrayList<>(mapParser.getObstacles()));
        allShiftableElements.put(LINE_CHANGER_UP, new ArrayList<>(mapParser.getLineUpChangers()));
        allShiftableElements.put(LINE_CHANGER_DOWN, new ArrayList<>(mapParser.getLineDownChangers()));
        allShiftableElements.put(BIKE_FALLEN, new ArrayList<>(mapParser.getFallenBikes()));

        allShiftableElements.put(SpringboardElementType.SPRINGBOARD_LEFT_UP, new ArrayList<>(mapParser.getSpringboardLeftUpElements()));
        allShiftableElements.put(SpringboardElementType.SPRINGBOARD_RIGHT, new ArrayList<>(mapParser.getSpringboardLightElements()));
        allShiftableElements.put(SpringboardElementType.SPRINGBOARD_LEFT_DOWN, new ArrayList<>(mapParser.getSpringboardLeftDownElements()));
        allShiftableElements.put(SpringboardElementType.SPRINGBOARD_RIGHT_UP, new ArrayList<>(mapParser.getSpringboardRightUpElements()));
        allShiftableElements.put(SpringboardElementType.SPRINGBOARD_LEFT, new ArrayList<>(mapParser.getSpringboardDarkElements()));
        allShiftableElements.put(SpringboardElementType.SPRINGBOARD_RIGHT_DOWN, new ArrayList<>(mapParser.getSpringboardRightDownElements()));
        allShiftableElements.put(SpringboardElementType.SPRINGBOARD_TOP, new ArrayList<>(mapParser.getSpringboardNoneElements()));

        this.trackStepGenerator = new TrackStepGenerator(dice, mapParser.getXSize(), mapParser.getYSize(), weightedRandomBag);
    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
        shiftTrack();
        players.forEach(player -> player.getHero().tick());
        players.forEach(player -> player.getHero().setTicked(false));
        if (players.stream().filter(Player::isAlive).count() <= 1 && players.size() > 1) {
            players.stream().filter(Player::isAlive).findFirst().ifPresent(player -> player.event(Events.WIN));
            restart();
        }
        allShiftableElements.put(BIKE_FALLEN, players.stream()
                .map(Player::getHero)
                .filter(h -> h != null && !h.isAlive())
                .collect(toList())
        );
    }

    private void restart() {
        players.forEach(player -> player.setHero(null));
        allShiftableElements.values().forEach(List::clear);
    }

    public int size() {
        return mapParser.getXSize();
    }

    @Override
    public boolean isFence(int x, int y) {
        return y < 1 || y > mapParser.getYSize() - 2;
    }

    @Override
    public boolean isInhibitor(int x, int y) {
        return allShiftableElements.get(INHIBITOR).contains(pt(x, y));
    }

    @Override
    public boolean isAccelerator(int x, int y) {
        return allShiftableElements.get(ACCELERATOR).contains(pt(x, y));
    }

    @Override
    public boolean isObstacle(int x, int y) {
        return allShiftableElements.get(OBSTACLE).contains(pt(x, y));
    }

    @Override
    public boolean isUpLineChanger(int x, int y) {
        return allShiftableElements.get(LINE_CHANGER_UP).contains(pt(x, y));
    }

    @Override
    public boolean isDownLineChanger(int x, int y) {
        return allShiftableElements.get(LINE_CHANGER_DOWN).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardDarkElement(int x, int y) {
        return allShiftableElements.get(SpringboardElementType.SPRINGBOARD_LEFT).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardLightElement(int x, int y) {
        return allShiftableElements.get(SpringboardElementType.SPRINGBOARD_RIGHT).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardLeftDownElement(int x, int y) {
        return allShiftableElements.get(SpringboardElementType.SPRINGBOARD_LEFT_DOWN).contains(pt(x, y));
    }

    @Override
    public boolean isSpringboardRightDownElement(int x, int y) {
        return allShiftableElements.get(SpringboardElementType.SPRINGBOARD_RIGHT_DOWN).contains(pt(x, y));
    }

    @Override
    public Optional<Bike> getEnemyBike(int x, int y, Player player) {
        return player != null ?
                players.parallelStream()
                        .map(Player::getHero)
                        .filter(bike -> bike.state(player).name().contains(OTHER_BIKE_PREFIX) && bike.itsMe(x, y))
                        .findFirst()
                : Optional.empty();
    }

    @Override
    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public Bike getNewFreeBike() {
        return createNewFreeBike(getBikesCountOnEachY());
    }

    private Map<Integer, Long> getBikesCountOnEachY() {
        Map<Integer, Long> bikesCountOnEachY = getBikes().stream().collect(
                groupingBy(
                        PointImpl::getY,
                        Collectors.counting()
                )
        );
        IntStream.range(1, mapParser.getYSize() - 1).forEach(value -> bikesCountOnEachY.putIfAbsent(value, 0L));
        return bikesCountOnEachY;
    }

    private int getYWithMinNumberOfBikes(Map<Integer, Long> bikesCountOnEachY) {
        return bikesCountOnEachY.entrySet()
                .stream()
                .min(
                        Comparator.comparingLong(Map.Entry::getValue)
                )
                .map(Map.Entry::getKey)
                .orElseGet(() -> 1);
    }

    private Bike createNewFreeBike(Map<Integer, Long> bikesCountOnEachY) {
        final int minPossibleX = 0;
        final int step = 3;
        int y = getYWithMinNumberOfBikes(bikesCountOnEachY);

        Bike newBike = new Bike(minPossibleX, y);

        for (int i = minPossibleX; i < mapParser.getXSize(); i += step) {
            newBike.setX(newBike.getY() % 2 == 0 ? i + 1 : i);
            if (isFree(newBike) || tryToSetFreeCoordinates(newBike, bikesCountOnEachY, i)) {
                break;
            }
            newBike.setY(1);
        }
        return newBike;
    }

    private boolean tryToSetFreeCoordinates(Bike bike, Map<Integer, Long> bikesOnYCount, int x) {
        return bikesOnYCount.entrySet()
                .stream()
                .sorted(comparingByValue())
                .map(Map.Entry::getKey)
                .anyMatch(y -> {
                    bike.setY(y);
                    bike.setX(y % 2 == 0 ? x + 1 : x);
                    return isFree(bike);
                });
    }

    private boolean isFree(Point point) {
        return !getBikes().contains(point) && !fences.contains(point) && !allShiftableElements.get(OBSTACLE).contains(point);
    }

    public List<Bike> getBikes() {
        return players.stream()
                .map(Player::getHero)
                .filter(Objects::nonNull)
                .collect(toList());
    }

    public List<Fence> getFences() {
        return fences;
    }

    @Override
    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    @Override
    public void remove(Player player) {
        players.remove(player);
    }

    @Override
    public BoardReader reader() {
        return new BoardReader() {

            @Override
            public int size() {
                return mapParser.getXSize();
            }

            @Override
            public Iterable<? extends Point> elements() {
                return new LinkedList<Point>() {{
                    addAll(GameFieldImpl.this.getBikes());
                    GameFieldImpl.this.allShiftableElements.values().forEach(this::addAll);
                    addAll(getFences());
                }};
            }
        };
    }

    private void shiftTrack() {
        final int lastPossibleX = 0;
        allShiftableElements.values().parallelStream().forEach(
                pointsOfElementType -> {
                    pointsOfElementType.forEach(Shiftable::shift);
                    pointsOfElementType.removeIf(point -> point.getX() < lastPossibleX);
                }
        );

        generateNewTrackStep();
    }

    private void generateNewTrackStep() {
        Map<? extends CharElements, List<Shiftable>> generated = trackStepGenerator.generate();
        if (generated != null) {
            generated.forEach((key, elements) -> allShiftableElements.merge(key, elements, (currentElements, newElements) -> {
                        currentElements.addAll(newElements);
                        return currentElements;
                    }
            ));
        }
    }

    @Override
    public Player getPlayerOfBike(Bike bike) {
        return players.parallelStream().filter(p -> Objects.equals(p.getHero(), bike)).findFirst().orElse(null);
    }
}