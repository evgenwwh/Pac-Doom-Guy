package com.evgenwwh.pacdoomguy.model;

import org.junit.jupiter.api.Test;

import java.awt.Point;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    @Test
    void allLevelsHaveValidSpawns() {
        for (Level level : Levels.ALL) {
            Point spawn = level.playerSpawn();
            assertEquals(Level.SPAWN, level.tile(spawn.x, spawn.y), level.name() + " player spawn must be a spawn tile");
            assertFalse(level.demonSpawns().isEmpty(), level.name() + " needs demons");
            for (Point d : level.demonSpawns()) {
                assertFalse(level.isWall(d.x, d.y), level.name() + " demon spawn " + d + " is a wall");
            }
        }
    }

    @Test
    void allLevelsAreEnclosedByWalls() {
        for (Level level : Levels.ALL) {
            for (int x = 0; x < level.cols(); x++) {
                assertTrue(level.isWall(x, 0));
                assertTrue(level.isWall(x, level.rows() - 1));
            }
            for (int y = 0; y < level.rows(); y++) {
                assertTrue(level.isWall(0, y));
                assertTrue(level.isWall(level.cols() - 1, y));
            }
        }
    }

    /**
     * Dot counts of the original maps. Note: the old code declared Hell "won" at 190 points
     * although the map holds 248 dots - the level ended before it was cleared. Fixed here.
     */
    @Test
    void totalDotsMatchesOriginalCounts() {
        assertEquals(128, Levels.ARGENT_NUR.totalDots());
        assertEquals(173, Levels.MARS.totalDots());
        assertEquals(248, Levels.HELL.totalDots());
        assertEquals(122, Levels.SPACE_SHIP.totalDots());
        assertEquals(234, Levels.EARTH.totalDots());
    }

    @Test
    void walkabilityRespectsWallsAndBounds() {
        Level level = Levels.ARGENT_NUR;
        int c = Level.CELL;
        assertTrue(level.isWalkable(c + 5, c + 5, 27, 38), "inside open cell (1,1)");
        assertFalse(level.isWalkable(5, 5, 27, 38), "inside wall (0,0)");
        assertFalse(level.isWalkable(c + 30, c + 5, 27, 38), "overlapping wall (2,1)");
        assertFalse(level.isWalkable(-1, c, 27, 38), "outside map");
    }

    @Test
    void rejectsDemonSpawnInsideWall() {
        int[][] grid = {{1, 1, 1}, {1, 3, 1}, {1, 1, 1}};
        assertThrows(IllegalArgumentException.class,
                () -> new Level("bad", grid, java.awt.Color.BLACK, java.awt.Color.GRAY, java.util.List.of(new Point(0, 0))));
    }
}
