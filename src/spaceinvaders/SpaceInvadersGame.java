package spaceinvaders;

import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;
import com.javarush.engine.cell.Key;
import spaceinvaders.gameobjects.Bullet;
import spaceinvaders.gameobjects.EnemyFleet;
import spaceinvaders.gameobjects.PlayerShip;
import spaceinvaders.gameobjects.Star;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SpaceInvadersGame extends Game {
    public static final int WIDTH = 64;
    public static final int HEIGHT= 64;
    private List<Star> stars;  //звезды на фоне
    private EnemyFleet enemyFleet;
    public static final int COMPLEXITY = 5; //сложность игры, а именно — вероятность выстрела вражеского корабля за один шаг игры.
    private List<Bullet> enemyBullets; //список всех вражеских пуль
    private PlayerShip playerShip;
    private boolean  isGameStopped;
    private int animationsCount; //кол-во кадров анимации
    private List<Bullet> playerBullets; // список пуль игрока
    private final static int PLAYER_BULLETS_MAX = 2;    // ограничим максимальное количество пуль игрока на экране, чтобы немного усложнить игру.
    private int score = 0;  //игровой счет

    @Override
    public void initialize() {   //НАЧАЛО выполнения кода (вместо метода main)
        setScreenSize(WIDTH, HEIGHT);  //задаем размеры поля
        createGame();  //создаем элементы игры
    }

    private void createGame() {  //здесь создаем элементы игры
        isGameStopped = false;  //игра запущена -- снимаем флаг isGameStopped
        createStars();  //создаем звезды для фона
        playerShip = new PlayerShip();
        enemyFleet = new EnemyFleet(); //создаем вражеский флот
        enemyBullets = new ArrayList<Bullet>(); //создаем список для вражеских пуль
        playerBullets = new ArrayList<Bullet>(); //создаем список для пуль игрока
        animationsCount = 0;
        drawScene(); //рисуем элементы игры, в т.ч. задний фон
        setTurnTimer(40); //задаем длительность шага перерисовки экрана 40 мс (0.04 с, 25 раз в секунду)
        score = 0;
    }

    private void drawScene() {   //здесь отрисовываем элементы игры
        drawField();  //рисуем задний фон
        playerShip.draw(this);  //рисуем корабль игрока
        enemyFleet.draw(this); //рисуем вражеский флот
        for (Bullet bullet : enemyBullets) bullet.draw(this); //рисуем вражеские пули
        for (Bullet bullet : playerBullets) bullet.draw(this); //рисуем пули игрока
    }

    private void drawField() {  //метод отрисовывает задний фон (космос)
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                setCellValueEx(x, y, Color.BLACK, "");  //закрашиваем все ячейки поля в черный цвет
            }
        }
        for (Star s : stars) {  //рисуем все звезды из списка stars
            s.draw(this);
        }
    }

    private void createStars() { //создаем звезды для фона
        stars = new ArrayList<Star>();
        for (int i = 0; i < 8; i++) {
            stars.add(new Star(getRandomNumber(64), getRandomNumber(64)));
        }
    }

    private void moveSpaceObjects() {  //метод отвечает за движение объектов
        playerShip.move();
        enemyFleet.move();
        for (Bullet bullet : enemyBullets) bullet.move();
        for (Bullet bullet : playerBullets) bullet.move();
    }

    private void removeDeadBullets() { // метод удаляет все "неживые" пули, а такжне те, которые вылетели за пределы экрана
        Iterator<Bullet> itr = enemyBullets.iterator();
        while (itr.hasNext()) {
            Bullet bullet = itr.next();
            if (bullet.isAlive == false || bullet.y >= HEIGHT - 1) itr.remove();
        }

        Iterator<Bullet> itr2 = playerBullets.iterator();
        while (itr2.hasNext()) {
            Bullet bullet = itr2.next();
            if (bullet.isAlive == false || bullet.y + bullet.height <0) itr2.remove();
        }
    }

    private void check() {  //проверяем попадания и убираем лишние объекты на экране
        playerShip.verifyHit(enemyBullets);  //проверяем, есть ли попалание в корабль игрока (внутри метода если есть попадание, то вызывается метод kill)
        score = score + enemyFleet.verifyHit(playerBullets); //проверяем, попал ли игрок во вражю корабли (внутри метода для каждого убитого корабля вызывается метод kill), а также увеличиваем счет за каждый убитый корабль
        removeDeadBullets(); //удаляем из списка пуль "мертвые" пули (попавшие в кого-то или вылетевшие за пределы экрана)
        enemyFleet.deleteHiddenShips(); //удаляем из списка враж. кораблей невидимые (уничтоженные) враж. корабли
        if (enemyFleet.getBottomBorder() >= playerShip.y) playerShip.kill(); //если враж. флот достиг низа, игрок погибает
        if (playerShip.isAlive == false) stopGameWithDelay(); //если игрок убит, останавливаем игру, но прежде задержка
        if (enemyFleet.getShipsCount() == 0) {  //если кол-во враж. кораблей = 0 --> победа!
            playerShip.win();
            stopGameWithDelay();
        }
    }

    private void stopGameWithDelay() {  //Если пуля попала в игрока, перед остановкой игры нужно успеть показать анимацию взрыва
        //Для этого в данном методе будем вызывать метод stopGame с задержкой.
        animationsCount += 1;
        if (animationsCount >= 10) {
            stopGame(playerShip.isAlive);
        }
    }

    private void stopGame(boolean isWin) {  //что происходит при окончании игры (победе или поражении)
        isGameStopped = true;
        stopTurnTimer();
        if (isWin) showMessageDialog(Color.BLACK, "You win!", Color.GREEN, 14);
        else showMessageDialog(Color.BLACK, "You lose!", Color.RED, 14);
    }


    @Override
    public void onTurn(int turn) {   //метод класса Game, нужен для периодической перерисовки экрана
        check(); //удаляем лишние объекты с экрана
        moveSpaceObjects(); //в каждом ходе передвигаем все объекты
        Bullet newBullet = enemyFleet.fire(this); //вражеский флот может совершить выстрел
        if (newBullet != null) enemyBullets.add(newBullet); //Если выстрел произошел, добавляем новую пулю в список
        setScore(score);  //setScore - метод родит. класса Game, задаем счет игры
        drawScene();
    }

    @Override
    public void  onKeyPress(Key key) {
        if (isGameStopped == true  &&  key == Key.SPACE) {  //при нажатии пробела во время паузы - начало новой игры
            createGame();
        }
        if (key == Key.LEFT) playerShip.setDirection(Direction.LEFT); //при нажатии клавиши "влево" задаем движение корабля влево
        else if (key == Key.RIGHT) playerShip.setDirection(Direction.RIGHT); //аналогично вправо
        else if (key == Key.SPACE) {
            Bullet newBullet = playerShip.fire();
            if (newBullet != null && playerBullets.size() < PLAYER_BULLETS_MAX)  playerBullets.add(newBullet);


        }
    }

    @Override
    public void onKeyReleased(Key key) {   //метод определяет, что происходит после отпускания клавиш влево/вправо
        //  (иначе компьютер будет думать, что клавиши не нажаты, а зажаты)
        if (key == Key.LEFT  &&  playerShip.getDirection() == Direction.LEFT) playerShip.setDirection(Direction.UP);
        //если корабль движется влево и отпускается клавиша "влево", то корабль отсанавливается на месте (движения "вверх" в игре нет)
        else if (key == Key.RIGHT  &&  playerShip.getDirection() == Direction.RIGHT) playerShip.setDirection(Direction.UP);
    }

    @Override
    public void setCellValueEx(int x, int y, Color cellColor, String stringValue) {   //Переопределим метод, чтобы он работал только с валидными координатами.
        if (x >= WIDTH || x < 0 || y >= HEIGHT || y < 0 ) return;
        else super.setCellValueEx(x, y, cellColor, stringValue);
    }


}
