package com.evgenwwh.pacdoomguy.ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Path2D;

/**
 * Classic DOOM menu item: no box, just big bone-coloured text that ignites when selected,
 * with a demon marker on the left. Arrow keys move focus so menus are fully keyboard-driven.
 */
public final class DoomButton extends JButton {
    private static final Color BONE = new Color(0xb8b0a2);
    private static final Color FIRE_TOP = new Color(0xffd23f);
    private static final Color FIRE_BOTTOM = new Color(0xff4a00);
    private static final int MARKER_W = 44;
    /** True once the user navigated with the keyboard; until then focus alone does not highlight. */
    private static boolean keyboardMode;

    public DoomButton(String text) {
        this(text, false);
    }

    /** {@code primary} is kept for API symmetry; every item is styled the same, like in DOOM. */
    public DoomButton(String text, boolean primary) {
        super(text.toUpperCase());
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setFont(Theme.title(34));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        Dimension size = new Dimension(320, 54);
        setPreferredSize(size);
        setMaximumSize(size);
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                keyboardMode = false;
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyboardMode = true;
                repaint();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP, KeyEvent.VK_LEFT, KeyEvent.VK_W, KeyEvent.VK_A -> transferFocusBackward();
                    case KeyEvent.VK_DOWN, KeyEvent.VK_RIGHT, KeyEvent.VK_S, KeyEvent.VK_D -> transferFocus();
                    case KeyEvent.VK_ENTER -> doClick();
                    default -> {
                    }
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.prepare(g);
        int w = getWidth();
        int h = getHeight();
        boolean hot = getModel().isRollover() || (hasFocus() && keyboardMode);
        boolean pressed = getModel().isPressed();

        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        String text = getText();
        int tx = MARKER_W + (w - MARKER_W - fm.stringWidth(text)) / 2;
        int ty = (h - fm.getHeight()) / 2 + fm.getAscent() + (pressed ? 2 : 0);

        // Hard drop shadow, like the DOOM bitmap font.
        g.setColor(Theme.withAlpha(Color.BLACK, 200));
        g.drawString(text, tx + 3, ty + 3);

        if (!isEnabled()) {
            g.setColor(Theme.MUTED);
        } else if (hot) {
            g.setPaint(new GradientPaint(0, ty - fm.getAscent(), FIRE_TOP, 0, ty, FIRE_BOTTOM));
        } else {
            g.setColor(BONE);
        }
        g.drawString(text, tx, ty);

        if (hot && isEnabled()) {
            drawMarker(g, tx - 16, ty - fm.getAscent() / 2, fm.getAscent() * 0.55f);
        }
        g.dispose();
    }

    /** Flaming spike pointing at the selected item, with a dark outline. */
    private static void drawMarker(Graphics2D g, int tipX, int cy, float size) {
        Path2D spike = new Path2D.Float();
        spike.moveTo(tipX, cy);
        spike.lineTo(tipX - size * 1.6f, cy - size);
        spike.lineTo(tipX - size * 1.1f, cy);
        spike.lineTo(tipX - size * 1.6f, cy + size);
        spike.closePath();
        g.setColor(Theme.withAlpha(Color.BLACK, 200));
        g.translate(3, 3);
        g.fill(spike);
        g.translate(-3, -3);
        g.setPaint(new GradientPaint(0, cy - size, FIRE_TOP, 0, cy + size, FIRE_BOTTOM));
        g.fill(spike);
    }
}
