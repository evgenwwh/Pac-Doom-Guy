package com.evgenwwh.pacdoomguy.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/** Colours and fonts shared by every screen. Pure Swing, no external assets required. */
public final class Theme {
    private Theme() {
    }

    public static final Color BG = new Color(0x0b0b0e);
    public static final Color BG_ELEVATED = new Color(0x15151b);
    public static final Color PANEL = new Color(0x1c1c24);
    public static final Color BORDER = new Color(0x2c2c38);
    public static final Color TEXT = new Color(0xe8e6e3);
    public static final Color MUTED = new Color(0x8a8a96);
    public static final Color ACCENT = new Color(0xd7263d);
    public static final Color ACCENT_DARK = new Color(0x7a1424);
    public static final Color GOLD = new Color(0xf2b134);
    public static final Color CYAN = new Color(0x3fd2ff);
    public static final Color ORANGE = new Color(0xff7a1a);

    /** Bundled display font (Metal Mania, SIL OFL) - falls back to a heavy system font if it fails to load. */
    private static final Font TITLE_BASE = loadBundledFont("/fonts/MetalMania-Regular.ttf");
    private static final String TITLE_FALLBACK = pickFamily("Impact", "Arial Black", "Helvetica Neue", Font.SANS_SERIF);
    private static final String BODY_FAMILY = pickFamily("Menlo", "JetBrains Mono", "Consolas", "DejaVu Sans Mono", Font.MONOSPACED);

    public static Font title(float size) {
        if (TITLE_BASE != null) {
            return TITLE_BASE.deriveFont(Font.PLAIN, size);
        }
        return new Font(TITLE_FALLBACK, Font.BOLD, Math.round(size));
    }

    public static Font body(float size) {
        return new Font(BODY_FAMILY, Font.PLAIN, Math.round(size));
    }

    public static Font bodyBold(float size) {
        return new Font(BODY_FAMILY, Font.BOLD, Math.round(size));
    }

    /** Enables anti-aliasing for text and shapes. */
    public static void prepare(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, alpha)));
    }

    private static Font loadBundledFont(String path) {
        try (InputStream in = Theme.class.getResourceAsStream(path)) {
            if (in == null) {
                return null;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, in);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
            return font;
        } catch (IOException | FontFormatException e) {
            System.err.println("Could not load font " + path + ": " + e.getMessage());
            return null;
        }
    }

    private static String pickFamily(String... candidates) {
        Set<String> installed = Set.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        List<String> wanted = Arrays.asList(candidates);
        return wanted.stream().filter(installed::contains).findFirst().orElse(candidates[candidates.length - 1]);
    }
}
