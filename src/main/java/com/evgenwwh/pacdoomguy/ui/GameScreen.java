package com.evgenwwh.pacdoomguy.ui;

import com.evgenwwh.pacdoomguy.model.Demon;
import com.evgenwwh.pacdoomguy.model.Direction;
import com.evgenwwh.pacdoomguy.model.Effect;
import com.evgenwwh.pacdoomguy.model.GameSession;
import com.evgenwwh.pacdoomguy.model.GameState;
import com.evgenwwh.pacdoomguy.model.Level;
import com.evgenwwh.pacdoomguy.model.Player;
import com.evgenwwh.pacdoomguy.model.item.Item;
import com.evgenwwh.pacdoomguy.stats.Record;
import com.evgenwwh.pacdoomguy.stats.RecordsRepository;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.prefs.Preferences;

/**
 * Runs a {@link GameSession} at 60 Hz on the Swing timer and paints it with Graphics2D.
 * The map is scaled to fit the window, so the game works on any screen size.
 */
public final class GameScreen extends Screen {
    private static final int HUD_H = 104;
    private static final int PAD = 24;
    private static final double MAX_SCALE = 1.6;

    private final MainWindow window;
    private final RecordsRepository repository;
    private final Timer timer = new Timer(1000 / GameSession.TICKS_PER_SECOND, e -> tick());
    private final EndPanel endPanel = new EndPanel();
    private final PausePanel pausePanel = new PausePanel();

    private Level level;
    private GameSession session;
    private LevelRenderer renderer;
    private int lastLives;
    private int hitFlash;
    private int shake;
    private long frame;

    public GameScreen(MainWindow window, RecordsRepository repository) {
        super(null);
        this.window = window;
        this.repository = repository;
        setFocusable(true);
        add(endPanel);
        add(pausePanel);
        endPanel.setVisible(false);
        pausePanel.setVisible(false);
        bindKeys();
    }

    // ---- lifecycle -----------------------------------------------------------------------------

    public void start(Level level) {
        this.level = level;
        this.session = new GameSession(level);
        this.renderer = new LevelRenderer(level);
        lastLives = session.lives();
        hitFlash = 0;
        shake = 0;
        endPanel.setVisible(false);
        pausePanel.setVisible(false);
        timer.start();
        requestFocusInWindow();
        repaint();
    }

    public void stop() {
        timer.stop();
    }

    /** Called when the window loses focus so the player is not killed while alt-tabbed. */
    public void pauseIfRunning() {
        if (session != null && session.state() == GameState.RUNNING) {
            togglePause();
        }
    }

    private void togglePause() {
        session.togglePause();
        boolean paused = session.state() == GameState.PAUSED;
        pausePanel.setVisible(paused);
        if (paused) {
            pausePanel.focusResume();
        } else {
            requestFocusInWindow();
        }
        repaint();
    }

    private void tick() {
        if (session == null) {
            return;
        }
        frame++;
        session.update();
        if (session.lives() < lastLives) {
            hitFlash = 18;
            shake = 14;
        }
        lastLives = session.lives();
        if (hitFlash > 0) {
            hitFlash--;
        }
        if (shake > 0) {
            shake--;
        }
        if (session.state().isOver() && !endPanel.isVisible()) {
            timer.stop();
            endPanel.show(session, level);
        }
        repaint();
    }

    private void bindKeys() {
        bind("UP", () -> session.setDesiredDirection(Direction.UP), "UP", "W");
        bind("DOWN", () -> session.setDesiredDirection(Direction.DOWN), "DOWN", "S");
        bind("LEFT", () -> session.setDesiredDirection(Direction.LEFT), "LEFT", "A");
        bind("RIGHT", () -> session.setDesiredDirection(Direction.RIGHT), "RIGHT", "D");
        // ESC must also work while a pause-menu button has focus, hence the window-wide scope.
        bind(WHEN_IN_FOCUSED_WINDOW, "PAUSE", this::togglePause, "ESCAPE", "P");
    }

    private void bind(String name, Runnable action, String... keys) {
        bind(WHEN_FOCUSED, name, action, keys);
    }

