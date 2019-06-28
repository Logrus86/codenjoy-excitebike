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


import com.codenjoy.dojo.excitebike.model.items.bike.Bike;
import com.codenjoy.dojo.excitebike.services.parse.MapParser;
import com.codenjoy.dojo.excitebike.services.parse.MapParserImpl;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.multiplayer.Single;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.printer.PrinterFactoryImpl;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultiplayerSystemTest {

    private Game game1;
    private Game game2;
    private Game game3;
    private Dice dice;
    private GameFieldImpl field;

    private void init() {
        MapParser mapParser = new MapParserImpl("■■■■■■■" +
                "       " +
                "       " +
                "       " +
                "       " +
                "       " +
                "■■■■■■■");

        dice = mock(Dice.class);
        field = new GameFieldImpl(mapParser, dice);
        PrinterFactory factory = new PrinterFactoryImpl();

        game1 = new Single(new Player(mock(EventListener.class)), factory);
        game1.on(field);

        game2 = new Single(new Player(mock(EventListener.class)), factory);
        game2.on(field);

        game3 = new Single(new Player(mock(EventListener.class)), factory);
        game3.on(field);

        game1.newGame();
        game2.newGame();
        game3.newGame();
    }

    @Test
    public void games__shouldInitializeCorrectly() {
        //given

        //when
        init();

        //then
        assertThat(game1.getBoardAsString(), is("■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "E      \n" +
                " E     \n" +
                "B      \n" +
                "■■■■■■■\n"));
        assertThat(game2.getBoardAsString(), is("■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "E      \n" +
                " B     \n" +
                "E      \n" +
                "■■■■■■■\n"));
        assertThat(game3.getBoardAsString(), is("■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "B      \n" +
                " E     \n" +
                "E      \n" +
                "■■■■■■■\n"));
    }

    @Test
    public void shouldJoystick() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);

        game3.getJoystick().up();
        game2.getJoystick().left();
        game1.getJoystick().down();

        //when
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "E      \n" +
                "       \n" +
                " Z     \n" +
                "       \n" +
                "c■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldRemove() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);

        //when
        game3.close();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                "       \n" +
                " E     \n" +
                "B      \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldCrushEnemyBikeAfterClash() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX()+1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX()+1);

        //when
        game1.getJoystick().up();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                " E     \n" +
                " k     \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game2.isGameOver(), is(true));
    }

    @Test
    public void shouldCrushEnemyBikeAfterClash2() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX()+1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX()+1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().up();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                " l     \n" +
                " B     \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
        assertThat(game3.isGameOver(), is(true));
    }

    @Test
    public void shouldCrushEnemyBikeAfterClash3() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX()+1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX()+1);

        //when
        game1.getJoystick().down();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                " E     \n" +
                "       \n" +
                " B     \n" +
                "■r■■■■■\n";
        assertThat(game2.getBoardAsString(), is(expected));
        assertThat(game1.isGameOver(), is(true));
    }

    @Test
    public void shouldDoNothingAfterBikesClashEachOther__bike1() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);

        //when
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                " E     \n" +
                " E     \n" +
                " B     \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldDoNothingAfterBikesClashEachOther__bike2() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);

        //when
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                " E     \n" +
                " B     \n" +
                " E     \n" +
                "■■■■■■■\n";
        assertThat(game2.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldDoNothingAfterBikesClashEachOther__bike3() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX()+1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX()+1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().down();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                "       \n" +
                " B     \n" +
                " E     \n" +
                " E     \n" +
                "■■■■■■■\n";
        assertThat(game3.getBoardAsString(), is(expected));
    }

    @Test
    public void shouldMoveBikesInAnyOrderOfCall() {
        //given
        init();
        when(dice.next(anyInt())).thenReturn(5);
        Bike bike1 = (Bike) game1.getPlayer().getHero();
        bike1.setX(bike1.getX()+1);
        Bike bike3 = (Bike) game3.getPlayer().getHero();
        bike3.setX(bike3.getX()+1);

        //when
        game1.getJoystick().up();
        game2.getJoystick().up();
        game3.getJoystick().up();
        field.tick();

        //then
        String expected = "■■■■■■■\n" +
                "       \n" +
                " E     \n" +
                " E     \n" +
                " B     \n" +
                "       \n" +
                "■■■■■■■\n";
        assertThat(game1.getBoardAsString(), is(expected));
    }
}
