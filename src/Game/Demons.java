package Game;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Demons implements Runnable {
    private GamePanel gamePanel;
    private int ghostX, ghostY, ghostSpeed = 3, ghostWidth = 27, ghostHeight = 38;
    private ImageIcon ghostIcon = new ImageIcon(getClass().getResource("/images/demons/demon1.png"));
    private JLabel ghostLabel = new JLabel(ghostIcon);
    private Checker checker;
    private Random random = new Random();
    private boolean running = true;

    private int initialMoveDuration; // Duration in milliseconds for initial movement
    private int interval;
    private boolean chaseMode = false;
    private CustomTimer moveTimer;
    private CustomTimer modeSwitchTimer;

    // Current direction
    private int directionX = 0;
    private int directionY = 0;

    public Demons(GamePanel gamePanel, Checker checker, int initialX, int initialY, int initialMoveDuration, int interval) {
        this.gamePanel = gamePanel;
        this.checker = checker;
        this.ghostX = initialX;
        this.ghostY = initialY;
        this.initialMoveDuration = initialMoveDuration;
        this.interval = interval;
        ghostLabel.setSize(ghostWidth, ghostHeight);
        ghostLabel.setLocation(ghostX, ghostY);
        gamePanel.add(ghostLabel);

        moveTimer = new CustomTimer(15, this::moveGhost); // Timer to move the ghost
        moveTimer.start();

        modeSwitchTimer = new CustomTimer(interval, this::switchMode);
        modeSwitchTimer.start();

        new Thread(this).start(); // Start the thread for this demon
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(15); // Adjust the sleep time as needed for movement speed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void switchMode() {
        chaseMode = !chaseMode;
        System.out.println("Switching to " + (chaseMode ? "chase" : "scatter") + " mode for demon.");
    }

    public void moveGhost() {
        if (initialMoveDuration > 0) {
            initialMoveDuration -= 15;
            moveToInitialPosition();
        } else if (chaseMode) {
            chasePlayer();
        } else {
            randomMove();
        }
        ghostLabel.setLocation(ghostX, ghostY);
        checkCollisionWithPlayer();
    }

    private void moveToInitialPosition() {
        Point initialPos = gamePanel.getPlayer().getInitialPosition();
        int targetX = initialPos.x;
        int targetY = initialPos.y;

        int dx = Integer.compare(targetX, ghostX) * ghostSpeed;
        int dy = Integer.compare(targetY, ghostY) * ghostSpeed;

        if (checker.canMove(ghostX + dx, ghostY + dy, ghostWidth, ghostHeight)) {
            ghostX += dx;
            ghostY += dy;
        } else {
            chooseNewDirection();
        }
    }

    private void chasePlayer() {
        Point playerPos = gamePanel.getPlayer().getCurrentPosition();
        List<Point> path = findPath(ghostX, ghostY, playerPos.x, playerPos.y);
        if (!path.isEmpty()) {
            Point nextStep = path.get(0);
            int dx = nextStep.x - ghostX;
            int dy = nextStep.y - ghostY;
            ghostX += dx;
            ghostY += dy;
        }
    }

    private void randomMove() {
        int newX = ghostX + directionX * ghostSpeed;
        int newY = ghostY + directionY * ghostSpeed;

        if (checker.canMove(newX, newY, ghostWidth, ghostHeight)) {
            ghostX = newX;
            ghostY = newY;
        } else {
            chooseNewDirection();
        }
    }

    private void chooseNewDirection() {
        int[] directionsX = {1, -1, 0, 0}; // Right, Left, Stop, Stop
        int[] directionsY = {0, 0, 1, -1}; // Stop, Stop, Down, Up

        int randomIndex = random.nextInt(4);
        directionX = directionsX[randomIndex];
        directionY = directionsY[randomIndex];
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

    private List<Point> findPath(int startX, int startY, int goalX, int goalY) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(node -> node.f));
        Map<Point, Node> allNodes = new HashMap<>();

        Node startNode = new Node(startX, startY, null, 0, getHeuristic(startX, startY, goalX, goalY));
        openList.add(startNode);
        allNodes.put(new Point(startX, startY), startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();
            if (currentNode.x == goalX && currentNode.y == goalY) {
                return reconstructPath(currentNode);
            }

            for (Point neighbor : getNeighbors(currentNode.x, currentNode.y)) {
                int tentativeG = currentNode.g + 1;
                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor.x, neighbor.y));
                if (tentativeG < neighborNode.g) {
                    neighborNode.g = tentativeG;
                    neighborNode.f = neighborNode.g + getHeuristic(neighbor.x, neighbor.y, goalX, goalY);
                    neighborNode.parent = currentNode;
                    if (!openList.contains(neighborNode)) {
                        openList.add(neighborNode);
                    }
                    allNodes.put(neighbor, neighborNode);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Point> getNeighbors(int x, int y) {
        List<Point> neighbors = new ArrayList<>();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int i = 0; i < 4; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (checker.canMove(newX, newY, ghostWidth, ghostHeight)) {
                neighbors.add(new Point(newX, newY));
            }
        }
        return neighbors;
    }

    private int getHeuristic(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private List<Point> reconstructPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node.parent != null) {
            path.add(new Point(node.x, node.y));
            node = node.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private static class Node {
        int x, y;
        Node parent;
        int g, f;

        Node(int x, int y) {
            this.x = x;
            this.y = y;
            this.g = Integer.MAX_VALUE;
            this.f = Integer.MAX_VALUE;
        }

        Node(int x, int y, Node parent, int g, int f) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.g = g;
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return x == node.x && y == node.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}


