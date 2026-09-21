package com.evgenwwh.pacdoomguy.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public final class MenuScreen extends Screen {
    private final DoomButton newGame;

    public MenuScreen(MainWindow window) {
        super(new GridBagLayout());

        JPanel column = new JPanel();
        column.setOpaque(false);
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));

        newGame = new DoomButton("New game", true);
        DoomButton records = new DoomButton("Records");
        DoomButton exit = new DoomButton("Exit");
        newGame.addActionListener(e -> window.showLevelSelect());
        records.addActionListener(e -> window.showRecords());
        exit.addActionListener(e -> System.exit(0));

        JLabel subtitle = new JLabel("A PAC-MAN IN HELL");
        subtitle.setFont(Theme.body(14));
        subtitle.setForeground(Theme.MUTED);

        for (JComponent c : new JComponent[] {subtitle, newGame, records, exit}) {
            c.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        GlowTitle title = new GlowTitle("Pac-Doom Guy", 96);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        column.add(title);
        column.add(subtitle);
        column.add(Box.createVerticalStrut(48));
        column.add(newGame);
        column.add(Box.createVerticalStrut(4));
        column.add(records);
        column.add(Box.createVerticalStrut(4));
        column.add(exit);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        add(column, gbc);
    }

    @Override
    public void onShown() {
        newGame.requestFocusInWindow();
    }
}
