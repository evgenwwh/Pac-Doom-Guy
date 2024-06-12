package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, MouseMotionListener {
    JFrame frame;
    JLabel healthIndicator;
    int health = 4;
    Thread gameThread;
    Player player;
    Checker checker;
    int[][] map;
    JLabel mouseCoordinatesLabel;
    JPanel[][] panelGrid;
    int mapOffsetX, mapOffsetY;
    JLabel scoreLabel, timeLabel;
    int score = 0, gameTime = 0;
    ArrayList<Demons> ghosts;
    private boolean isPaused = false;
    private JPanel pausePanel;
    private CustomTimer gameTimer;
    MenuPanel menu;
    PanelSwitcher panelSwitcher;

    public GamePanel(JFrame frame,PanelSwitcher panelSwitcher) {
        this.frame = frame;
        this.panelSwitcher = panelSwitcher;
        setBackground(Color.black);
        setLayout(null);
        setFocusable(true);
        addMouseMotionListener(this);
        initializePausePanel();
        initializeHealthIndicator();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });

        mouseCoordinatesLabel = new JLabel();
        mouseCoordinatesLabel.setBounds(10, 10, 100, 20);
        add(mouseCoordinatesLabel);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setBounds(10, 30, 200, 30);
        scoreLabel.setForeground(Color.white);
        add(scoreLabel);

        timeLabel = new JLabel("Time: 00:00");
        timeLabel.setBounds(10, 60, 200, 30);
        timeLabel.setForeground(Color.white);
        add(timeLabel);

        ghosts = new ArrayList<>();
    }








    private void initializeHealthIndicator() {
        healthIndicator = new JLabel(new ImageIcon(getClass().getResource("/images/healthIndicator/hp1.png")));
        healthIndicator.setBounds(10, 100, 50, 50);
        add(healthIndicator);
    }

    private void initializePausePanel() {
        pausePanel = new JPanel(new GridBagLayout());
        pausePanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        pausePanel.setBackground(new Color(0, 0, 0, 150));
        pausePanel.setVisible(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components

        // Add "PAUSED" label
        JLabel pausedLabel = new JLabel("PAUSED");
        pausedLabel.setFont(new Font("Arial", Font.BOLD, 48));
        pausedLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        pausePanel.add(pausedLabel, gbc);

        // Add Resume button
        JButton resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> togglePause());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pausePanel.add(resumeButton, gbc);

        // Add Menu button
        JButton menuButton = new JButton("Main Menu");
        menuButton.addActionListener(e -> {
           panelSwitcher.switchToMenu();
        });

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        pausePanel.add(menuButton, gbc);

        setComponentZOrder(pausePanel, 0); // Ensure the pause panel is on top
        add(pausePanel);
    }


    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pausePanel.setVisible(true);
            gameTimer.stop();
            for (Demons ghost : ghosts) {
                ghost.pause();
            }
        } else {
            gameTimer.resume();
            pausePanel.setVisible(false);
            for (Demons ghost : ghosts) {
                ghost.resume();
            }
            requestFocusInWindow();
        }
    }

    public void playerTouchedByGhost() {
        if (!player.isInvulnerable()) {
            health--;
            System.out.println("Lives left: " + health);
            updateHealthIndicator();
            if (health > 0) {
                player.respawn(); // Ensure this method resets the invulnerability correctly
            } else {
                gameOver();
            }
        }
    }

    public void updateHealthIndicator() {
        switch (health) {
            case 4:
                healthIndicator.setIcon(new ImageIcon(getClass().getResource("/images/healthIndicator/hp1.png")));
                break;
            case 3:
                healthIndicator.setIcon(new ImageIcon(getClass().getResource("/images/healthIndicator/hp2.png")));
                break;
            case 2:
                healthIndicator.setIcon(new ImageIcon(getClass().getResource("/images/healthIndicator/hp3.png")));
                break;
            case 1:
                healthIndicator.setIcon(new ImageIcon(getClass().getResource("/images/healthIndicator/hp4.png")));
                break;
            default:
                gameOver();
                break;
        }
    }

    public int[][] getMap() {
        return map;
    }

    public void startGame(int[][] map) {
        this.map = MapManager.copyMap(map);
        calculateMapOffset(map);
        checker = new Checker(map, mapOffsetX, mapOffsetY);

        Point spawnPoint = findSpawnPoint(map);
        player = new Player(this, checker, spawnPoint.x, spawnPoint.y);
        add(player.getPlayerLabel());
        addKeyListener(player);
        requestFocusInWindow();

        addGhosts(); // Initialize and add ghosts here

        JPanel mapPanel = MapManager.createMapPanel(map);
        mapPanel.setBounds(mapOffsetX, mapOffsetY, map[0].length * MapManager.cellSize, map.length * MapManager.cellSize);
        add(mapPanel);

        panelGrid = MapManager.getPanelGrid();
        gameTimer = new CustomTimer(1000, this::updateGameTime);
        gameTimer.start();

        revalidate();
        repaint();
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void addGhosts() {
        // Ensure ghost initial positions are within the map bounds
        ghosts.add(new Demons(this, checker, 516, 754));
        ghosts.add(new Demons(this, checker, 1009, 382));
        ghosts.add(new Demons(this, checker, 1247, 585));

        for (Demons ghost : ghosts) {
            add(ghost.getGhostLabel());
        }
    }

    private void updateGameTime() {
        if (!isPaused) {
            gameTime++;
            SwingUtilities.invokeLater(() -> {
                int minutes = gameTime / 60;
                int seconds = gameTime % 60;
                timeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
            });
        }
    }

    public void incrementScore(int points) {
        score += points;
        SwingUtilities.invokeLater(() -> scoreLabel.setText("Score: " + score));
    }

    public void gameOver() {
        JOptionPane.showMessageDialog(this, "Game Over! Your score: " + score + ", Time: " + timeLabel.getText());
        System.exit(0);
    }

    public Point findSpawnPoint(int[][] map) {
        if (map == MapManager.map1) {
            return new Point(522, 334);
        }
        if (map == MapManager.map2) {
            return new Point(570, 159);
        }
        if (map == MapManager.map3) {
            return new Point(406, 156);
        }
        if (map == MapManager.map4) {
            return new Point(720, 130);
        }
        if (map == MapManager.map5) {
            return new Point(480, 170);
        }
        return null;
    }

    private void calculateMapOffset(int[][] map) {
        int mapWidth = map[0].length * MapManager.cellSize;
        int mapHeight = map.length * MapManager.cellSize;
        mapOffsetX = (frame.getWidth() - mapWidth) / 2;
        mapOffsetY = (frame.getHeight() - mapHeight) / 2;
    }

    @Override
    public void run() {
        while (gameThread != null) {
            try {
                Thread.sleep(16); // Approx. 60 frames per second
                player.isInvulnerable();
                player.update();
                repaint();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Implement as needed for mouse interactions
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseCoordinatesLabel.setText("X: " + e.getX() + " Y: " + e.getY());
    }
}



