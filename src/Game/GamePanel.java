package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class GamePanel extends JPanel implements Runnable, MouseMotionListener {
    JFrame frame;
    Thread gameThread;
    Player player;
    Checker checker;
    int[][] map;
    JLabel mouseCoordinatesLabel;
    JPanel[][] panelGrid;
    int mapOffsetX;
    int mapOffsetY;
    JLabel scoreLabel;
    int score = 0;

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
    }

    public void startGame(int[][] map) {
        this.map = map;

        // Рассчитываем смещения карты относительно центра окна
        calculateMapOffset(map);

        // Создаем Checker с учетом этих смещений
        checker = new Checker(map, mapOffsetX, mapOffsetY);

        // Находим точку появления игрока
        Point spawnPoint = findSpawnPoint(map);
        int initialX = spawnPoint.x;
        int initialY = spawnPoint.y;

        // Создаем игрока с учетом точки появления и проверяющего объекта
        player = new Player(this, checker, initialX, initialY);
        add(player.getPlayerLabel());
        addKeyListener(player);
        requestFocusInWindow();

        // Создаем панель карты и размещаем ее
        JPanel mapPanel = MapManager.createMapPanel(map);
        mapPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        add(mapPanel);

        // Получаем таблицу панелей из MapManager
        panelGrid = MapManager.getPanelGrid();

        revalidate();
        repaint();
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void incrementScore(int points) {
        score += points;
        System.out.println("New score: " + score); // Добавить для проверки
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText("Score: " + score);
        });
    }

    public int[][] getMap() {
        return map;
    }

    public Point findSpawnPoint(int[][] map) {
        if (map == MapManager.map1) {
            return new Point(528, 306);
        }
        if (map == MapManager.map2) {
            return new Point(570,159);
        }
        if (map == MapManager.map3) {
            return new Point(406, 156);
        }
        if (map == MapManager.map4) {
            return new Point(720,130);
        }
        if (map == MapManager.map5) {
            return new Point(475, 170);
        }
        return null;
    }

    private void calculateMapOffset(int[][] map) {
        int mapWidth = map[0].length * MapManager.cellSize;
        int mapHeight = map.length * MapManager.cellSize;
        mapOffsetX = (1920 - mapWidth) / 2;
        mapOffsetY = (1080 - mapHeight) / 2;
        System.out.println("Map offset X: " + mapOffsetX + ", Map offset Y: " + mapOffsetY);
    }

    @Override
    public void run() {
        while (gameThread != null) {
            try {
                Thread.sleep(16); // Примерно 60 FPS
                if (player != null) {
                    player.update();
                    repaint();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Не используется
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseCoordinatesLabel.setText("X: " + e.getX() + " Y: " + e.getY());
    }
}








