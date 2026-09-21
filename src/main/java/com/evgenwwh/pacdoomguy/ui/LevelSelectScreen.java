package com.evgenwwh.pacdoomguy.ui;

import com.evgenwwh.pacdoomguy.model.Level;
import com.evgenwwh.pacdoomguy.model.Levels;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Grid of level cards with a live map preview. Arrow keys or mouse select, Enter/click starts. */
public final class LevelSelectScreen extends Screen {
    private final LevelCard first;

    public LevelSelectScreen(MainWindow window) {
        super(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 28));
        header.setOpaque(false);
        header.add(new GlowTitle("Choose location", 56));
        add(header, BorderLayout.NORTH);

        JPanel grid = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        grid.setOpaque(false);
        LevelCard firstCard = null;
        for (Level level : Levels.ALL) {
            LevelCard card = new LevelCard(level, () -> window.startGame(level));
            if (firstCard == null) {
                firstCard = card;
            }
            grid.add(card);
        }
        first = firstCard;
        JPanel gridWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        gridWrap.setOpaque(false);
        gridWrap.add(grid);
        grid.setPreferredSize(new Dimension(3 * (LevelCard.W + 20) + 20, 2 * (LevelCard.H + 12) + 12));
        add(gridWrap, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 24));
        footer.setOpaque(false);
        DoomButton back = new DoomButton("Back");
        back.addActionListener(e -> window.showMenu());
        footer.add(back);
        add(footer, BorderLayout.SOUTH);
    }

    @Override
    public void onShown() {
        first.requestFocusInWindow();
    }

    /** One selectable level. */
    private static final class LevelCard extends JComponent {
        static final int W = 300;
        static final int H = 250;
        private final Level level;
        private boolean hover;

        LevelCard(Level level, Runnable onSelect) {
            this.level = level;
            setPreferredSize(new Dimension(W, H));
            setFocusable(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                    onSelect.run();
                }
            });
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_ENTER, KeyEvent.VK_SPACE -> onSelect.run();
                        case KeyEvent.VK_LEFT, KeyEvent.VK_UP, KeyEvent.VK_A, KeyEvent.VK_W -> transferFocusBackward();
                        case KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_D, KeyEvent.VK_S -> transferFocus();
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
            boolean hot = hover || hasFocus();
            int w = getWidth();
            int h = getHeight();

            g.setColor(hot ? Theme.PANEL : Theme.BG_ELEVATED);
            g.fillRect(0, 0, w, h);
            g.setColor(hot ? Theme.ACCENT : Theme.BORDER);
            g.drawRect(0, 0, w - 1, h - 1);
            if (hot) {
                g.drawRect(1, 1, w - 3, h - 3);
            }

            int pad = 14;
            g.setColor(Theme.BG);
            g.fillRect(pad, pad, w - 2 * pad, 150);
            LevelRenderer.drawPreview(g, level, pad + 6, pad + 6, w - 2 * pad - 12, 150 - 12);

            g.setFont(Theme.title(26));
            g.setColor(hot ? Theme.TEXT : Theme.MUTED);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(level.name().toUpperCase(), pad, pad + 150 + 12 + fm.getAscent());

            g.setFont(Theme.body(12));
            g.setColor(Theme.MUTED);
            String info = level.cols() + "x" + level.rows() + "   " + level.totalDots() + " dots   "
                    + level.demonSpawns().size() + " demons";
            g.drawString(info, pad, h - pad - 4);
            g.dispose();
        }
    }
}
