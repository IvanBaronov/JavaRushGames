package snake;
import com.javarush.engine.cell.*;

public class SnakeGame extends Game {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 15;
    private Snake snake;
    private int turnDelay; // установка продолжительности хода
    private Apple apple;
    private boolean isGameStopped;
    private static final int GOAL = 28; //выйгрыш при достижении длины змеи 28
    private int score; //счет

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame() {
        isGameStopped = false;
        score = 0;
        setScore(score);
        snake = new Snake(WIDTH / 2, HEIGHT / 2); //начальные координаты положения головы змеи - середина поля
        createNewApple();  //создаем первое яблоко
        drawScene();  //отрисовка игрового поля
        turnDelay = 300;
        setTurnTimer(turnDelay); //метод из движка, задает указаанную продолжительность хода в миллисекундах
    }

    private void drawScene() {   //отрисовка игрового поля, змеи (созданной в методе CreateGame)
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                setCellValueEx(x, y, Color.DARKSEAGREEN, ""); //После передвижения змейки нужно очищать игровое поле от уже несуществующих ее элементов, поэтому присваиваем значение "пустая строка"
            }
        }
        snake.draw(this); //отрисовка змеи, передаем "этот" объект SnakeGame, чтобы прорисовка происходила в данном игровом поле
        apple.draw(this); //отрисовка яблока, передаем "этот" объект SnakeGame, чтобы прорисовка происходила в данном игровом поле
    }

    @Override
    public void onTurn(int iii) {   //  здесь описывается всё, что должно происходить в игре на протяжении одного хода
        if (snake.isAlive == false) {
            gameOver();
        }
        if (snake.getLength() > GOAL) {
            win();
        }
        snake.move(apple); //передвигаем змею + передаем в метод move объект "яблоко", чтобы сверяться, не съедает ли его змея при текущем перемещении
        if (apple.isAlive == false) {
            createNewApple();  //если яблоко было съедено, то надо создать новое
            score += 5;   // если яблоко было съедено, то надо увеличить счет на 5
            setScore(score);
            turnDelay -= 10;  // если яблоко было съедено, то увеличиваем скорость игры (уменьшаем длительность хода на 10 мс)
            setTurnTimer(turnDelay);

        }
        drawScene();  //перерисовка поля
        
    }

    @Override
    public void onKeyPress(Key key) {  //смена направления движения змеи при нажатии клавиш-стрелок
        switch (key) {
            case DOWN:
                snake.setDirection(Direction.DOWN);
                break;
            case LEFT:
                snake.setDirection(Direction.LEFT);
                break;
            case RIGHT:
                snake.setDirection(Direction.RIGHT);
                break;
            case UP:
                snake.setDirection(Direction.UP);
                break;
            case SPACE:
                if (isGameStopped == true) createGame();
        }


    }

    private void createNewApple() {    //создаем новое яблоко с рандомными координатами в пределах высоты и ширины поля
        int a = getRandomNumber(WIDTH);
        int b = getRandomNumber(HEIGHT);
        apple = new Apple(a, b);
        while (snake.checkCollision(apple) == true)  {   //до тех пор, пока вновь созданное яблоко попадает на тело змеи, пересоздаем его
            a = getRandomNumber(WIDTH);
            b = getRandomNumber(HEIGHT);
            apple = new Apple(a, b);
        }
    }

    private void gameOver() {
        stopTurnTimer();  //метод из родительского класса Game (класс из движка JavaRush), остановка таймера
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "GAME OVER", Color.BLACK, 75);
          }

    private void win() {
        stopTurnTimer();
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "YOU WIN", Color.BLACK, 75);
    }


}
