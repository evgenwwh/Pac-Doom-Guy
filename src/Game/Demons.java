package Game;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Demons implements Runnable {
    private GamePanel gamePanel;
    private int ghostX, ghostY, ghostSpeed = 2, ghostWidth = 27, ghostHeight = 38;
    private ImageIcon ghostIcon = new ImageIcon(getClass().getResource("/images/demons/demon1.png"));
    private JLabel ghostLabel = new JLabel(ghostIcon);
    private Checker checker;
    private Random random = new Random();
    private boolean running = true;

    // Current direction
    private int directionX = 0;
    private int directionY = 0;

    public Demons(GamePanel gamePanel, Checker checker, int initialX, int initialY) {
        this.gamePanel = gamePanel;
        this.checker = checker;
        this.ghostX = initialX;
        this.ghostY = initialY;
        ghostLabel.setSize(ghostWidth, ghostHeight);
        ghostLabel.setLocation(ghostX, ghostY);
        gamePanel.add(ghostLabel);
        chooseNewDirection();
        new Thread(this).start(); // Start the thread for this demon
    }

    @Override
    public void run() {
        while (running) {
            moveGhost();
            try {
                Thread.sleep(18); // Adjust the sleep time as needed for movement speed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void moveGhost() {
        int newX = ghostX + directionX * ghostSpeed;
        int newY = ghostY + directionY * ghostSpeed;

        if (checker.canMove(newX, newY, ghostWidth, ghostHeight)) {
            ghostX = newX;
            ghostY = newY;
            System.out.println("Ghost moving to: (" + ghostX + ", " + ghostY + ")");
        } else {
            System.out.println("Ghost hit a wall at: (" + newX + ", " + newY + ")");
            chooseNewDirection();
        }

        ghostLabel.setLocation(ghostX, ghostY);
        checkCollisionWithPlayer();
    }

    private void chooseNewDirection() {
        int[] directionsX = {1, -1, 0, 0}; // Right, Left, Stop, Stop
        int[] directionsY = {0, 0, 1, -1}; // Stop, Stop, Down, Up

        int randomIndex = random.nextInt(4);
        directionX = directionsX[randomIndex];
        directionY = directionsY[randomIndex];

        System.out.println("New direction chosen: (" + directionX + ", " + directionY + ")");
    }

    private void checkCollisionWithPlayer() {
        Rectangle ghostRect = new Rectangle(ghostX, ghostY, ghostWidth, ghostHeight);
        Rectangle playerRect = new Rectangle(gamePanel.getPlayer().playerX, gamePanel.getPlayer().playerY, gamePanel.getPlayer().playerWidth, gamePanel.getPlayer().playerHeight);
        if (ghostRect.intersects(playerRect)) {
            gamePanel.gameOver();
        }
    }

    public JLabel getGhostLabel() {
        return ghostLabel;
    }

    public void stop() {
        running = false;
    }
}