    private void bind(int scope, String name, Runnable action, String... keys) {
        getActionMap().put(name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (session != null) {
                    action.run();
                }
            }
        });
        for (String key : keys) {
            getInputMap(scope).put(KeyStroke.getKeyStroke(key), name);
        }
    }

    @Override
    public void doLayout() {
        Dimension d = endPanel.getPreferredSize();
        endPanel.setBounds((getWidth() - d.width) / 2, (getHeight() - d.height) / 2, d.width, d.height);
        pausePanel.setBounds(0, 0, getWidth(), getHeight());
    }

    // ---- painting ------------------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        if (session == null) {
            return;
        }
        Graphics2D g = (Graphics2D) g0.create();
        Theme.prepare(g);
        int w = getWidth();
        int h = getHeight();

        double availW = w - 2 * PAD;
        double availH = h - HUD_H - 2 * PAD;
        double scale = Math.min(Math.min(availW / level.widthPx(), availH / level.heightPx()), MAX_SCALE);
        double mapW = level.widthPx() * scale;
        double mapH = level.heightPx() * scale;
        double ox = (w - mapW) / 2 + Math.sin(frame * 1.9) * shake * 0.5;
        double oy = HUD_H + PAD + (availH - mapH) / 2 + Math.cos(frame * 2.3) * shake * 0.5;

        // Frame around the maze.
        g.setColor(Theme.withAlpha(Color.BLACK, 120));
        g.fillRect((int) ox - 8, (int) oy - 8, (int) mapW + 16, (int) mapH + 16);
        g.setColor(Theme.BORDER);
        g.drawRect((int) ox - 8, (int) oy - 8, (int) mapW + 15, (int) mapH + 15);

        AffineTransform saved = g.getTransform();
        g.translate(ox, oy);
        g.scale(scale, scale);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(renderer.background(), 0, 0, null);
        renderer.drawDots(g, session);
        drawItems(g);
        drawDemons(g);
        drawPlayer(g);
        g.setTransform(saved);

        drawHud(g, w);

        if (hitFlash > 0) {
            g.setColor(Theme.withAlpha(Theme.ACCENT, hitFlash * 7));
            g.fillRect(0, 0, w, h);
        }
        if (session.state() == GameState.PAUSED || session.state().isOver()) {
            g.setColor(Theme.withAlpha(Color.BLACK, 170));
            g.fillRect(0, 0, w, h);
        }
        g.dispose();
    }

    private void drawItems(Graphics2D g) {
        for (Item item : session.items()) {
            boolean expiring = item.ticksLeft() < 3 * GameSession.TICKS_PER_SECOND;
            if (expiring && (frame / 6) % 2 == 0) {
                continue;
            }
            double bob = Math.sin((frame + System.identityHashCode(item) % 60) * 0.12) * 3;
            Color glow = effectColor(item.effect());
            double cx = item.centerX();
            double cy = item.centerY() + bob;
            g.setColor(Theme.withAlpha(glow, 60));
            g.fill(new Ellipse2D.Double(cx - 22, cy - 22, 44, 44));
            g.setColor(Theme.withAlpha(glow, 110));
            g.fill(new Ellipse2D.Double(cx - 14, cy - 14, 28, 28));
            BufferedImage sprite = Sprites.item(item.effect());
            g.drawImage(sprite, (int) Math.round(cx - sprite.getWidth() / 2.0),
                    (int) Math.round(cy - sprite.getHeight() / 2.0), null);
        }
    }

    private void drawDemons(Graphics2D g) {
        boolean vulnerable = session.hasEffect(Effect.KILL);
        for (Demon demon : session.demons()) {
            if (!demon.isAlive()) {
                continue;
            }
            BufferedImage sprite = Sprites.demon(demon.variant());
            double cx = demon.centerX();
            double cy = demon.centerY();
            g.setColor(Theme.withAlpha(Color.BLACK, 90));
            g.fill(new Ellipse2D.Double(cx - 12, demon.y() + demon.height() - 5, 24, 9));
            if (vulnerable) {
                float pulse = (float) (0.5 + 0.5 * Math.sin(frame * 0.3));
                g.setColor(Theme.withAlpha(Theme.CYAN, (int) (60 + 80 * pulse)));
                g.setStroke(new BasicStroke(2));
                g.draw(new Ellipse2D.Double(cx - 20, cy - 20, 40, 40));
            }
            g.drawImage(sprite, (int) Math.round(cx - sprite.getWidth() / 2.0),
                    (int) Math.round(cy - sprite.getHeight() / 2.0), null);
        }
    }

    private void drawPlayer(Graphics2D g) {
        Player p = session.player();
        double cx = p.centerX();
        double cy = p.centerY();
        g.setColor(Theme.withAlpha(Color.BLACK, 90));
        g.fill(new Ellipse2D.Double(cx - 12, p.y() + p.height() - 5, 24, 9));

        int ring = 0;
        for (Effect effect : Effect.values()) {
            if (session.hasEffect(effect)) {
                double r = 24 + ring * 5 + Math.sin(frame * 0.25 + ring) * 1.5;
                g.setColor(Theme.withAlpha(effectColor(effect), 170));
                g.setStroke(new BasicStroke(2.5f));
                g.draw(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
                ring++;
            }
        }

        Composite old = g.getComposite();
        if (p.isSpawnInvulnerable() && (frame / 5) % 2 == 0) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f));
        }
        g.drawImage(Sprites.player(p.facing(), p.frame()), (int) Math.round(p.x()), (int) Math.round(p.y()), null);
        g.setComposite(old);
    }

    private void drawHud(Graphics2D g, int w) {
        g.setPaint(new GradientPaint(0, 0, Theme.BG_ELEVATED, 0, HUD_H, Theme.BG));
        g.fillRect(0, 0, w, HUD_H);
        g.setColor(Theme.BORDER);
        g.drawLine(0, HUD_H - 1, w, HUD_H - 1);

        // Left: lives, score, time.
        BufferedImage hp = Sprites.health(session.lives());
        int hpH = 72;
        int hpW = hp.getWidth() * hpH / hp.getHeight();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(hp, PAD, (HUD_H - hpH) / 2, hpW, hpH, null);

        int x = PAD + hpW + 28;
        drawStat(g, x, 22, Sprites.get("hud_dot"), String.valueOf(session.score()), "SCORE");
        drawStat(g, x + 150, 22, Sprites.get("hud_time"), formatTime(session.elapsedSeconds()), "TIME");

        // Centre: level name and progress.
        g.setFont(Theme.title(22));
        g.setColor(Theme.TEXT);
        FontMetrics fm = g.getFontMetrics();
        String name = level.name().toUpperCase();
        g.drawString(name, (w - fm.stringWidth(name)) / 2, 40);
        int barW = 240;
        int barX = (w - barW) / 2;
        int barY = 54;
        g.setColor(Theme.PANEL);
        g.fillRect(barX, barY, barW, 8);
        double progress = 1.0 - (double) session.dotsLeft() / level.totalDots();
        g.setColor(Theme.ACCENT);
        g.fillRect(barX, barY, (int) (barW * progress), 8);
        g.setFont(Theme.body(11));
        g.setColor(Theme.MUTED);
        String left = session.dotsLeft() + " DOTS LEFT";
        fm = g.getFontMetrics();
        g.drawString(left, (w - fm.stringWidth(left)) / 2, barY + 24);

        // Right: effect slots with remaining-time rings.
        int slot = 56;
        int sx = w - PAD - Effect.values().length * (slot + 14) + 14;
        for (Effect effect : Effect.values()) {
            int cy = HUD_H / 2;
            boolean active = session.hasEffect(effect);
            BufferedImage icon = Sprites.hudIcon(effect);
            Composite old = g.getComposite();
            if (!active) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.22f));
            }
            g.drawImage(icon, sx + (slot - icon.getWidth()) / 2, cy - icon.getHeight() / 2, null);
            g.setComposite(old);
            if (active) {
                g.setColor(effectColor(effect));
                g.setStroke(new BasicStroke(3));
                double extent = -360 * session.effectProgress(effect);
                g.draw(new Arc2D.Double(sx + 1, cy - slot / 2.0 + 1, slot - 2, slot - 2, 90, extent, Arc2D.OPEN));
            }
            sx += slot + 14;
        }
    }

    private void drawStat(Graphics2D g, int x, int y, BufferedImage icon, String value, String label) {
        g.drawImage(icon, x, y + 4, null);
        g.setFont(Theme.title(28));
        g.setColor(Theme.TEXT);
        g.drawString(value, x + icon.getWidth() + 10, y + 30);
        g.setFont(Theme.body(11));
        g.setColor(Theme.MUTED);
        g.drawString(label, x + icon.getWidth() + 10, y + 50);
    }

    private static Color effectColor(Effect effect) {
        return switch (effect) {
            case SHIELD -> Theme.CYAN;
            case SPEED -> Theme.ORANGE;
            case KILL -> Theme.ACCENT;
        };
    }

    private static String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    // ---- pause menu ----------------------------------------------------------------------------

    /** Transparent full-screen panel with the pause menu in the middle. */
    private final class PausePanel extends JPanel {
        private final DoomButton resume = new DoomButton("Resume");

        PausePanel() {
            super(new GridBagLayout());
            setOpaque(false);
            DoomButton restart = new DoomButton("Restart");
            DoomButton menu = new DoomButton("Main menu");
            resume.addActionListener(e -> togglePause());
            restart.addActionListener(e -> start(level));
            menu.addActionListener(e -> window.showMenu());

            JLabel title = new JLabel("PAUSED", JLabel.CENTER);
            title.setFont(Theme.title(72));
            title.setForeground(Theme.TEXT);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.insets = new Insets(0, 0, 24, 0);
            add(title, c);
            c.insets = new Insets(2, 0, 2, 0);
            add(resume, c);
            add(restart, c);
            add(menu, c);
        }

        void focusResume() {
            // The panel has only just been made visible; focus can be requested once it is laid out.
            javax.swing.SwingUtilities.invokeLater(resume::requestFocusInWindow);
        }
    }

    // ---- end-of-game panel ---------------------------------------------------------------------

    /** Result card: outcome, stats, name entry and navigation. */
    private final class EndPanel extends JPanel {
        private final JLabel title = new JLabel("", JLabel.CENTER);
        private final JLabel stats = new JLabel("", JLabel.CENTER);
        private final JTextField name = new JTextField(18);
        private final DoomButton save = new DoomButton("Save record", true);
        private final JLabel status = new JLabel(" ", JLabel.CENTER);
        private GameSession finished;
        private Level finishedLevel;

        EndPanel() {
            super(new GridBagLayout());
            setOpaque(false);
            setPreferredSize(new Dimension(520, 400));

            title.setFont(Theme.title(56));
            stats.setFont(Theme.body(15));
            stats.setForeground(Theme.TEXT);
            status.setFont(Theme.body(12));
            status.setForeground(Theme.MUTED);

            name.setFont(Theme.body(16));
            name.setBackground(Theme.BG);
            name.setForeground(Theme.TEXT);
            name.setCaretColor(Theme.TEXT);
            name.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDER),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)));
            name.addActionListener(e -> saveRecord());
            save.addActionListener(e -> saveRecord());

            DoomButton restart = new DoomButton("Play again");
            DoomButton menu = new DoomButton("Main menu");
            restart.addActionListener(e -> start(finishedLevel));
            menu.addActionListener(e -> window.showMenu());

            JLabel nameLabel = new JLabel("YOUR NAME");
            nameLabel.setFont(Theme.body(11));
            nameLabel.setForeground(Theme.MUTED);

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(6, 30, 6, 30);
            add(title, c);
            add(stats, c);
            c.insets = new Insets(18, 30, 2, 30);
            add(nameLabel, c);
            c.insets = new Insets(2, 30, 6, 30);
            add(name, c);
            add(save, c);
            add(status, c);
            c.gridwidth = 1;
            c.insets = new Insets(6, 30, 12, 6);
            add(restart, c);
            c.gridx = 1;
            c.insets = new Insets(6, 6, 12, 30);
            add(menu, c);
        }

        void show(GameSession session, Level level) {
            finished = session;
            finishedLevel = level;
            boolean won = session.state() == GameState.WON;
            title.setText(won ? "LEVEL CLEARED" : "YOU DIED");
            title.setForeground(won ? Theme.GOLD : Theme.ACCENT);
            stats.setText(String.format("%s   ·   SCORE %d   ·   TIME %s",
                    level.name().toUpperCase(), session.score(), formatTime(session.elapsedSeconds())));
            name.setText(Preferences.userNodeForPackage(GameScreen.class).get("playerName", ""));
            name.setEnabled(true);
            save.setEnabled(true);
            status.setText(" ");
            setVisible(true);
            name.requestFocusInWindow();
            name.selectAll();
        }

        private void saveRecord() {
            if (!save.isEnabled()) {
                return;
            }
            String playerName = name.getText().trim();
            Preferences.userNodeForPackage(GameScreen.class).put("playerName", playerName);
            Record record = new Record(playerName, finishedLevel.name(), finished.score(),
                    finished.elapsedSeconds(), finished.state() == GameState.WON, LocalDate.now());
            try {
                repository.save(record);
                status.setText("Saved to hall of fame");
                save.setEnabled(false);
                name.setEnabled(false);
            } catch (IOException e) {
                status.setText("Could not save: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setColor(Theme.withAlpha(Theme.BG_ELEVATED, 240));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Theme.ACCENT);
            g.setStroke(new BasicStroke(2));
            g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
            g.dispose();
        }
    }
}
