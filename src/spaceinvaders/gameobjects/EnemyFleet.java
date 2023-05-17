package spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;
import spaceinvaders.Direction;
import spaceinvaders.ShapeMatrix;
import spaceinvaders.SpaceInvadersGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class EnemyFleet {  //класс описывает структуру вражеского флота

    private final static int ROWS_COUNT = 3;  //число рядов вражеских кораблей
    private final static int COLUMNS_COUNT = 10; //число столбцов вражеских кораблей
    private final static int STEP = ShapeMatrix.ENEMY.length + 1; //расстояние между левыми верхними углами соседних кораблей
    private List<EnemyShip> ships; //список кораблей
    private Direction direction = Direction.RIGHT; //направление движения флота, изначально - вправо

    public EnemyFleet() { //в конструкторе вызываем метод, создающий флот (список вражеских корпблей)
        createShips();
    }

    private void createShips() {  //создаем флот вражеских кораблей
        ships = new ArrayList<>();
        for (int x = 0; x < COLUMNS_COUNT; x++) {  //x, y - координаты верхней левой ячейки корабля
            for (int y = 0;  y < ROWS_COUNT; y++) {
                ships.add(new EnemyShip(x*STEP, y*STEP + 12));
                // y+12, т.к. 12 ячеек = оптимальное расстояние от верхней границы экрана до первого сверху ряда кораблей.
            }
        }
        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));  //добавляем босса в список враж. кораблей
    }

    public void draw (Game game) {
        for (EnemyShip ship : ships) {
            ship.draw(game);
        }
    }

    private double getLeftBorder() {  //возвращает левый край флота - мин. координата по "х"
        ArrayList<Double> coord = new ArrayList<>();
        for (EnemyShip ship : ships) {
            coord.add(ship.x);
        }
        return Collections.min(coord);
    }

    private double getRightBorder() {  //возвращает правый край флота - макс. координата по "х" + ширина корабля
        ArrayList<Double> coord = new ArrayList<>();
        for (EnemyShip ship : ships) {
            coord.add(ship.x + ship.width);
        }
        return Collections.max(coord);
    }

    private double getSpeed() {  //возвращает скорость движения флота (3 / кол-во оставшихся кораблей), но макс скорость  - 2
        //чем меньше кораблей, тем выше скорость их движения по горизонтали.
        return Math.min(2.0, 3.0 / ships.size());
    }

    public void move() {  //метод отвечает за движение враж. флота
        if (ships.size() != 0) { //двигаем, только если еще есть, что двигать
            if (direction == Direction.LEFT && getLeftBorder() < 0) {
                direction = Direction.RIGHT; //если флот движется влево и уперся в левый край, то надо сменить направление вправо
                for (EnemyShip ship : ships) ship.move(Direction.DOWN, getSpeed()); //при смене направления у края экрана сдвигаем корабли вниз
            }
            else if (direction == Direction.RIGHT && getRightBorder() > SpaceInvadersGame.WIDTH) {
                direction = Direction.LEFT; //если флот движется вправо и уперся в правый край, то надо сменить направление влево
                for (EnemyShip ship : ships) ship.move(Direction.DOWN, getSpeed()); //при смене направления у края экрана сдвигаем корабли вниз
            }
            else {
                for (EnemyShip ship : ships) ship.move(direction, getSpeed()); //если флот не у края экрана, то просто продолжаем движение в том же направлении
            }
        }
    }

    public Bullet fire (Game game) { //Этот метод будет вызывать метод fire у одного из кораблей флота, выбранного случайно.
        if (ships.size() > 0) {
            int probabilityOfSilence = game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY); //флот будет стрелять с вероятностью COMPLEXITY процентов.
            if (probabilityOfSilence > 0) return null;
            //Пояснение: если COMPLEXITY = 100, то probabilityOfSilence от 0 до (100/100), не включая 1, т.е. всегда 0. --> выстрел произойдет 100%
            //Если COMPLEXITY = 30, то probabilityOfSilence вернет число от 0 до (100/30), т.е. от 0 до 2  --> выстрел произойдет 30%, если выпадет 0.
            //Чем меньше COMPLEXITY, тем больше вероятность того, что probabilityOfSilence > 0 --> флот не выстрелит (т.е. метод вернет null).
            else { //Если probabilityOfSilence = 0, то флот стреляет
                int shipNumber = game.getRandomNumber(ships.size()); //выбираем случайный корабль, который произведет выстрел
                Bullet result = ships.get(shipNumber).fire(); //выбранный корабль стреляет
                return result; //метод возвращает пулю, выпущенную кораблем
            }
        }
        else return null; //если кораблей нет, то никто не стреляет
    }

    public int verifyHit(List<Bullet> bullets) {  //проверяем, есть ли столковение пуль игрока с вражескими кораблями, а также подсчитываем изменение игрового счета
        if (bullets.isEmpty()) return 0;  //если пуль игрока на экране нет, то незачем проверять столкновения, счет не меняется (+0)
        else {
        int deltaScore = 0; //величина, на которую увеличится счет
        Iterator<EnemyShip> itr = ships.iterator();   //обходим список вражеских кораблей
        while (itr.hasNext()) {
            EnemyShip nextShip = itr.next();
            for (Bullet nextBullet : bullets) {  //обходим список пуль, переданный в виде аргумента
                if (nextShip.isCollision(nextBullet) && nextShip.isAlive && nextBullet.isAlive) {
                    nextShip.kill(); //убиваем враж. корабль, который столкнулся с пулей
                    nextBullet.kill();  //пуля тоже "умирает"
                    deltaScore += nextShip.score; //добавляем очки, причитающиеся за уб-во данного корабля
                }
            }
        }
        return deltaScore; }
    }


    public void  deleteHiddenShips() {   //метод удаляет из списка враж. кораблей "ships" невидимые корабли (уничтоженные)
        Iterator<EnemyShip> itr = ships.iterator();
        while (itr.hasNext()) {
            EnemyShip nextShip = itr.next();
            if (nextShip.isVisible() == false) itr.remove();
        }
    }

    public double  getBottomBorder() { //определяем координаты нижней границы вражеского флота (игра проиграна, если флот дойдет до нижней границы экрана)
        double max = 0; //максимальная координата враж. корабля по оси "y" (чем больше - тем ниже?)
        for (EnemyShip nextShip : ships) {
            if (nextShip.y + nextShip.height > max) max = nextShip.y + nextShip.height;
        }
        return max;
    }

    public int getShipsCount() {    //возвращает кол-во вражеских кораблей в списке ships
        return ships.size();
    }




}
