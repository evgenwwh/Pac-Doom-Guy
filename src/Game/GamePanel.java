package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, MouseMotionListener {
    JFrame frame;
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

    public GamePanel(JFrame frame) {
        this.frame = frame;
        setBackground(Color.black);
        setLayout(null);
        setFocusable(true);
        addMouseMotionListener(this);

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

    public int getMapOffsetX() {
        return mapOffsetX;
    }

    public int getMapOffsetY() {
        return mapOffsetY;
    }

    public int[][] getMap() {
        return map;
    }

    public void startGame(int[][] map) {
        this.map = map;
        calculateMapOffset(map);
        checker = new Checker(map, mapOffsetX, mapOffsetY);

        Point spawnPoint = findSpawnPoint(map);
        player = new Player(this, checker, spawnPoint.x, spawnPoint.y);
        add(player.getPlayerLabel());
        addKeyListener(player);
        requestFocusInWindow();

        addGhosts();  // Initialize and add ghosts here

        JPanel mapPanel = MapManager.createMapPanel(map);
        mapPanel.setBounds(mapOffsetX, mapOffsetY, map[0].length * MapManager.cellSize, map.length * MapManager.cellSize);
        add(mapPanel);

        panelGrid = MapManager.getPanelGrid();
        new CustomTimer(1000, this::updateGameTime).start();

        revalidate();
        repaint();
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void addGhosts() {
        // Ensure ghost initial positions are within the map bounds
        ghosts.add(new Demons(this, checker, 516, 754));
        ghosts.add(new Demons(this, checker, 1373, 339));
        ghosts.add(new Demons(this, checker, 1375, 338));

        for (Demons ghost : ghosts) {
            add(ghost.getGhostLabel());
        }
    }

    private void updateGameTime() {
        gameTime++;
        SwingUtilities.invokeLater(() -> {
            int minutes = gameTime / 60;
            int seconds = gameTime % 60;
            timeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        });
    }

    public void incrementScore(int points) {
        score += points;
        SwingUtilities.invokeLater(() -> scoreLabel.setText("Score: " + score));
    }

    public void gameOver() {
        gameThread = null;
        for (Demons ghost : ghosts) {
            ghost.stop();
        }
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



