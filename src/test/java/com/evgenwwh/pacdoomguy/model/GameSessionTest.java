package com.evgenwwh.pacdoomguy.model;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    /** A single horizontal corridor: spawn on the left, three dots to the right, demon at far end. */
    private static Level corridor() {
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1},
                {1, 3, 2, 2, 2, 0, 1},
                {1, 1, 1, 1, 1, 1, 1},
        };
        return new Level("corridor", grid, Color.BLACK, Color.GRAY, List.of(new Point(5, 1)));
    }

    /** Like {@link #corridor()} but with an extra dot in a dead end the player never visits, so the game cannot be won. */
    private static Level endlessCorridor() {
        int[][] grid = {
                {1, 1, 1, 1, 1, 1, 1},
                {1, 3, 2, 2, 2, 0, 1},
                {1, 2, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1},
        };
        return new Level("corridor", grid, Color.BLACK, Color.GRAY, List.of(new Point(5, 1)));
    }

    private static void tick(GameSession s, int n) {
        for (int i = 0; i < n; i++) {
            s.update();
        }
    }

    @Test
    void collectingAllDotsWinsAndScores() {
        GameSession s = new GameSession(corridor(), new Random(1));
        s.setDesiredDirection(Direction.RIGHT);
        s.applyEffect(Effect.SHIELD);
        tick(s, 120);
        assertEquals(3, s.score());
        assertEquals(0, s.dotsLeft());
        assertEquals(GameState.WON, s.state());
    }

    @Test
    void playerCannotWalkIntoWalls() {
        GameSession s = new GameSession(corridor(), new Random(1));
        s.setDesiredDirection(Direction.UP);
        double y = s.player().y();
        tick(s, 30);
        assertEquals(y, s.player().y(), 0.001);
        s.setDesiredDirection(Direction.LEFT);
        double x = s.player().x();
        tick(s, 30);
        assertEquals(x, s.player().x(), 0.001);
    }

    @Test
    void touchingDemonCostsLifeAndRespawnsPlayer() {
        GameSession s = new GameSession(corridor(), new Random(1));
        Point spawn = s.level().playerSpawn();
        s.setDesiredDirection(Direction.RIGHT);
        tick(s, 60);
        assertTrue(s.lives() < GameSession.START_LIVES, "demon in a dead-end corridor must have hit the player");
        assertEquals(spawn.x, s.player().cellX());
        assertTrue(s.player().isSpawnInvulnerable());
    }

    @Test
    void shieldPreventsDamage() {
        GameSession s = new GameSession(corridor(), new Random(1));
        s.applyEffect(Effect.SHIELD);
        s.setDesiredDirection(Direction.RIGHT);
        tick(s, 100);
        assertEquals(GameSession.START_LIVES, s.lives());
    }

    @Test
    void killEffectSlaysDemonAndItRespawnsLater() {
        GameSession s = new GameSession(endlessCorridor(), new Random(1));
        s.applyEffect(Effect.KILL);
        s.setDesiredDirection(Direction.RIGHT);
        tick(s, 100);
        Demon demon = s.demons().get(0);
        assertFalse(demon.isAlive());
        assertTrue(s.score() >= GameSession.KILL_SCORE);
        assertEquals(GameSession.START_LIVES, s.lives());
        // Walk away so the demon does not respawn straight into the still-active KILL effect.
        s.setDesiredDirection(Direction.LEFT);
        tick(s, Demon.RESPAWN_TICKS);
        assertTrue(demon.isAlive());
    }

    @Test
    void effectsExpire() {
        GameSession s = new GameSession(corridor(), new Random(1));
        s.applyEffect(Effect.SPEED);
        assertTrue(s.hasEffect(Effect.SPEED));
        assertEquals(1.0, s.effectProgress(Effect.SPEED), 0.001);
        tick(s, Effect.SPEED.durationTicks);
        assertFalse(s.hasEffect(Effect.SPEED));
    }

    @Test
    void pauseFreezesEverything() {
        GameSession s = new GameSession(corridor(), new Random(1));
        s.setDesiredDirection(Direction.RIGHT);
        tick(s, 5);
        s.togglePause();
        assertEquals(GameState.PAUSED, s.state());
        double x = s.player().x();
        long ticks = s.ticks();
        tick(s, 50);
        assertEquals(x, s.player().x(), 0.001);
        assertEquals(ticks, s.ticks());
        s.togglePause();
        tick(s, 1);
        assertEquals(ticks + 1, s.ticks());
    }

    @Test
    void losingAllLivesEndsGame() {
        GameSession s = new GameSession(corridor(), new Random(1));
        s.setDesiredDirection(Direction.RIGHT);
        tick(s, 60 * 60);
        assertEquals(GameState.LOST, s.state());
        assertEquals(0, s.lives());
    }

    @Test
    void pausingOverGameIsNoOp() {
        GameSession s = new GameSession(corridor(), new Random(1));
        s.setDesiredDirection(Direction.RIGHT);
        tick(s, 60 * 60);
        s.togglePause();
        assertEquals(GameState.LOST, s.state());
    }
}
