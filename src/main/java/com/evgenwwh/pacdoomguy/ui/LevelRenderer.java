package com.evgenwwh.pacdoomguy.ui;

import com.evgenwwh.pacdoomguy.model.GameSession;
import com.evgenwwh.pacdoomguy.model.Level;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/** Draws a {@link Level}: the static background is rendered once and cached, dots are drawn per frame. */
public final class LevelRenderer {
    private static final int BEVEL = 4;

    private final Level level;
    private BufferedImage background;

    public LevelRenderer(Level level) {
        this.level = level;
    }

    /** Walls and floor at native scale (one {@link Level#CELL} per tile). */
    public BufferedImage background() {
        if (background == null) {
            background = new BufferedImage(level.widthPx(), level.heightPx(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = background.createGraphics();
            paintTiles(g, level, Level.CELL, true);
            g.dispose();
        }
        return background;
    }

    public void drawDots(Graphics2D g, GameSession session) {
        BufferedImage dot = Sprites.get("dot");
        int off = (Level.CELL - dot.getWidth()) / 2;
        for (int cy = 0; cy < level.rows(); cy++) {
            for (int cx = 0; cx < level.cols(); cx++) {
                if (session.hasDot(cx, cy)) {
                    g.drawImage(dot, cx * Level.CELL + off, cy * Level.CELL + off, null);
                }
            }
        }
    }

    /** Small map preview fitted into the given box, keeping aspect ratio. */
    public static void drawPreview(Graphics2D g, Level level, int x, int y, int w, int h) {
        int cell = Math.max(1, Math.min(w / level.cols(), h / level.rows()));
        int pw = cell * level.cols();
        int ph = cell * level.rows();
        Graphics2D g2 = (Graphics2D) g.create(x + (w - pw) / 2, y + (h - ph) / 2, pw, ph);
        paintTiles(g2, level, cell, false);
        g2.setColor(Theme.withAlpha(Color.WHITE, 140));
        for (int cy = 0; cy < level.rows(); cy++) {
            for (int cx = 0; cx < level.cols(); cx++) {
                if (level.tile(cx, cy) == Level.DOT) {
                    g2.fillRect(cx * cell + cell / 2, cy * cell + cell / 2, 1, 1);
                }
            }
        }
        g2.dispose();
    }

    private static void paintTiles(Graphics2D g, Level level, int cell, boolean detailed) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        Color wall = level.wallColor();
        Color floor = level.floorColor();
        Color light = blend(wall, Color.WHITE, 0.22);
        Color dark = blend(wall, Color.BLACK, 0.45);
        Color floorLine = Theme.withAlpha(Color.BLACK, 22);

        for (int cy = 0; cy < level.rows(); cy++) {
            for (int cx = 0; cx < level.cols(); cx++) {
                int px = cx * cell;
                int py = cy * cell;
                if (level.isWall(cx, cy)) {
                    g.setColor(wall);
                    g.fillRect(px, py, cell, cell);
                    if (!detailed) {
                        continue;
                    }
                    // Bevel only the edges that face a corridor.
                    if (!level.isWall(cx, cy - 1)) {
                        g.setColor(light);
                        g.fillRect(px, py, cell, BEVEL);
                    }
                    if (!level.isWall(cx - 1, cy)) {
                        g.setColor(light);
                        g.fillRect(px, py, BEVEL, cell);
                    }
                    if (!level.isWall(cx, cy + 1)) {
                        g.setColor(dark);
                        g.fillRect(px, py + cell - BEVEL, cell, BEVEL);
                    }
                    if (!level.isWall(cx + 1, cy)) {
                        g.setColor(dark);
                        g.fillRect(px + cell - BEVEL, py, BEVEL, cell);
                    }
                } else {
                    g.setColor(floor);
                    g.fillRect(px, py, cell, cell);
                    if (detailed) {
                        g.setColor(floorLine);
                        g.drawLine(px, py + cell - 1, px + cell - 1, py + cell - 1);
                        g.drawLine(px + cell - 1, py, px + cell - 1, py + cell - 1);
                    }
                }
            }
        }
        if (detailed) {
            // Soft dark seam between wall and floor gives the maze some depth.
            g.setColor(Theme.withAlpha(Color.BLACK, 110));
            g.setStroke(new BasicStroke(1));
            for (int cy = 0; cy < level.rows(); cy++) {
                for (int cx = 0; cx < level.cols(); cx++) {
                    if (level.isWall(cx, cy)) {
                        continue;
                    }
                    int px = cx * cell;
                    int py = cy * cell;
                    if (level.isWall(cx, cy - 1)) {
                        g.drawLine(px, py, px + cell - 1, py);
                    }
                    if (level.isWall(cx - 1, cy)) {
                        g.drawLine(px, py, px, py + cell - 1);
                    }
                    if (level.isWall(cx, cy + 1)) {
                        g.drawLine(px, py + cell - 1, px + cell - 1, py + cell - 1);
                    }
                    if (level.isWall(cx + 1, cy)) {
                        g.drawLine(px + cell - 1, py, px + cell - 1, py + cell - 1);
                    }
                }
            }
        }
    }

    private static Color blend(Color a, Color b, double t) {
        return new Color(
                (int) (a.getRed() + (b.getRed() - a.getRed()) * t),
                (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }
}
