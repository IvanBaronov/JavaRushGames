package snake;

public class GameObject {  //вспомогательный класс, один объект GameObject будет соответствовать одной ячейке змеи
    public int x;
    public int y;

    public GameObject(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
