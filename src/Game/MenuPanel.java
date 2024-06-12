package Game;

import javax.swing.*;
import java.awt.*;

public class MenuPanel {
    JButton newGameButton;
    MenuPanel menu;
    JButton exitButton;
    JButton recordButton;
    JPanel mainPanel = new JPanel(new GridBagLayout());
    JLabel nameOfGame;
    JFrame frame;
    GamePanel gamePanel;
    PanelSwitcher panelSwitcher;

    public MenuPanel(JFrame frame) {
        this.frame = frame;
        this.panelSwitcher = new PanelSwitcher(frame,this);
        this.gamePanel = new GamePanel(frame, panelSwitcher);
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
        map1Button.addActionListener(e -> panelSwitcher.switchToGamePanel("Argent Nur"));
        map2Button.addActionListener(e -> panelSwitcher.switchToGamePanel("Mars"));
        map3Button.addActionListener(e -> panelSwitcher.switchToGamePanel("HELL"));
        map4Button.addActionListener(e -> panelSwitcher.switchToGamePanel("Space ship"));
        map5Button.addActionListener(e -> panelSwitcher.switchToGamePanel("Earth"));

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







