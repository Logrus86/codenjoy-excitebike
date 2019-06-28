package com.codenjoy.dojo.excitebike.model.items.bike;

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


import com.codenjoy.dojo.excitebike.model.GameField;
import com.codenjoy.dojo.excitebike.model.Player;
import com.codenjoy.dojo.excitebike.model.items.Shiftable;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.multiplayer.PlayerHero;

import java.util.Objects;

import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_AT_ACCELERATOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_AT_DOWNED_BIKE;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_AT_INHIBITOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_AT_LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_AT_LINE_CHANGER_UP;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN_AT_ACCELERATOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN_AT_BORDER;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN_AT_INHIBITOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN_AT_LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN_AT_LINE_CHANGER_UP;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_FALLEN_AT_OBSTACLE;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_LEFT;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_LEFT_AT_ACCELERATOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_LEFT_AT_INHIBITOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_LEFT_AT_LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_LEFT_AT_LINE_CHANGER_UP;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_RIGHT;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_RIGHT_AT_ACCELERATOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_RIGHT_AT_INHIBITOR;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_RIGHT_AT_LINE_CHANGER_DOWN;
import static com.codenjoy.dojo.excitebike.model.items.bike.BikeType.BIKE_INCLINE_RIGHT_AT_LINE_CHANGER_UP;
import static com.codenjoy.dojo.services.Direction.DOWN;
import static com.codenjoy.dojo.services.Direction.UP;

public class Bike extends PlayerHero<GameField> implements State<BikeType, Player>, Shiftable {

    public static final String OTHER_BIKE_PREFIX = "OTHER";
    public static final String FALLEN_BIKE_SUFFIX = "FALLEN";
    public static final String INCLINE_LEFT_AT_PREFIX = "BIKE_INCLINE_LEFT_AT";
    public static final String INCLINE_RIGHT_AT_PREFIX = "BIKE_INCLINE_RIGHT_AT";
    public static final String BIKE_AT_PREFIX = "BIKE_AT";

    private Direction command;
    private Movement movement = new Movement();
    private BikeType type = BIKE;
    private boolean ticked;
    private boolean accelerated;
    private boolean inhibited;
    private boolean interacted;

    public Bike(Point xy) {
        super(xy);
    }

    public Bike(int x, int y) {
        super(x, y);
    }

    @Override
    public void init(GameField gameField) {
        this.field = gameField;
    }

    @Override
    public void down() {
        if (!isAlive()) return;
        command = DOWN;
    }

    @Override
    public void up() {
        if (!isAlive()) return;
        command = UP;
    }

    @Override
    public void left() {
        if (!isAlive()) return;
        changeIncline(BIKE_INCLINE_LEFT, BIKE_INCLINE_RIGHT);
    }

    @Override
    public void right() {
        if (!isAlive()) return;
        changeIncline(BIKE_INCLINE_RIGHT, BIKE_INCLINE_LEFT);
    }

    private void changeIncline(BikeType toIncline, BikeType inclinedTo) {
        if (type == BIKE) {
            type = toIncline;
        } else if (type == inclinedTo) {
            type = BIKE;
        }
    }

    public void crush() {
        type = type == BIKE_AT_ACCELERATOR ? BIKE_FALLEN_AT_ACCELERATOR :
                type == BIKE_AT_INHIBITOR ? BIKE_FALLEN_AT_INHIBITOR :
                        type == BIKE_AT_LINE_CHANGER_DOWN ? BIKE_FALLEN_AT_LINE_CHANGER_DOWN :
                                type == BIKE_AT_LINE_CHANGER_UP ? BIKE_FALLEN_AT_LINE_CHANGER_UP :
                                        BIKE_FALLEN;
    }

    public void jump() {
        //TODO add new BikeType.JUMP for springboard TASK - 26-springboard-item
        //type = BikeType.BIKE_JUMP;
    }

    @Override
    public void act(int... p) {
        //nothing to do
    }

    @Override
    public void tick() {
        if (!ticked) {
            if (isAlive()) {
                actAccordingToState();
                executeCommand();
                adjustStateToElement();
                tryToMove();
            }
        }
    }

    private void executeCommand() {
        if (command != null) {
            x = command.changeX(x);
            y = command.changeY(y);
            interactWithOtherBike();
            adjustStateToElement();
            command = null;
        } else {
            interacted = false;
        }
    }

    private void actAccordingToState() {
        if (type == BIKE_AT_ACCELERATOR
                || type == BIKE_INCLINE_LEFT_AT_ACCELERATOR
                || type == BIKE_INCLINE_RIGHT_AT_ACCELERATOR
                || accelerated) {
            movement.setRight();
            type = backToNormalType();
            accelerated = false;
            return;
        }

        if (type == BIKE_AT_INHIBITOR
                || type == BIKE_INCLINE_LEFT_AT_INHIBITOR
                || type == BIKE_INCLINE_RIGHT_AT_INHIBITOR) {
            if (!inhibited) {
                movement.setLeft();
                inhibited = true;
            }
            type = backToNormalType();
            return;
        } else {
            inhibited = false;
        }

        if (type == BIKE_AT_LINE_CHANGER_UP
                || type == BIKE_INCLINE_LEFT_AT_LINE_CHANGER_UP
                || type == BIKE_INCLINE_RIGHT_AT_LINE_CHANGER_UP) {
            movement.setUp();
            type = backToNormalType();
            return;
        }

        if (type == BIKE_AT_LINE_CHANGER_DOWN
                || type == BIKE_INCLINE_LEFT_AT_LINE_CHANGER_DOWN
                || type == BIKE_INCLINE_RIGHT_AT_LINE_CHANGER_DOWN) {
            movement.setDown();
            type = backToNormalType();
        }

    }

