package com.evgenwwh.pacdoomguy;

import com.evgenwwh.pacdoomguy.ui.MainWindow;

import javax.swing.SwingUtilities;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        System.setProperty("apple.awt.application.name", "Pac-Doom Guy");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
