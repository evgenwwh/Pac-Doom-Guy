package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Player implements KeyListener {
    GamePanel gamePanel;
    int playerX, playerY, playerSpeed, playerWidth, playerHeight;
    private ImageIcon upIcon1, downIcon1, leftIcon1, rightIcon1;
    private ImageIcon upIcon2, downIcon2, leftIcon2, rightIcon2;
    private ImageIcon currentIcon;
    private int counter, steps;
    private String currentKey = "";
    private JLabel playerLabel;
    private JLabel boundingBoxLabel;
    private Checker checker;

    public Player(GamePanel gamePanel, Checker checker, int initialX, int initialY) {
        this.gamePanel = gamePanel;
        this.checker = checker;
        this.playerX = initialX;
        this.playerY = initialY;
        playerLabel = new JLabel();
        boundingBoxLabel = new JLabel();
        boundingBoxLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
        getPlayerImage();
        initializePosition();
        gamePanel.add(playerLabel);
        gamePanel.add(boundingBoxLabel);
    }

    public JLabel getPlayerLabel() {
        return playerLabel;
    }

    private void animation() {
        steps++;
        if (steps > 15) {
            counter = (counter + 1) % 2;
            steps = 0;
            updateIcon();
        }
    }

    private void updateIcon() {
        switch (currentKey) {
            case "W":
                currentIcon = (counter == 0) ? upIcon1 : upIcon2;
                break;
            case "S":
                currentIcon = (counter == 0) ? downIcon1 : downIcon2;
                break;
            case "A":
                currentIcon = (counter == 0) ? leftIcon1 : leftIcon2;
                break;
            case "D":
                currentIcon = (counter == 0) ? rightIcon1 : rightIcon2;
                break;
            default:
                currentIcon = downIcon1;
        }
        playerLabel.setIcon(currentIcon);
    }

    public void getPlayerImage() {
        upIcon1 = new ImageIcon(getClass().getResource("/images/player/w_move1.png"));
        downIcon1 = new ImageIcon(getClass().getResource("/images/player/s_move1.png"));
        leftIcon1 = new ImageIcon(getClass().getResource("/images/player/a_move1.png"));
        rightIcon1 = new ImageIcon(getClass().getResource("/images/player/d_move1.png"));
        upIcon2 = new ImageIcon(getClass().getResource("/images/player/w_move2.png"));
        downIcon2 = new ImageIcon(getClass().getResource("/images/player/s_move2.png"));
        leftIcon2 = new ImageIcon(getClass().getResource("/images/player/a_move2.png"));
        rightIcon2 = new ImageIcon(getClass().getResource("/images/player/d_move2.png"));
    }

    private void moveCharacter() {
        int prevX = playerX;
        int prevY = playerY;
        System.out.println("Attempting to move character: CurrentKey=" + currentKey);

        switch (currentKey) {
            case "W":
                playerY -= playerSpeed;
                break;
            case "S":
                playerY += playerSpeed;
                break;
            case "A":
                playerX -= playerSpeed;
                break;
            case "D":
                playerX += playerSpeed;
                break;
        }

        if (!checker.canMove(playerX, playerY, playerWidth, playerHeight)) {
            playerX = prevX;
            playerY = prevY;
            System.out.println("Movement blocked: (" + playerX + "," + playerY + ")");
        } else {
            collectDot();
        }

        playerLabel.setLocation(playerX, playerY);
        updateBoundingBox();
    }

    private void collectDot() {
        int cellX = (playerX - gamePanel.mapOffsetX) / MapManager.cellSize;
        int cellY = (playerY - gamePanel.mapOffsetY) / MapManager.cellSize;
        System.out.println("Checking dot collection at: (" + cellX + "," + cellY + ")");

        JLabel[][] dotGrid = MapManager.getDotGrid();
        int[][] map = gamePanel.getMap();
        if (cellY >= 0 && cellY < dotGrid.length && cellX >= 0 && cellX < dotGrid[0].length) {
            if (map[cellY][cellX] == 2) {
                SwingUtilities.invokeLater(() -> {
                    gamePanel.incrementScore(1);  // Увеличиваем счет
                    map[cellY][cellX] = 0;        // Меняем карту, убирая точку
                    if (dotGrid[cellY][cellX] != null) {
                        dotGrid[cellY][cellX].setVisible(false);
                        dotGrid[cellY][cellX].getParent().revalidate();
                        dotGrid[cellY][cellX].getParent().repaint();
                        dotGrid[cellY][cellX] = null;  // Удаляем ссылку на JLabel
                    }
                });
            }
        }
    }

    private void initializePosition() {
        playerWidth = 27;
        playerHeight = 38;
        playerSpeed = 2;
        counter = 0;
        currentIcon = downIcon1;
        playerLabel.setIcon(currentIcon);
        playerLabel.setSize(playerWidth, playerHeight);
        playerLabel.setLocation(playerX, playerY);

        boundingBoxLabel.setSize(playerWidth, playerHeight);
        boundingBoxLabel.setLocation(playerX, playerY);
    }

    private void updateBoundingBox() {
        boundingBoxLabel.setLocation(playerX, playerY);
    }

    public void update() {
        moveCharacter();
        animation();
        gamePanel.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        String newKey = "";
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                newKey = "W";
                break;
            case KeyEvent.VK_S:
                newKey = "S";
                break;
            case KeyEvent.VK_A:
                newKey = "A";
                break;
            case KeyEvent.VK_D:
                newKey = "D";
                break;
        }
        if (!newKey.equals(currentKey)) {
            currentKey = newKey;
            updateIcon();
        }
        System.out.println("Key pressed: " + newKey);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Не очищаем currentKey при отпускании клавиши, чтобы движение продолжалось
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Не используется
    }
}










