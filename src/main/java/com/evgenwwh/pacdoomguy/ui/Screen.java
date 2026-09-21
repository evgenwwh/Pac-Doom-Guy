package com.evgenwwh.pacdoomguy.ui;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

/** Base for full-window screens: dark background with a red vignette. */
public abstract class Screen extends JPanel {
    protected Screen(LayoutManager layout) {
        super(layout);
        setBackground(Theme.BG);
        setOpaque(true);
    }

    /** Called by {@link MainWindow} every time this screen becomes visible. */
    public void onShown() {
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0.create();
        int w = getWidth();
        int h = getHeight();
        float radius = Math.max(w, h) * 0.75f;
        RadialGradientPaint glow = new RadialGradientPaint(
                new Point2D.Float(w / 2f, h * 0.35f), radius,
                new float[] {0f, 0.55f, 1f},
                new Color[] {Theme.withAlpha(Theme.ACCENT_DARK, 70), Theme.withAlpha(Theme.ACCENT_DARK, 15), Theme.BG});
        g.setPaint(glow);
        g.fillRect(0, 0, w, h);
        g.dispose();
    }
}
