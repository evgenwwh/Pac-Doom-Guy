package com.evgenwwh.pacdoomguy.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** A wandering enemy. Picks a new direction when blocked or after a random number of ticks. */
public final class Demon extends Entity {
    public static final int WIDTH = 27;
    public static final int HEIGHT = 34;
    public static final double SPEED = 2.0;
    public static final int RESPAWN_TICKS = 300;
    /** Chance that, when choosing a direction, the demon heads towards the player. */
    private static final double CHASE_CHANCE = 0.35;

    private final Point spawnCell;
    private final int variant;
    private final Random rng;
    private Direction direction;
    private int ticksUntilTurn;
    private int deadTicks;

    public Demon(Point spawnCell, int variant, Random rng) {
        super(0, 0, WIDTH, HEIGHT);
        this.spawnCell = new Point(spawnCell);
        this.variant = variant;
        this.rng = rng;
        moveToCell(spawnCell);
    }

    public void update(Level level, Player player) {
        if (deadTicks > 0) {
            deadTicks--;
            if (deadTicks == 0) {
                respawn();
            }
            return;
        }
        if (direction == null || ticksUntilTurn <= 0 || !canStep(level, direction, SPEED)) {
            chooseDirection(level, player);
        }
        if (direction != null && step(level, direction, SPEED)) {
            ticksUntilTurn--;
        } else {
            ticksUntilTurn = 0;
        }
    }

    private void chooseDirection(Level level, Player player) {
        List<Direction> options = new ArrayList<>(4);
        for (Direction d : Direction.values()) {
            if (canStep(level, d, SPEED)) {
                options.add(d);
            }
        }
        // Avoid immediately reversing unless it's the only way out.
        if (direction != null && options.size() > 1) {
            options.remove(direction.opposite());
        }
        if (options.isEmpty()) {
            direction = null;
            ticksUntilTurn = 10;
            return;
        }
        if (rng.nextDouble() < CHASE_CHANCE) {
            direction = towards(player, options);
        } else {
            direction = options.get(rng.nextInt(options.size()));
        }
        ticksUntilTurn = 30 + rng.nextInt(120);
    }

    private Direction towards(Player player, List<Direction> options) {
        Direction best = options.get(0);
        double bestDist = Double.MAX_VALUE;
        for (Direction d : options) {
            double px = centerX() + d.dx * Level.CELL;
            double py = centerY() + d.dy * Level.CELL;
            double dist = Math.hypot(player.centerX() - px, player.centerY() - py);
            if (dist < bestDist) {
                bestDist = dist;
                best = d;
            }
        }
        return best;
    }

    public void kill() {
        deadTicks = RESPAWN_TICKS;
    }

    public void respawn() {
        moveToCell(spawnCell);
        direction = null;
        ticksUntilTurn = 0;
        deadTicks = 0;
    }

    public boolean isAlive() {
        return deadTicks == 0;
    }

    /** Sprite variant index (0..2). */
    public int variant() {
        return variant;
    }
}
