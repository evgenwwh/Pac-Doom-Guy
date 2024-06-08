package Game;

public class Checker {
    private int[][] map;
    private int mapOffsetX;
    private int mapOffsetY;

    public Checker(int[][] map, int mapOffsetX, int mapOffsetY) {
        this.map = map;
        this.mapOffsetX = mapOffsetX;
        this.mapOffsetY = mapOffsetY;
    }

    public boolean canMove(int x, int y, int width, int height) {
        int left = (x - mapOffsetX) / MapManager.cellSize;
        int right = (x + width - mapOffsetX - 1) / MapManager.cellSize;
        int top = (y - mapOffsetY) / MapManager.cellSize;
        int bottom = (y + height - mapOffsetY - 1) / MapManager.cellSize;

        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (i < 0 || i >= map.length || j < 0 || j >= map[0].length || map[i][j] == 1) {
                    return false;
                }
            }
        }
        return true;
    }
}






