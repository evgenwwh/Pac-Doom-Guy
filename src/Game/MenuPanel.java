package Game;

import javax.swing.*;
import java.awt.*;

public class MenuPanel {
    JButton newGameButton;
    JButton exitButton;
    JButton recordButton;
    JPanel mainPanel = new JPanel(new GridBagLayout());
    JLabel nameOfGame;
    JFrame menuFrame;
    GamePanel gamePanel;

    public MenuPanel(JFrame frame) {
        this.menuFrame = frame;
        this.gamePanel = new GamePanel(frame);
    }

    public JPanel startGame() {
        mainPanel.removeAll();

        newGameButton = new JButton("New Game");
        exitButton = new JButton("Exit");
        recordButton = new JButton("Records");

        newGameButton.setPreferredSize(new Dimension(100, 50));
        exitButton.setPreferredSize(new Dimension(100, 50));
        recordButton.setPreferredSize(new Dimension(100, 50));

        newGameButton.addActionListener(e -> pressNewGame());
        exitButton.addActionListener(e -> System.exit(0));

        nameOfGame = new JLabel("DOOM");
        nameOfGame.setFont(new Font("Arial", Font.ITALIC, 84));
        nameOfGame.setForeground(new Color(0xFFFFFF));

        GridBagConstraints buttons = new GridBagConstraints();
        buttons.insets = new Insets(50, 50, 50, 50);
        buttons.fill = GridBagConstraints.NONE;
        buttons.anchor = GridBagConstraints.CENTER;

        GridBagConstraints label = new GridBagConstraints();
        label.gridwidth = GridBagConstraints.REMAINDER;
        label.anchor = GridBagConstraints.CENTER;
        label.insets = new Insets(30, 30, 30, 30);
        mainPanel.setBackground(Color.black);
        mainPanel.add(nameOfGame, label);
        mainPanel.add(newGameButton, buttons);
        mainPanel.add(recordButton, buttons);
        mainPanel.add(exitButton, buttons);
        mainPanel.revalidate();
        mainPanel.repaint();
        return mainPanel;
    }

    private void switchToGamePanel(String mapName) {
        if ("Earth".equals(mapName)) {
            menuFrame.getContentPane().removeAll();
            menuFrame.add(gamePanel);
            menuFrame.revalidate();
            menuFrame.repaint();
            gamePanel.startGame(MapManager.map5);
            gamePanel.requestFocusInWindow();
        }if ("Argent Nur".equals(mapName)) {
            menuFrame.getContentPane().removeAll();
            menuFrame.add(gamePanel);
            menuFrame.revalidate();
            menuFrame.repaint();
            gamePanel.startGame(MapManager.map1);
            gamePanel.requestFocusInWindow();
        }
        if ("Mars".equals(mapName)) {
            menuFrame.getContentPane().removeAll();
            menuFrame.add(gamePanel);
            menuFrame.revalidate();
            menuFrame.repaint();
            gamePanel.startGame(MapManager.map2);
            gamePanel.requestFocusInWindow();
        }
        if ("HELL".equals(mapName)) {
            menuFrame.getContentPane().removeAll();
            menuFrame.add(gamePanel);
            menuFrame.revalidate();
            menuFrame.repaint();
            gamePanel.startGame(MapManager.map3);
            gamePanel.requestFocusInWindow();
        }
        if ("Earth".equals(mapName)) {
            menuFrame.getContentPane().removeAll();
            menuFrame.add(gamePanel);
            menuFrame.revalidate();
            menuFrame.repaint();
            gamePanel.startGame(MapManager.map4);
            gamePanel.requestFocusInWindow();
        }


//        menuFrame.getContentPane().removeAll();
//        gamePanel = new GamePanel(menuFrame); // Recreate GamePanel with new parameters
//        gamePanel.startGame();
//        menuFrame.add(gamePanel);
//        menuFrame.revalidate();
//        menuFrame.repaint();
//
//        gamePanel.requestFocusInWindow();
    }

    public JPanel pressNewGame() {
        mainPanel.removeAll();

        JButton map1Button = new JButton("Argent Nur");
        JButton map2Button = new JButton("Mars");
        JButton map3Button = new JButton("HELL");
        JButton map4Button = new JButton("Space ship");
        JButton map5Button = new JButton("Earth");
        JLabel text = new JLabel("Locations: ");
        JButton backButton = new JButton(" <- Back");

        map1Button.setPreferredSize(new Dimension(100, 50));
        map2Button.setPreferredSize(new Dimension(100, 50));
        map3Button.setPreferredSize(new Dimension(100, 50));
        map4Button.setPreferredSize(new Dimension(100, 50));
        map5Button.setPreferredSize(new Dimension(100, 50));
        backButton.setPreferredSize(new Dimension(100, 50));

        backButton.addActionListener(e -> startGame());
        map1Button.addActionListener(e -> switchToGamePanel("Argent Nur"));
        map2Button.addActionListener(e -> switchToGamePanel("Mars"));
        map3Button.addActionListener(e -> switchToGamePanel("HELL"));
        map4Button.addActionListener(e -> switchToGamePanel("Space ship"));
        map5Button.addActionListener(e -> switchToGamePanel("Earth"));

        text.setFont(new Font("Arial", Font.ITALIC, 84));
        text.setForeground(new Color(0xFFFFFF));

        GridBagConstraints buttons = new GridBagConstraints();
        buttons.insets = new Insets(20, 20, 20, 20);
        buttons.fill = GridBagConstraints.NONE;
        buttons.anchor = GridBagConstraints.CENTER;

        GridBagConstraints label = new GridBagConstraints();
        label.gridwidth = GridBagConstraints.REMAINDER;
        label.anchor = GridBagConstraints.CENTER;
        label.insets = new Insets(30, 30, 30, 30);

        mainPanel.setBackground(Color.black);
        mainPanel.add(text, label);
        mainPanel.add(map1Button, buttons);
        mainPanel.add(map2Button, buttons);
        mainPanel.add(map3Button, buttons);
        mainPanel.add(map4Button, buttons);
        mainPanel.add(map5Button, buttons);
        mainPanel.add(backButton, buttons);

        mainPanel.revalidate();
        mainPanel.repaint();
        return mainPanel;
    }
}







