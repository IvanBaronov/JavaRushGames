package spaceinvaders.gameobjects;

import spaceinvaders.Direction;
import spaceinvaders.ShapeMatrix;

public class EnemyShip extends  Ship {   // вражеский корабль

    public  int score = 15;  // за убийство обычного враж. корабля дается 15 очков


    public EnemyShip (double x, double y) {
        super(x, y);
        setStaticView(ShapeMatrix.ENEMY);   //берем матрицу ("чертеж") корабля из класса ShapeMatrix
    }

    public void move(Direction direction, double speed) {
        // Метод описывает движение вражеского корабля. В зависимости от направления и скорости, метод меняет соответствующую координату.
        // У движения вниз скорость постоянная — 2.
        if (direction == Direction.RIGHT) x = x + speed;
        if (direction == Direction.LEFT) x = x - speed;
        if (direction == Direction.DOWN) y = y + 2;
    }

    public Bullet fire() { //метод отвечает за стрельбу враж. корабля
        return new Bullet(x + 1, y + height, Direction.DOWN);
        //У корабля координаты х, y - это коорд. верхней левой ячейки, а нам нужно, чтобы пуля вылетала снизу посередине корабля
        //Поэтому к "х" прибавляем 1 (корабль имеет 3 ячейки в ширину), а к "у" прибавляем высоту корабля, чтобы пуля вылетала снизу

    }

    @Override
    public void kill() {  //переопределяем метод "убийвающий" вражеский корабль, чтобы добавить в него анимацию
        if (this.isAlive) {
            this.isAlive = false;
            super.setAnimatedView(false, ShapeMatrix.KILL_ENEMY_ANIMATION_FIRST, ShapeMatrix.KILL_ENEMY_ANIMATION_SECOND, ShapeMatrix.KILL_ENEMY_ANIMATION_THIRD);
        }
    }


}