    private BikeType backToNormalType() {
        return type.name().contains(INCLINE_LEFT_AT_PREFIX) ? BIKE_INCLINE_LEFT :
                type.name().contains(INCLINE_RIGHT_AT_PREFIX) ? BIKE_INCLINE_RIGHT :
                        type.name().contains(BIKE_AT_PREFIX) ? BIKE : type;
    }

    private void tryToMove() {
        if (!isAlive()) {
            return;
        }
        if (movement.isUp()) {
            y = UP.changeY(y);
        }
        if (movement.isDown()) {
            y = DOWN.changeY(y);
        }
        if (movement.isLeft()) {
            x = Direction.LEFT.changeX(x);
            if (isAlive() && x < 0) {
                x = 0;
            }
        }
        if (movement.isRight()) {
            x = Direction.RIGHT.changeX(x);
            if (x >= field.size()) {
                x = field.size() - 1;
            }
        }
        interactWithOtherBike();
        movement.clear();
        adjustStateToElement();
    }

    private void interactWithOtherBike() {
        if (interacted) {
            return;
        }
        field.getEnemyBike(x, y, field.getPlayerOfBike(this)).ifPresent(enemy -> {
            if (enemy != this) {
                if (!enemy.isAlive() ||
                        (movement.isRight()
                                && !enemy.movement.isRight()
                                && !enemy.movement.isUp()
                                && !enemy.movement.isDown()
                                && enemy.command == null)) {
                    crush();
                    enemy.type = BIKE_AT_DOWNED_BIKE;
                    return;
                }
                if (!enemy.movement.isUp() && !enemy.movement.isDown() && enemy.command == null) {
                    enemy.crush();
                    type = BIKE_AT_DOWNED_BIKE;
                    move(enemy);
                    enemy.ticked = true;
                    movement.clear();
                    command = null;
                } else if (((movement.isDown() || command == DOWN) && (enemy.movement.isUp() || enemy.command == UP))
                        || ((movement.isUp() || command == UP) && (enemy.movement.isDown() || enemy.command == DOWN))) {
                    enemy.clearY();
                    if (movement.isUp() || command == UP) {
                        move(x, DOWN.changeY(y));
                    } else if (movement.isDown() || command == DOWN) {
                        move(x, UP.changeY(y));
                    }
                    clearY();
                } else {
                    enemy.tick();
                    enemy.ticked = true;
                }
            }
        });
        interacted = true;
    }

    private void clearY() {
        if (movement.isUp()) {
            movement.setDown();
        } else if (movement.isDown()) {
            movement.setUp();
        }
        command = null;
    }

    private void adjustStateToElement() {
        if (!isAlive()) {
            return;
        }

        if (field.isAccelerator(x, y)) {
            type = BIKE_AT_ACCELERATOR;
            accelerated = true;
            return;
        }

        if (field.isInhibitor(x, y)) {
            type = type == BIKE_AT_ACCELERATOR ? BIKE : BIKE_AT_INHIBITOR;
            if (movement.isRight()) {
                movement.setLeft();
                inhibited = true;
            }
            return;
        }

        if (field.isObstacle(x, y)) {
            if (movement.isRight()) {
                movement.setLeft();
            }
            type = BIKE_FALLEN_AT_OBSTACLE;
            return;
        }

        if (field.isUpLineChanger(x, y)) {
            if (movement.isRight()) {
                movement.setUp();
            } else {
                type = BIKE_AT_LINE_CHANGER_UP;
            }
            return;
        }

        if (field.isDownLineChanger(x, y)) {
            if (movement.isRight()) {
                movement.setDown();
            } else {
                type = BIKE_AT_LINE_CHANGER_DOWN;
            }
            return;
        }

        if (field.isBorder(x, y)) {
            type = BIKE_FALLEN_AT_BORDER;
            return;
        }

        if (!field.getEnemyBike(x, y, field.getPlayerOfBike(this)).isPresent()) {
            type = backToNormalType();
        }
    }

    @Override
    public BikeType state(Player player, Object... alsoAtPoint) {
        Bike bike = player.getHero();

        return this == bike ? bike.type : this.getEnemyBikeType();
    }

    private BikeType getEnemyBikeType() {
        return BikeType.valueOf(OTHER_BIKE_PREFIX + "_" + type.name());
    }

    public boolean isAlive() {
        return type != null && !type.name().contains(FALLEN_BIKE_SUFFIX);
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Bike bike = (Bike) o;
        return ticked == bike.ticked &&
                accelerated == bike.accelerated &&
                inhibited == bike.inhibited &&
                Objects.equals(movement, bike.movement) &&
                type == bike.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), movement, type, ticked, inhibited, accelerated);
    }

    @Override
    public String toString() {
        return "Bike{" +
                "movement=" + movement +
                ", type=" + type +
                ", ticked=" + ticked +
                ", accelerated=" + accelerated +
                ", inhibited=" + inhibited +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
