package spaceinvaders.gameobjects;

import spaceinvaders.Direction;
import spaceinvaders.ShapeMatrix;
import spaceinvaders.SpaceInvadersGame;

import java.util.List;

public class PlayerShip extends Ship {



    private Direction direction = Direction.UP;  //по умолчанию движение "вверх" - это отсутствие движения

    public PlayerShip() {  //конструктор родит. класса ship: x, y - начальные координаты левой верхней ячейки (x - посередине, y - внизу)
        super(SpaceInvadersGame.WIDTH / 2.0, SpaceInvadersGame.HEIGHT - ShapeMatrix.PLAYER.length - 1);
        setStaticView(ShapeMatrix.PLAYER); //внешний вид корабля игрока
    }

    public void verifyHit(List<Bullet> bullets) {  //метод проверяет, попали ли вражеские пули в корабль игрока
        if (!bullets.isEmpty()) {  //проверяем, только если в списке bullets есть пули
            if (this.isAlive) { //проверяем только если корабль игрока еще жив
                for (Bullet bullet : bullets) { //обходим список bullets - проверяем столкновение с каждой из пуль
                    if (bullet.isAlive) { //проверяем столкновение только с "живыми" пулями
                        if (isCollision(bullet)) {
                            kill(); //если произошло столкновение с пулей, то запускам метод kill, корабль игрока погибает
                            bullet.kill();  //пуля тоже "погибает"
                        }
                    }
                }
            }
        }
    }

    public void setDirection(Direction newDirection) {
        if (newDirection != Direction.DOWN) {
            direction = newDirection;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void move() {  //двигаем корабль игрока
        if (isAlive) { //двигаем только живой корабль
            if (direction == Direction.LEFT) x -= 1; //влево -- уменьшаем координату "х"
            else if (direction == Direction.RIGHT) x += 1; //вправо -- увеличиваем координату "х"
            if (x < 0) x = 0;  //координата х не может быть меньше нуля, чтобы корабль не вылетел за левый край экрана
            if (x + width > SpaceInvadersGame.WIDTH) x = SpaceInvadersGame.WIDTH - width;
            //координата "х" не м.б. больше ширины экрана (с учетом ширины корабля), иначе корабль вылетел бы за правый край экрана
        }
    }

    public void win() { //метод передает кораблю игрока "форму победителя"
        setStaticView(ShapeMatrix.WIN_PLAYER);
    }

    @Override
    public void kill() {   //метод реализует убийство корабля игрока
        if (this.isAlive) { //убить можно только дивой корабль
            this.isAlive = false;
            super.setAnimatedView(false, ShapeMatrix.KILL_PLAYER_ANIMATION_FIRST, ShapeMatrix.KILL_PLAYER_ANIMATION_SECOND, ShapeMatrix.KILL_PLAYER_ANIMATION_THIRD, ShapeMatrix.DEAD_PLAYER);
            //задаем анимацию "убийства" корабля в виде нескольких матриц
        }
    }

    @Override
    public Bullet fire() {
        if (this.isAlive) { //стреляет только "живой" корабль
            return new Bullet( x + 2, y - ShapeMatrix.BULLET.length, Direction.UP);
            //координата "x+2", т.к. пуля вылетает из середины корабля, а ширина корабля игрока - 6 ячеек
            //при задании координаты "у" учитываем длину пули в высоту
        }
        else return null; //если корабль "неживой", то стрелять не может
    }
}
