package snake;


import com.javarush.engine.cell.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static snake.Direction.*;
import static snake.SnakeGame.HEIGHT;
import static snake.SnakeGame.WIDTH;


public class Snake {

    private static final String HEAD_SIGN = "\uD83D\uDC7E";  // символ - голова змеи
    private static final String BODY_SIGN = "\u26AB";       // символ - тело змеи
    public boolean isAlive = true;

    private Direction direction = Direction.LEFT;  //текущее направление движения, по умолчанию - влево

    public Snake (int x, int y) {                 //конструктор змеи - исп. 3 объекта GameObject
        GameObject gameObject1 = new GameObject(x, y);
        GameObject gameObject2 = new GameObject(x + 1, y);
        GameObject gameObject3 = new GameObject(x + 2, y);
        snakeParts.addAll(Arrays.asList(gameObject1, gameObject2, gameObject3));

    }

    private List<GameObject> snakeParts = new ArrayList<>();  //список хранит объекты GameObject, которые являются частями змеи

    public void draw (Game game) {          //метод snake.draw(game) - отрисовка змеи на экране
        if (this.isAlive == true) {     //отрисовка черной змейки, если она живая
            game.setCellValueEx(snakeParts.get(0).x, snakeParts.get(0).y, Color.NONE, HEAD_SIGN, Color.BLACK, 75); //рисуем значок головы для первого GameObject'a змеи
            for (int i = 1; i < snakeParts.size(); i++) {   //рисуем значки тела для всех последующих GameObject'ов змеи
                game.setCellValueEx(snakeParts.get(i).x, snakeParts.get(i).y, Color.NONE, BODY_SIGN, Color.BLACK, 75);
            }
        }
        else {        //отрисовка красной змейки, если она НЕ живая
            game.setCellValueEx(snakeParts.get(0).x, snakeParts.get(0).y, Color.NONE, HEAD_SIGN, Color.RED, 75); //рисуем значок головы для первого GameObject'a змеи
            for (int i = 1; i < snakeParts.size(); i++) {   //рисуем значки тела для всех последующих GameObject'ов змеи
                game.setCellValueEx(snakeParts.get(i).x, snakeParts.get(i).y, Color.NONE, BODY_SIGN, Color.RED, 75);
            }
            }
        }

    public void setDirection(Direction direction) {  //метод изменяет направление движения змейки
        //сначала проверяем, что это НЕ поворот на 180 градусов - такое запрещено
        if (this.direction == Direction.LEFT && direction == Direction.RIGHT || this.direction == Direction.RIGHT && direction == Direction.LEFT || this.direction == Direction.UP && direction == Direction.DOWN || this.direction == Direction.DOWN && direction == Direction.UP) {
        return;
        }
        //также если змейка продолжает движение в том же направлении, что и раньше, то тоже направление менять не надо
        else if ((this.direction == Direction.LEFT || this.direction == Direction.RIGHT) && snakeParts.get(0).x == snakeParts.get(1).x) {
            return;
        }
        else if ((this.direction == Direction.UP || this.direction == Direction.DOWN) && snakeParts.get(0).y == snakeParts.get(1).y) {
            return;
        }
        else {
            this.direction = direction;
        }
    }



    public void move(Apple apple) {  //метод, двигающий змейку. Передаем в него яблоко, т.к. нужны его координаты
        GameObject newHead = createNewHead();
        if (newHead.x >= WIDTH || newHead.x < 0 || newHead.y >= HEIGHT || newHead.y < 0) {
                isAlive = false;  //если попадаем за пределы поля, то НЕ перерысовываем голову змейки (+) isAlive = false
            }
        else if (checkCollision(newHead) == true) { //проеряем, нет ли столкновения змейки с собой
                isAlive = false; //если столкновение есть, то isAlive = false и не передвигаем змею
        }
        else if (newHead.x == apple.x && newHead.y == apple.y) { //если змея съедает яблоко, то яблоко "умирает" (исчезает) и хвост змеи не отрезается (т.к. змея удлинняется)
                apple.isAlive = false;
                snakeParts.add(0, newHead);  //перерисовываем голову змейки в соответствии с направлением движения
            }
        else {
                snakeParts.add(0, newHead);  //перерисовываем голову змейки в соответствии с направлением движения
                removeTail(); //удаляем последний элемент змейки
            }
        }

    public GameObject createNewHead() { //перерисовка головы змеи в зависимости от направления движения
        switch (direction) {
            case LEFT:
                return new GameObject(snakeParts.get(0).x - 1, snakeParts.get(0).y);
            case RIGHT:
                return new GameObject(snakeParts.get(0).x + 1, snakeParts.get(0).y);
            case UP:
                return new GameObject(snakeParts.get(0).x, snakeParts.get(0).y - 1);
            case DOWN:
                return new GameObject(snakeParts.get(0).x, snakeParts.get(0).y + 1);
            default:
                return snakeParts.get(0);
        }
    }

    public void removeTail() {  //удаляет хвост - т.е. последний элемент змейки, нужно для ее движения
        snakeParts.remove(snakeParts.size() - 1);
    }

    public boolean checkCollision(GameObject gameObject) {  //метод, проверяющий, не сталкивается ли змея сама с собой (в качестве аргумента будет передаваться голова змеи)
        for (GameObject snakePart : snakeParts) {
            if (snakePart.x == gameObject.x && snakePart.y == gameObject.y) return true; //если координаты переданного объекта совпадают с координатами одного из элементов змейки, то значит столкновение есть
        }
        return false;
    }

    public int getLength() {    //возвращает длину змеи
        return snakeParts.size();
    }



}
