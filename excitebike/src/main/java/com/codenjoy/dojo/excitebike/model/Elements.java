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


import com.codenjoy.dojo.services.printer.CharElements;

public enum Elements implements CharElements {

    //ASCII code of char in comment
    //Drawable elements
    BORDER('■'),    //254
    WATCHER('*'),
    TRIBUNE_HEADER('╦'),    //203
    TRIBUNE_BOTTOM('║'),    //186

    //Interactive elements
    BIKE('o'),
    BIKE_ENEMY('e'),
    BIKE_FALLEN('~'),
    BIKE_INCLINE_LEFT('('),
    BIKE_INCLINE_RIGHT(')'),

    ACCELERATOR('»'),   //175
    INHIBITOR('▒'),    //177
    OBSTACLE('█'),     //178
    LINE_CHANGER('┤'),  //180
    ROAD('░'),         //176
    NONE(' '),

    SPRINGBOARD_DARK('/'),
    SPRINGBOARD_LIGHT('\\'),
    SPRINGBOARD_LEFT_DOWN('╚'),     //200
    SPRINGBOARD_LEFT_UP('╔'),       //201
    SPRINGBOARD_RIGHT_DOWN('╝'),    //188
    SPRINGBOARD_RIGHT_UP('╗');      //187


    final char ch;

    Elements(char ch) {
        this.ch = ch;
    }

    @Override
    public char ch() {
        return ch;
    }

    @Override
    public String toString() {
        return String.valueOf(ch);
    }

    public static Elements valueOf(char ch) {
        for (Elements el : Elements.values()) {
            if (el.ch == ch) {
                return el;
            }
        }
        throw new IllegalArgumentException("No such element for " + ch);
    }

}
