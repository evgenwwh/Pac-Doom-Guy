import Game.MenuPanel;

import javax.swing.*;
import java.awt.*;




public class MainFrame extends JFrame {
        public MainFrame() {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1920, 1080);
            setLocationRelativeTo(null);
            setTitle("Pac-DoomGuy");
            getContentPane().setBackground(Color.black);

            MenuPanel menuPanel = new MenuPanel(this);
            add(menuPanel.startGame());
            setVisible(true);
        }

}



