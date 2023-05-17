package spaceinvaders.gameobjects;


import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

public class Star extends GameObject {  //объект "звезда" - белая точка на фоне

    private static final String STAR_SIGN = "\u2605";  //знак звезды, символ - вместо рисунка


    public Star (double x, double y) {    //в конструкторе передаем координаты звезды
        super(x, y);
    }

    public void draw(Game game) {    //метод рисует звезду белого цвета с координатами x и y
        game.setCellValueEx((int)x, (int)y,  Color.NONE, STAR_SIGN, Color.WHITE, 100);
    }





}
