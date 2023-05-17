package spaceinvaders.gameobjects;

import spaceinvaders.Direction;
import spaceinvaders.ShapeMatrix;

public class Bullet extends GameObject {

    private int dy; //величина изменения координаты y
    public boolean isAlive = true; //"жива" ли пуля

    public Bullet(double x, double y, Direction direction) {
        super(x, y); //задаем начальные координаты
        super.setMatrix(ShapeMatrix.BULLET);  //задаем форму пули
        if (direction == Direction.UP) dy = -1; // При движении вверх "dy" имеет отрицательное значение, т.к. мы уменьшаем координату "y"
        else dy = 1;  // При движении вниз (у пули только 2 варианта: вверх или вниз) "dy" имеет положительное значение, т.к. мы увеличиваем координату "y"
    }

    public void move() {
        y = y + dy; //y - координата, переменная из родительского класса
        //при перемещении пули увеличиваем ее координату "y" на "dy"
    }

    public void kill() {
        isAlive = false;
    }

}
