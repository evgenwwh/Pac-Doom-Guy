package Game;

import javax.swing.*;
import java.awt.*;

public class PanelSwitcher {
    private JFrame frame;
    private MenuPanel menuPanel;
    GamePanel gamePanel;
    MapManager map;
    public PanelSwitcher(JFrame frame, MenuPanel menuPanel) {
        this.frame = frame;
        this.menuPanel = menuPanel;
        this.gamePanel = new GamePanel(frame, this);
    }

    public JPanel switchToMenu() {
        SwingUtilities.invokeLater(() -> {
            frame.getContentPane().removeAll();
            frame.add(menuPanel.startGame());
            frame.revalidate();
            frame.repaint();
        });
        return null;
    }
    public void switchToGamePanel(String mapName) {
        frame.getContentPane().removeAll();
        MapManager.reset();
        gamePanel = new GamePanel(frame, this);

            if (gamePanel == null) {
                gamePanel = new GamePanel(frame, this);
            }

            if ("Earth".equals(mapName)) {
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.startGame(MapManager.map5);
                gamePanel.requestFocusInWindow();
            }
            if ("Argent Nur".equals(mapName)) {
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.startGame(MapManager.map1);
                gamePanel.requestFocusInWindow();
            }
            if ("Mars".equals(mapName)) {
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.startGame(MapManager.map2);
                gamePanel.requestFocusInWindow();
            }
            if ("HELL".equals(mapName)) {
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.startGame(MapManager.map3);
                gamePanel.requestFocusInWindow();
            }
            if ("Space ship".equals(mapName)) {
                frame.getContentPane().removeAll();
                frame.add(gamePanel);
                frame.revalidate();
                frame.repaint();
                gamePanel.startGame(MapManager.map4);
                gamePanel.requestFocusInWindow();
            }
        }
    }



