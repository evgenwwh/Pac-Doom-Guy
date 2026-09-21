package com.evgenwwh.pacdoomguy.ui;

import javax.swing.JComponent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

/** Large title text with a soft red glow behind it. */
public final class GlowTitle extends JComponent {
    private final String text;
    private final Font font;
    private final Color color;

    public GlowTitle(String text, float size) {
        this(text, size, Theme.TEXT);
    }

    public GlowTitle(String text, float size, Color color) {
        this.text = text.toUpperCase();
        this.font = Theme.title(size);
        this.color = color;
        FontMetrics fm = getFontMetrics(font);
        setPreferredSize(new Dimension(fm.stringWidth(this.text) + 40, fm.getHeight() + 20));
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.prepare(g);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        for (int r = 6; r >= 1; r--) {
            g.setColor(Theme.withAlpha(Theme.ACCENT, 18));
            for (int dx = -r; dx <= r; dx += r) {
                for (int dy = -r; dy <= r; dy += r) {
                    g.drawString(text, x + dx, y + dy);
                }
            }
        }
        g.setColor(Theme.ACCENT_DARK);
        g.drawString(text, x + 3, y + 3);
        g.setColor(color);
        g.drawString(text, x, y);
        g.dispose();
    }
}
