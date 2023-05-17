package minesweeper;

public class GameObject {
    public int x;
    public int y;
    public boolean isMine; //есть ли мина в ячейке
    public int countMineNeighbors; //кол-во мин у соседей данной ячейки
    public boolean isOpen; //открыта ли ячейка
    public boolean isFlag; //поставлен ли флаг в ячейку

    public GameObject(int x, int y, boolean isMine) {
        this.x = x;
        this.y = y;
        this.isMine = isMine;  //является ли ячейка миной
    }

}
