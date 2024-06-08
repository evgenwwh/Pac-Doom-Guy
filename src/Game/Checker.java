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
        // Преобразование координат пикселей персонажа в координаты ячеек карты с учетом смещения
        int left = (x - mapOffsetX) / MapManager.cellSize;
        int right = (x + width - 1 - mapOffsetX) / MapManager.cellSize;
        int top = (y - mapOffsetY) / MapManager.cellSize;
        int bottom = (y + height - 1 - mapOffsetY) / MapManager.cellSize;



        // Проверка границ карты
        if (left < 0 || right >= map[0].length || top < 0 || bottom >= map.length) {
            System.out.println("Collision with boundary");
            return false;
        }

        // Проверка каждой ячейки в пределах занимаемого игроком пространства
        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                if (map[i][j] == 1) {
                    System.out.println("Collision with wall at map cell: (" + i + ", " + j + ")");
                    return false;
                }
            }
        }

        return true;
    }
}


