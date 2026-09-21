package com.evgenwwh.pacdoomguy.ui;

import com.evgenwwh.pacdoomguy.stats.Record;
import com.evgenwwh.pacdoomguy.stats.RecordsRepository;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

/** Top-10 leaderboard. */
public final class RecordsScreen extends Screen {
    private static final int TOP = 10;

    private final RecordsRepository repository;
    private final Table table = new Table();
    private final DoomButton back;
    private List<Record> records = List.of();

    public RecordsScreen(MainWindow window, RecordsRepository repository) {
        super(new BorderLayout());
        this.repository = repository;

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 28));
        header.setOpaque(false);
        header.add(new GlowTitle("Hall of fame", 56));
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        center.add(table);
        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 24));
        footer.setOpaque(false);
        back = new DoomButton("Back");
        back.addActionListener(e -> window.showMenu());
        footer.add(back);
        add(footer, BorderLayout.SOUTH);
    }

    @Override
    public void onShown() {
        records = repository.top(TOP);
        table.repaint();
        back.requestFocusInWindow();
    }

    private final class Table extends JComponent {
        private static final int ROW = 36;
        private static final int[] COLS = {60, 260, 160, 100, 90, 70, 120};
        private static final String[] HEAD = {"#", "PLAYER", "LEVEL", "SCORE", "TIME", "", "DATE"};

        Table() {
            int w = 0;
            for (int c : COLS) {
                w += c;
            }
            setPreferredSize(new Dimension(w + 40, ROW * (TOP + 1) + 40));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Theme.prepare(g);
            int w = getWidth();
            int h = getHeight();
            g.setColor(Theme.withAlpha(Theme.BG_ELEVATED, 230));
            g.fillRect(0, 0, w, h);
            g.setColor(Theme.BORDER);
            g.drawRect(0, 0, w - 1, h - 1);

            int y = 20;
            g.setFont(Theme.bodyBold(13));
            g.setColor(Theme.MUTED);
            drawRow(g, y, HEAD);
            y += ROW;
            g.setColor(Theme.BORDER);
            g.drawLine(20, y - 6, w - 20, y - 6);

            if (records.isEmpty()) {
                g.setFont(Theme.body(14));
                g.setColor(Theme.MUTED);
                String msg = "No games played yet. Go clear a level!";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(msg, (w - fm.stringWidth(msg)) / 2, y + ROW * 2);
                g.dispose();
                return;
            }

            g.setFont(Theme.body(14));
            for (int i = 0; i < records.size(); i++) {
                Record r = records.get(i);
                if (i % 2 == 1) {
                    g.setColor(Theme.withAlpha(Theme.PANEL, 120));
                    g.fillRect(12, y - 4, w - 24, ROW);
                }
                g.setColor(i == 0 ? Theme.GOLD : Theme.TEXT);
                drawRow(g, y, new String[] {
                        String.valueOf(i + 1), r.playerName(), r.levelName(), String.valueOf(r.score()),
                        r.formattedTime(), r.won() ? "WIN" : "LOSS", r.date().toString()});
                y += ROW;
            }
            g.dispose();
        }

        private void drawRow(Graphics2D g, int y, String[] cells) {
            FontMetrics fm = g.getFontMetrics();
            int x = 20;
            int baseline = y + (ROW - fm.getHeight()) / 2 + fm.getAscent() - 4;
            for (int i = 0; i < cells.length; i++) {
                String text = cells[i];
                int max = COLS[i] - 12;
                while (fm.stringWidth(text) > max && text.length() > 1) {
                    text = text.substring(0, text.length() - 2) + "…";
                }
                g.drawString(text, x, baseline);
                x += COLS[i];
            }
        }
    }
}
