package com.evgenwwh.pacdoomguy.model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;

/** Something with a position and an axis-aligned bounding box in map pixels. */
public abstract class Entity {
    protected double x;
    protected double y;
    protected final int width;
    protected final int height;

    protected Entity(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Places the entity so that it is centred in the given cell. */
    protected void moveToCell(Point cell) {
        x = cell.x * Level.CELL + (Level.CELL - width) / 2.0;
        y = cell.y * Level.CELL + (Level.CELL - height) / 2.0;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double centerX() {
        return x + width / 2.0;
    }

    public double centerY() {
        return y + height / 2.0;
    }

    public int cellX() {
        return (int) Math.floor(centerX() / Level.CELL);
    }

    public int cellY() {
        return (int) Math.floor(centerY() / Level.CELL);
    }

    public Rectangle2D bounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }

    public boolean intersects(Entity other) {
        return bounds().intersects(other.bounds());
    }

    /**
     * Tries to step in {@code dir} by {@code speed} pixels. Two Pac-Man style helpers apply:
     * <ul>
     *   <li>the entity never leaves the lane centre towards a wall, so it stops flush with the
     *       corridor instead of "hugging" the wall by the sprite's margin;</li>
     *   <li>if a turn is requested a few pixels early, the entity is nudged towards the lane
     *       centre of the perpendicular axis first ("cornering").</li>
     * </ul>
     *
     * @return true if the entity moved
     */
    protected boolean step(Level level, Direction dir, double speed) {
        double nx = clampedX(level, dir, speed);
        double ny = clampedY(level, dir, speed);
        if (level.isWalkable(nx, ny, width, height)) {
            boolean moved = nx != x || ny != y;
            x = nx;
            y = ny;
            return moved;
        }
        // Blocked: maybe we are just off-centre in the perpendicular axis.
        double laneX = laneX();
        double laneY = laneY();
        double tolerance = Level.CELL / 2.0;
        if (dir.isHorizontal() && Math.abs(laneY - y) <= tolerance && level.isWalkable(nx, laneY, width, height)) {
            y = approach(y, laneY, speed);
            if (level.isWalkable(nx, y, width, height)) {
                x = nx;
            }
            return true;
        }
        if (!dir.isHorizontal() && Math.abs(laneX - x) <= tolerance && level.isWalkable(laneX, ny, width, height)) {
            x = approach(x, laneX, speed);
            if (level.isWalkable(x, ny, width, height)) {
                y = ny;
            }
            return true;
        }
        return false;
    }

    /** True if a step in {@code dir} would actually move the entity, including cornering assistance. */
    protected boolean canStep(Level level, Direction dir, double speed) {
        double nx = clampedX(level, dir, speed);
        double ny = clampedY(level, dir, speed);
        if (level.isWalkable(nx, ny, width, height)) {
            return nx != x || ny != y;
        }
        double tolerance = Level.CELL / 2.0;
        if (dir.isHorizontal()) {
            return Math.abs(laneY() - y) <= tolerance && level.isWalkable(nx, laneY(), width, height);
        }
        return Math.abs(laneX() - x) <= tolerance && level.isWalkable(laneX(), ny, width, height);
    }

    /** X of this entity when centred in its current column. */
    private double laneX() {
        return cellX() * Level.CELL + (Level.CELL - width) / 2.0;
    }

    /** Y of this entity when centred in its current row. */
    private double laneY() {
        return cellY() * Level.CELL + (Level.CELL - height) / 2.0;
    }

    /** Next X after a step; does not pass the lane centre if the neighbouring cell is a wall. */
    private double clampedX(Level level, Direction dir, double speed) {
        double nx = x + dir.dx * speed;
        if (dir.dx != 0 && level.isWall(cellX() + dir.dx, cellY())) {
            nx = dir.dx < 0 ? Math.max(nx, laneX()) : Math.min(nx, laneX());
        }
        return nx;
    }

    /** Next Y after a step; does not pass the lane centre if the neighbouring cell is a wall. */
    private double clampedY(Level level, Direction dir, double speed) {
        double ny = y + dir.dy * speed;
        if (dir.dy != 0 && level.isWall(cellX(), cellY() + dir.dy)) {
            ny = dir.dy < 0 ? Math.max(ny, laneY()) : Math.min(ny, laneY());
        }
        return ny;
    }

    private static double approach(double from, double to, double maxDelta) {
        if (Math.abs(to - from) <= maxDelta) {
            return to;
        }
        return from + Math.signum(to - from) * maxDelta;
    }
}
