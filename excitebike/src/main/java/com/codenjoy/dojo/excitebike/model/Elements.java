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

    //Interactive elements
    BIKE_BACK('<'),
    BIKE_FRONT('>'),
    BIKE_FALLEN_BACK('`'),
    BIKE_FALLEN_FRONT('~'),
    BIKE_INCLINE_LEFT_BACK('('),
    BIKE_INCLINE_LEFT_FRONT('['),
    BIKE_INCLINE_RIGHT_BACK(')'),
    BIKE_INCLINE_RIGHT_FRONT(']'),

    ACCELERATOR('»'),   //175
    INHIBITOR('▒'),    //177
    OBSTACLE('█'),     //178
    LINE_CHANGER_UP('▲'),  //30
    LINE_CHANGER_DOWN('▼'),  //31
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
