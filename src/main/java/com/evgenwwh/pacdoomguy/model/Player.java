package com.evgenwwh.pacdoomguy.model;

import java.awt.Point;

/** The player character: moves continuously in the current direction, turning when possible. */
public final class Player extends Entity {
    public static final int WIDTH = 27;
    public static final int HEIGHT = 38;
    public static final double BASE_SPEED = 2.4;
    /** Ticks of invulnerability granted after a respawn. */
    public static final int SPAWN_INVULNERABLE_TICKS = 120;
    private static final int FRAME_TICKS = 12;

    private final Point spawnCell;
    private Direction facing = Direction.DOWN;
    private Direction current;
    private Direction desired;
    private boolean moving;
    private int animTicks;
    private int frame;
    private int invulnerableTicks;

    public Player(Point spawnCell) {
        super(0, 0, WIDTH, HEIGHT);
        this.spawnCell = new Point(spawnCell);
        moveToCell(spawnCell);
    }

    /** Requests a direction; the turn happens as soon as the corridor allows it. */
    public void setDesiredDirection(Direction dir) {
        desired = dir;
    }

    public void update(Level level, double speed) {
        if (invulnerableTicks > 0) {
            invulnerableTicks--;
        }
        if (desired != null && desired != current && canStep(level, desired, speed)) {
            current = desired;
        }
        moving = current != null && step(level, current, speed);
        if (current != null) {
            facing = current;
        }
        if (moving) {
            animTicks++;
            if (animTicks >= FRAME_TICKS) {
                animTicks = 0;
                frame ^= 1;
            }
        }
    }

    public void respawn() {
        moveToCell(spawnCell);
        current = null;
        desired = null;
        facing = Direction.DOWN;
        invulnerableTicks = SPAWN_INVULNERABLE_TICKS;
    }

    public Direction facing() {
        return facing;
    }

    /** 0 or 1: which walking frame to draw. */
    public int frame() {
        return moving ? frame : 0;
    }

    public boolean isSpawnInvulnerable() {
        return invulnerableTicks > 0;
    }

    public int invulnerableTicks() {
        return invulnerableTicks;
    }
}
