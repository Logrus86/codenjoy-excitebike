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



import com.codenjoy.dojo.excitebike.model.items.Hero;
import com.codenjoy.dojo.excitebike.services.Events;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.printer.BoardReader;

import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;
import static java.util.stream.Collectors.toList;

/**
 * О! Это самое сердце игры - борда, на которой все происходит.
 * Если какой-то из жителей борды вдруг захочет узнать что-то у нее, то лучше ему дать интефейс {@see GameField}
 * Борда реализует интерфейс {@see Tickable} чтобы быть уведомленной о каждом тике игры. Обрати внимание на {GameFieldImpl#tick()}
 */
public class GameFieldImpl implements GameField {

    private List<Player> players;
    private Level level;
    private Dice dice;

    public GameFieldImpl(Level level, Dice dice) {
        this.dice = dice;
        players = new LinkedList<>();
    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
        for (Player player : players) {
            Hero hero = player.getHero();

            hero.tick();

          /*  if (gold.contains(hero)) {
                gold.remove(hero);
                player.event(Events.WIN);

                Point pos = getNewPlayerPosition();
                gold.add(new Gold(pos));
            }*/
        }

        for (Player player : players) {
            Hero hero = player.getHero();

            if (!hero.isAlive()) {
                player.event(Events.LOOSE);
            }
        }
    }

    public int size() {
        return level.getXSize();
    }

    @Override
    public boolean isBarrier(int x, int y) {
        Point pt = pt(x, y);
        return x > level.getXSize() - 1
                || x < 0
                || y < 0
                || y > level.getXSize() - 1
                //|| walls.contains(pt)
                || getHeroes().contains(pt);
    }

    @Override
    public Point getNewPlayerPosition() {
        //TODO implement right logic
        int x;
        int y;
        int c = 0;
        do {
            x = dice.next(level.getXSize());
            y = dice.next(level.getXSize());
        } while (!isFree(x, y) && c++ < 100);

        if (c >= 100) {
            return pt(0, 0);
        }

        return pt(x, y);
    }

    @Override
    public boolean isFree(int x, int y) {
        Point pt = pt(x, y);

        return false;/*!(gold.contains(pt)
                || bombs.contains(pt)
                || walls.contains(pt)
                || getHeroes().contains(pt));*/
    }

    @Override
    public boolean isBomb(int x, int y) {
        return false;//bombs.contains(pt(x, y));
    }

    @Override
    public void setBomb(int x, int y) {
        Point pt = pt(x, y);
        /*if (!bombs.contains(pt)) {
            bombs.add(new Bomb(x, y));
        }*/
    }

    @Override
    public void removeBomb(int x, int y) {
        //bombs.remove(pt(x, y));
    }

  /*  public List<Gold> getGold() {
        return gold;
    }*/

    public List<Hero> getHeroes() {
        return players.stream()
                .map(Player::getHero)
                .collect(toList());
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

    /*public List<Wall> getWalls() {
        return walls;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }*/

    @Override
    public BoardReader reader() {
        return new BoardReader() {

            @Override
            public int size() {
                return level.getXSize();
            }

            @Override
            public Iterable<? extends Point> elements() {
                return new LinkedList<Point>(){{
                    //addAll(GameFieldImpl.this.getWalls());
                    addAll(GameFieldImpl.this.getHeroes());
                    //addAll(GameFieldImpl.this.getGold());
                    //addAll(GameFieldImpl.this.getBombs());
                }};
            }
        };
    }
}
