package spaceinvaders.gameobjects;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

public class GameObject {
    public double x;  //х, y - координаты верхней левой ячейки объекта
    public double y;
    public int width;  //ширина объекта (=ширина матрицы)
    public int height; //высота объекта (= высота матрицы)
    public int[][] matrix;   //матрица объекта (=какие ячейки будут закрашены в те или ииные цвета, а какие - нет)

    public GameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
        width = matrix[0].length;
        height = matrix.length;
    }

    public boolean isCollision(GameObject gameObject) {  //метод определяет, есть ли столкновение с передаваемым объектом
        for (int gameObjectX = 0; gameObjectX < gameObject.width; gameObjectX++) {
            for (int gameObjectY = 0; gameObjectY < gameObject.height; gameObjectY++) {
                if (gameObject.matrix[gameObjectY][gameObjectX] > 0) {
                    if (isCollision(gameObjectX + gameObject.x, gameObjectY + gameObject.y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isCollision(double x, double y) {  //метод определяет, есть ли столкновение тек. объекта с переданными координатами x,y
        for (int matrixX = 0; matrixX < width; matrixX++) {
            for (int matrixY = 0; matrixY < height; matrixY++) {
                if (matrix[matrixY][matrixX] > 0
                        && matrixX + (int) this.x == (int) x
                        && matrixY + (int) this.y == (int) y) {
                    return true;
                }
            }
        }
        return false;
    }

    public void draw(Game game) {   //метод "рисует" объектна основе матрицы (определяющей, какие ячейки будут закрашены. а какие - нет)
        for (int i = 0; i < width; i++) {  //цикл от 0 до ширины матрицы - проходимся по всем столбцам матрицы
            for (int j = 0; j < height; j++) {  //цикл от 0 до высоты матрицы - проходимся по каждой ячейке в пределах столбца
                int colorIndex = matrix[j][i];  //для каждой ячейки объекта берем номер цвета из соотв. ячейки матрицы
                game.setCellValueEx((int) x + i, (int) y + j, Color.values()[colorIndex], ""); //задаем цвет каждой ячейке объекта, не забываем прибавлять x,y - координаты верхней левой ячейки
                //ColorValues принимает число colorIndex ("номер цвета" из матрицы), а возвращает сам цвет
            }
        }
    }
}