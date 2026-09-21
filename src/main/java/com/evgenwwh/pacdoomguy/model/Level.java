package com.evgenwwh.pacdoomguy.model;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable level description: the tile grid, spawn points and colour palette.
 * All pixel coordinates used with this class are relative to the top-left corner of the map.
 */
public final class Level {
    public static final int CELL = 48;

    public static final int WALL = 1;
    public static final int DOT = 2;
    public static final int SPAWN = 3;

    private final String name;
    private final int[][] grid;
    private final Color wallColor;
    private final Color floorColor;
    private final Point playerSpawn;
    private final List<Point> demonSpawns;
    private final int totalDots;

    public Level(String name, int[][] grid, Color wallColor, Color floorColor, List<Point> demonSpawns) {
        this.name = name;
        this.grid = grid;
        this.wallColor = wallColor;
        this.floorColor = floorColor;
        this.demonSpawns = Collections.unmodifiableList(new ArrayList<>(demonSpawns));
        this.playerSpawn = findSpawnCenter(grid);
        this.totalDots = countDots(grid);
        validate();
    }

    private void validate() {
        for (int[] row : grid) {
            if (row.length != grid[0].length) {
                throw new IllegalArgumentException("Level '" + name + "' has ragged rows");
            }
        }
        if (playerSpawn == null) {
            throw new IllegalArgumentException("Level '" + name + "' has no spawn (3) cells");
        }
        for (Point p : demonSpawns) {
            if (isWall(p.x, p.y)) {
                throw new IllegalArgumentException("Level '" + name + "' demon spawn " + p + " is inside a wall");
            }
        }
    }

    /** Centre cell of the bounding box of all SPAWN tiles. */
    private static Point findSpawnCenter(int[][] grid) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = -1, maxY = -1;
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == SPAWN) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        if (maxX < 0) {
            return null;
        }
        return new Point((minX + maxX) / 2, (minY + maxY) / 2);
    }

    private static int countDots(int[][] grid) {
        int n = 0;
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell == DOT) {
                    n++;
                }
            }
        }
        return n;
    }

    public String name() {
        return name;
    }

    public int rows() {
        return grid.length;
    }

    public int cols() {
        return grid[0].length;
    }

    public int widthPx() {
        return cols() * CELL;
    }

    public int heightPx() {
        return rows() * CELL;
    }

    public int tile(int cx, int cy) {
        return grid[cy][cx];
    }

    public boolean isWall(int cx, int cy) {
        return cx < 0 || cy < 0 || cy >= rows() || cx >= cols() || grid[cy][cx] == WALL;
    }

    /** True if the rectangle (map pixels) overlaps no wall and stays inside the map. */
    public boolean isWalkable(double x, double y, int w, int h) {
        int left = (int) Math.floor(x / CELL);
        int right = (int) Math.floor((x + w - 1) / CELL);
        int top = (int) Math.floor(y / CELL);
        int bottom = (int) Math.floor((y + h - 1) / CELL);
        if (x < 0 || y < 0) {
            return false;
        }
        for (int cy = top; cy <= bottom; cy++) {
            for (int cx = left; cx <= right; cx++) {
                if (isWall(cx, cy)) {
                    return false;
                }
            }
        }
        return true;
    }

    public Color wallColor() {
        return wallColor;
    }

    public Color floorColor() {
        return floorColor;
    }

    public Point playerSpawn() {
        return new Point(playerSpawn);
    }

    public List<Point> demonSpawns() {
        return demonSpawns;
    }

    public int totalDots() {
        return totalDots;
    }

    /** Fresh copy of the dot layout: true where a dot still has to be collected. */
    public boolean[][] newDotMask() {
        boolean[][] dots = new boolean[rows()][cols()];
        for (int y = 0; y < rows(); y++) {
            for (int x = 0; x < cols(); x++) {
                dots[y][x] = grid[y][x] == DOT;
            }
        }
        return dots;
    }
}
