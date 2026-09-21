package com.evgenwwh.pacdoomguy.model;

import com.evgenwwh.pacdoomguy.model.item.Item;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * State and rules of a single play-through of one level.
 * Pure model: no Swing, no threads. Call {@link #update()} once per tick (60 Hz).
 */
public final class GameSession {
    public static final int TICKS_PER_SECOND = 60;
    public static final int START_LIVES = 4;
    public static final int DOT_SCORE = 1;
    public static final int KILL_SCORE = 10;
    public static final double SPEED_MULTIPLIER = 1.75;
    public static final int MAX_ITEMS = 4;
    private static final int ITEM_SPAWN_INTERVAL = 5 * TICKS_PER_SECOND;
    private static final double ITEM_SPAWN_CHANCE = 0.25;

    private final Level level;
    private final Random rng;
    private final Player player;
    private final List<Demon> demons = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private final boolean[][] dots;
    private final Map<Effect, Integer> effects = new EnumMap<>(Effect.class);

    private GameState state = GameState.RUNNING;
    private int lives = START_LIVES;
    private int score;
    private int dotsLeft;
    private long ticks;

    public GameSession(Level level) {
        this(level, new Random());
    }

    public GameSession(Level level, Random rng) {
        this.level = level;
        this.rng = rng;
        this.player = new Player(level.playerSpawn());
        this.dots = level.newDotMask();
        this.dotsLeft = level.totalDots();
        int i = 0;
        for (Point spawn : level.demonSpawns()) {
            demons.add(new Demon(spawn, i++ % 3, rng));
        }
    }

    // ---- input -------------------------------------------------------------------------------

    public void setDesiredDirection(Direction dir) {
        player.setDesiredDirection(dir);
    }

    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    // ---- simulation --------------------------------------------------------------------------

    public void update() {
        if (state != GameState.RUNNING) {
            return;
        }
        ticks++;
        tickEffects();
        player.update(level, playerSpeed());
        collectDot();
        for (Demon demon : demons) {
            demon.update(level, player);
        }
        updateItems();
        resolveDemonContact();
    }

    private double playerSpeed() {
        return hasEffect(Effect.SPEED) ? Player.BASE_SPEED * SPEED_MULTIPLIER : Player.BASE_SPEED;
    }

    private void tickEffects() {
        effects.replaceAll((effect, left) -> left - 1);
        effects.values().removeIf(left -> left <= 0);
    }

    private void collectDot() {
        int cx = player.cellX();
        int cy = player.cellY();
        if (cy >= 0 && cy < dots.length && cx >= 0 && cx < dots[cy].length && dots[cy][cx]) {
            dots[cy][cx] = false;
            dotsLeft--;
            score += DOT_SCORE;
            if (dotsLeft == 0) {
                state = GameState.WON;
            }
        }
    }

    private void updateItems() {
        items.removeIf(item -> !item.tick());
        for (Item item : new ArrayList<>(items)) {
            if (player.intersects(item)) {
                items.remove(item);
                applyEffect(item.effect());
            }
        }
        if (ticks % ITEM_SPAWN_INTERVAL == 0) {
            for (Demon demon : demons) {
                if (items.size() < MAX_ITEMS && demon.isAlive() && rng.nextDouble() < ITEM_SPAWN_CHANCE) {
                    items.add(new Item(randomEffect(), demon.centerX(), demon.centerY()));
                }
            }
        }
    }

    private Effect randomEffect() {
        Effect[] all = Effect.values();
        return all[rng.nextInt(all.length)];
    }

    /** Activates (or refreshes) an effect. Visible for tests. */
    public void applyEffect(Effect effect) {
        effects.put(effect, effect.durationTicks);
    }

    private void resolveDemonContact() {
        for (Demon demon : demons) {
            if (!demon.isAlive() || !demon.intersects(player)) {
                continue;
            }
            if (hasEffect(Effect.KILL)) {
                demon.kill();
                score += KILL_SCORE;
            } else if (!hasEffect(Effect.SHIELD) && !player.isSpawnInvulnerable()) {
                loseLife();
                return;
            }
        }
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            lives = 0;
            state = GameState.LOST;
        } else {
            player.respawn();
        }
    }

    // ---- queries -----------------------------------------------------------------------------

    public Level level() {
        return level;
    }

    public Player player() {
        return player;
    }

    public List<Demon> demons() {
        return Collections.unmodifiableList(demons);
    }

    public List<Item> items() {
        return Collections.unmodifiableList(items);
    }

    public boolean hasDot(int cx, int cy) {
        return dots[cy][cx];
    }

    public GameState state() {
        return state;
    }

    public int lives() {
        return lives;
    }

    public int score() {
        return score;
    }

    public int dotsLeft() {
        return dotsLeft;
    }

    public int elapsedSeconds() {
        return (int) (ticks / TICKS_PER_SECOND);
    }

    public long ticks() {
        return ticks;
    }

    public boolean hasEffect(Effect effect) {
        return effects.containsKey(effect);
    }

    /** Remaining duration as a fraction 0..1, or 0 if inactive. */
    public double effectProgress(Effect effect) {
        Integer left = effects.get(effect);
        return left == null ? 0 : (double) left / effect.durationTicks;
    }
}
