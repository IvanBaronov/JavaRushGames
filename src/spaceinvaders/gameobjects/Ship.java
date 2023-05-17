package spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Ship extends GameObject {    //класс Ship будет хранить в себе общие свойства космических кораблей

    public boolean isAlive = true;
    private List<int[][]> frames;  // список матриц для кадров анимации (крушения кораблей)
    private  int frameIndex;  // хранит индекс текущего кадра анимации из списка frames
    private boolean loopAnimation = false; //используется для создания бесконечной анимации корабля-босса. Если true, то метод nextFrame повторяет кадры, сбрасывая frameIndex.

    public Ship (double x, double y) {
        super (x, y);
    }

    public Bullet fire() {
        return null;  //метод отвечает за стрельбу. Здесь возвращает null, т.к. реализован в классах-наследниках.
    }

    public void kill() {
        isAlive = false;
    }

    public void setStaticView(int[][] viewFrame) {   //метод устанавливает матрицу (чертеж) корабля и задает анимацию
        super.setMatrix(viewFrame);
        frames = new ArrayList<int[][]>(); //создаем список матриц для кадров анимации (крушения)
        frames.add(viewFrame); //в кач-ве первого кадра используем матрицу, переданную в аргументе
        frameIndex = 0;  //начинаем нумерацию кадров анимации с 0
    }

    public  void setAnimatedView(boolean isLoopAnimation, int[][]... viewFrames) {   //метод задает анимацию
        //Многоточие указывает на то, что может быть передано произвольное число аргументов. Внутри метода это преобразуется в массив
        super.setMatrix(viewFrames[0]);
        frames = Arrays.asList(viewFrames); //список двумерных массивов
        frameIndex = 0;  //номер кадра анимации = 0
        loopAnimation = isLoopAnimation;  //значение loopAnimation устанавливается в соответствии с аргументом, переданным в метод
    }

    public void nextFrame() {  //переключаемся на следующий кадр анимации
            frameIndex += 1;  //в переменную frameIndex помещаем номер следующего кадра анимации
            if (frameIndex < frames.size()) { //Переключаемся на следующий кадр анимации только в том случае, если в списке кадров "frames" еще есть кадры
                matrix = frames.get(frameIndex);  // устанавливает в поле matrix следующий кадр анимации
            }
            if  (loopAnimation && frameIndex >= frames.size()) {
                frameIndex = 0; //если имеем дело с "зацикленной" анимацией (движение босса) и дошли до последнего кадра, то сбрасываем номер кадра до 0 --> "зацикливаем" анимацию
                matrix = frames.get(frameIndex);
            }
    }

    public boolean isVisible() {   //помечает корабль как невидимый, если он "не живой"
        if (this.isAlive == false && frameIndex >= frames.size()) return false; //корабль становится невидимым только после того, как отрисована анимация его уничтожения
        else return true;
    }




    @Override
    public void draw(Game game) {    // переопределяем родительский метод, отвечающий за отрисовку объекта, чтобы добавить в него смену кадров анимации.
        super.draw(game);
        nextFrame();
    }

}
