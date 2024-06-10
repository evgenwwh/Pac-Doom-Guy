package Game;

import javax.swing.*;
import java.awt.*;

public class Demons implements Runnable {
    private GamePanel gamePanel;
    private int ghostX, ghostY, ghostSpeed = 3, ghostWidth = 27, ghostHeight = 38;
    private ImageIcon ghostIcon = new ImageIcon(getClass().getResource("/images/demons/demon1.png"));
    private JLabel ghostLabel = new JLabel(ghostIcon);
    private Checker checker;
    private boolean running = true;
    private int moveDirection = -1;
    private int moveCounter = 0;

    private CustomTimer moveTimer;

    public Demons(GamePanel gamePanel, Checker checker, int initialX, int initialY) {
        this.gamePanel = gamePanel;
        this.checker = checker;
        this.ghostX = initialX;
        this.ghostY = initialY;
        ghostLabel.setSize(ghostWidth, ghostHeight);
        ghostLabel.setLocation(ghostX, ghostY);
        gamePanel.add(ghostLabel);

        moveTimer = new CustomTimer(15, this::moveGhost);
        moveTimer.start();

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                checkCollisionWithPlayer();
                Thread.sleep(16);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void moveGhost() {
        if (moveCounter <= 0 || !canMoveInCurrentDirection()) {
            chooseNewDirection();
            checkCollisionWithPlayer();
        }

        int dx = 0, dy = 0;
        switch (moveDirection) {
            case 0: dx = -ghostSpeed; break;
            case 1: dx = ghostSpeed; break;
            case 2: dy = -ghostSpeed; break;
            case 3: dy = ghostSpeed; break;
        }

        if (checker.canMove(ghostX + dx, ghostY + dy, ghostWidth, ghostHeight)) {
            ghostX += dx;
            ghostY += dy;
            ghostLabel.setLocation(ghostX, ghostY);
            moveCounter--;
        } else {
            moveCounter = 0; // Reset the counter if movement is blocked
        }
    }

    private void chooseNewDirection() {
        int trials = 0;
        boolean canMove;
        do {
            moveDirection = (int) (Math.random() * 4);  // Generate a random direction
            int dx = 0, dy = 0;
            switch (moveDirection) {
                case 0: dx = -ghostSpeed; break;
                case 1: dx = ghostSpeed; break;
                case 2: dy = -ghostSpeed; break;
                case 3: dy = ghostSpeed; break;
            }
            canMove = checker.canMove(ghostX + dx, ghostY + dy, ghostWidth, ghostHeight);
            trials++;
        } while (!canMove && trials < 10);

        moveCounter = (int) (Math.random() * 51) + 50;
    }
    private void checkCollisionWithPlayer() {
        Rectangle ghostBounds = new Rectangle(ghostX, ghostY, ghostWidth + 10, ghostHeight + 10);
        Rectangle playerBounds = new Rectangle(gamePanel.getPlayer().getPlayerX(), gamePanel.getPlayer().getPlayerY(), gamePanel.getPlayer().getPlayerWidth() + 10, gamePanel.getPlayer().getPlayerHeight() + 10);


        if (ghostBounds.intersects(playerBounds)) {
            if (!gamePanel.getPlayer().isInvulnerable()) {
                gamePanel.playerTouchedByGhost();
            }
        }
    }





    private boolean canMoveInCurrentDirection() {
        int dx = 0, dy = 0;
        switch (moveDirection) {
            case 0: dx = -ghostSpeed; break;
            case 1: dx = ghostSpeed; break;
            case 2: dy = -ghostSpeed; break;
            case 3: dy = ghostSpeed; break;
        }
        return checker.canMove(ghostX + dx, ghostY + dy, ghostWidth, ghostHeight);
    }

    public JLabel getGhostLabel() {
        return ghostLabel;
    }

    public void stop() {
        running = false;
        moveTimer.stop(); // Stop the timer when stopping the demon
    }
}



