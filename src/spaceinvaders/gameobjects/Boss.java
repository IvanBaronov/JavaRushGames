package spaceinvaders.gameobjects;

import spaceinvaders.Direction;
import spaceinvaders.ShapeMatrix;

public class Boss extends EnemyShip {

   private  int frameCount = 0;  //отсчет тактов для смены анимации при передвижении босса (смена анимации происх. 1 раз в 10 тактов)

    public Boss (double x, double y) {  //у босса есть анимация при передвижении, прописваем это в конструкторе
        super(x, y);
        setAnimatedView(true, ShapeMatrix.BOSS_ANIMATION_FIRST, ShapeMatrix.BOSS_ANIMATION_SECOND);
        this.score = 100;  //в отличие от обычного корабля, за босса дается 100 очков - указываем это в конструкторе
    }

    @Override
    public void nextFrame() {
        frameCount += 1;
        if (frameCount % 10 ==0 || this.isAlive == false) {  // %10==0 т.к. смена анимации босса должна происходить только 1 раз в 10 такто
            super.nextFrame();
        }
    }

    @Override
    public Bullet fire() {
        if (this.isAlive) {
            if (this.matrix == ShapeMatrix.BOSS_ANIMATION_FIRST) return new Bullet(x + 6, y + height, Direction.DOWN);
            //В зависимости от кадра анимации, босс будет стрелять из разных пушек
            else return new Bullet(x, y + height, Direction.DOWN);
        }
        else return null; //если босс "неживой", то он не стреляет
    }

    @Override
    public void kill() {  //переопределяем родительский метод "убийства" корабля, чтобы добавить в него анимацию
        if (this.isAlive) {
            this.isAlive = false;
            super.setAnimatedView(false, ShapeMatrix.KILL_BOSS_ANIMATION_FIRST, ShapeMatrix.KILL_BOSS_ANIMATION_SECOND, ShapeMatrix.KILL_BOSS_ANIMATION_THIRD);
        }
    }

}
