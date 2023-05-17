package minesweeper;
import com.javarush.engine.cell.*; //импорт игрового движка JavasRush
import java.util.*; //импорт для работы со списками


public class MinesweeperGame extends Game {

        private static final int SIDE = 9;   //переменная - размер стороны экрана
        private GameObject[][] gameField = new GameObject[SIDE][SIDE];   //массив для ячеек
        private int countMinesOnField;     //переменная для хранения кол-ва мин на поле
        private static final String MINE = "\uD83D\uDCA3"; //знак мины
        private static final String FLAG = "\uD83D\uDEA9";   //знак флага
        private  int countFlags;         // счетчик кол-ва флагов
        private boolean isGameStopped;   //запуск и приостановка игры
        private int countClosedTiles = SIDE * SIDE;     //кол-во оставшихся закрытых ячеек
        private int score;      //счет


        public void initialize() {
                setScreenSize(SIDE, SIDE);  //размер экрана
                createGame();     //начало игры
        }

        private void createGame() {   //в методе создаем двумерный массив ячеек с объектами GameObject, размером с игровое поле

                for (int i = 0; i < gameField.length; i++) {
                        for (int j = 0; j < gameField[i].length; j++) {
                                setCellValue(j, i, ""); // сначала очищаем все ячейки от предыдущих значений на случай, если это не первая игра, а рестарт (исп. пустую строку)
                                gameField[i][j] = new GameObject(j, i, false);   //создание GameObject для каждой ячейки
                                setCellColor(j, i, Color.GRAY);    //задаем цвет ячейки
                                int random = getRandomNumber(10);   //задаем вероятность мины в ячейке (10 -> 10%)
                                if (random == 0) {
                                        gameField[i][j].isMine = true;   //случайная закладка мин с вероятн. 10%
                                        countMinesOnField += 1;   //счетчик мин
                                }
                        }
                }
                countMineNeighbors(); //подсчитываем кол-во мин вокруг всех ячеек
                countFlags = countMinesOnField; //число флажков будет равно числу мин

                int randomVoice = getRandomNumber(4);
                if (randomVoice == 0) {
                        Sound.playSound("./src/minesweeper/sound/start/Dont_Make_Me_Laugh.wav");
                }
                else if (randomVoice == 1) {
                        Sound.playSound("./src/minesweeper/sound/start/You_re_Still_Trying_to_Win.wav");
                }
                else if (randomVoice == 2) {
                        Sound.playSound("./src/minesweeper/sound/start/You_Will_Die_Mortal.wav");
                }
                else {
                        Sound.playSound("./src/minesweeper/sound/start/You_Will_Never_Win.wav");
                }
        }



        private List<GameObject> getNeighbors(GameObject gameObject) {   //метод на вход получает ячейку, а на выходе выдает список из ссылок на соседей этой ячейки
                List<GameObject> result = new ArrayList<>();
                for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {  //обход ячеек с координатами по x & y от -1 до + 1
                        for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                                if (y < 0 || y >= SIDE) {    //если координаты выпадают за пределы поля, то они нас не интересуют
                                        continue;
                                }
                                if (x < 0 || x >= SIDE) {
                                        continue;
                                }
                                if (gameField[y][x] == gameObject) {  //если ячейка совпадает с исходной, то не учитываем её, т.к. нам нужны только соседи
                                        continue;
                                }
                                result.add(gameField[y][x]); //если ячейка не выходит за пределы поля и не совпадает с исходной, значит это сосед
                        }
                }
                return result; // в result хранится созданный список соседей
        }

        private void countMineNeighbors() {  //метод для подсчета кол-ва мин у соседей ВСЕХ НЕзаминиров. ячеек
                for (int i = 0; i < gameField.length; i++) {  //перебор всех ячеек
                        for (int j = 0; j < gameField[i].length; j++) {
                                if (gameField[i][j].isMine == false) {  //считаем соседей только для ячеек БЕЗ мин
                                        for (GameObject gameObject : getNeighbors(gameField[i][j])) { //для каждого из соседей данной ячейки
                                                if (gameObject.isMine == true) {   //если сосед-мина
                                                        gameField[i][j].countMineNeighbors += 1;  //обновляем счетчик мин данной ячейки (+1)
                                                }
                                        }
                                }
                        }
                }
        }


        private void openTile(int x, int y) {           //метод открывает ячейку, а также задает, что отображается при открытии ячейки
                
                if (isGameStopped == true || gameField[y][x].isFlag == true || gameField[y][x].isOpen == true)
                        return;         //Метод не должен работать, если игра окончена; если ячейка=флаг и если ячейка уже открыта
                else {

                        if (gameField[y][x].isMine == true) {          //если ячейка-мина, то отображаем знак мины
                                setCellValueEx(x, y, Color.RED, MINE);
                                Sound.playSound("./src/minesweeper/sound/lose/1_explosion.wav");
                                gameOver();                             //и вызываем метод GameOver
                        }

                        if (gameField[y][x].isMine == false && gameField[y][x].countMineNeighbors == 0) {    // если сама ячейка - не мина, и соседей-мин нет, то...
                                gameField[y][x].isOpen = true;          //как только открыли ячейку - обновляем переменную экз. isOpen на true
                                countClosedTiles -= 1;   //уменьшаем счетчик закрытых ячеек на 1
                                score += 5; // счет +5
                                setScore(score); //передаем значение счета в табло счета (это метод родит. класса Game)
                                setCellColor(x, y, Color.GREEN);         //при открытии ячейка меняет свой цвет
                                setCellValue(x, y, "");    //не выводим ноль (число мин-соседей) - вместо этого исп. пустую строку
                                List<GameObject> Sosedi = getNeighbors(gameField[y][x]); //создаем список соседей данной ячейки c пом. метода GetNeighbors
                                for (int i = 0; i < Sosedi.size(); i++) {     //обход циклом списка соседей
                                        if (Sosedi.get(i).isOpen == false)      //если i-тый сосед не открыт, то.... ( Sosedi.get(i) - доступ к i-тому соседу из списка)
                                                openTile(Sosedi.get(i).x, Sosedi.get(i).y);
                                }    // .... рекурсивно запускаем метод открытия ячейки-соседа
                                //Sound.playSound("./src/minesweeper/sound/surprise/drum.wav");
                        }

                        if (gameField[y][x].isMine == false && gameField[y][x].countMineNeighbors != 0) {  // если сама ячейка - не мина, но есть соседи-мины, то...
                                gameField[y][x].isOpen = true;          //как только открыли ячейку - обновляем переменную экз. isOpen на true
                                countClosedTiles -= 1;   //уменьшаем счетчик закрытых ячеек на 1
                                score += 5; // счет +5
                                setScore(score); //передаем значение счета в табло счета (это метод родит. класса Game)
                                setCellColor(x, y, Color.GREEN);         //при открытии ячейка меняет свой цвет
                                setCellNumber(x, y, gameField[y][x].countMineNeighbors);   // ...при открытии ячейки показываем число соседей-мин
                        }

                        if (countMinesOnField == countClosedTiles) // если кол-во закрытых ячеек = кол-ву мин, то игрок выйграл
                                win();
                        }
                }


        private void markTile(int x, int y) {    //метод отвечает за установку флагов
                if (isGameStopped == true)
                        return;                 //если игра окончена, то метод не должен работать
                else {
                        if (gameField[y][x].isOpen == false) {  //метод должен ставить флажок только в НЕоткрытую клетку

                                if (gameField[y][x].isFlag == false && countFlags != 0) {  //если в ячейке нет флага, то...
                                        gameField[y][x].isFlag = true;  //меняем значение isFlag на true (т.к. ставим флаг)
                                        countFlags -= 1; //уменьшаем счетчик флагов на 1
                                        setCellValue(x, y, FLAG); //рисуем символ флага в ячейке
                                        setCellColor(x, y, Color.YELLOW); //меняем цвет ячейки на желтый
                                } else if (gameField[y][x].isFlag == true) {  //если в ячейке уже есть флаг, то...
                                        gameField[y][x].isFlag = false;  //меняем значение isFlag на false (т.к. убираем флаг)
                                        countFlags += 1; //возвращаем 1 флаг в счетчик флагов
                                        setCellValue(x, y, ""); //стираем изображение флага - теперь пустая строка
                                        setCellColor(x, y, Color.GRAY);  //меняем цвет ячейки на исходный
                                }
                        }
                }
        }

        private void gameOver() {
                isGameStopped = true;
                int randomVoice = getRandomNumber(4);
                if (randomVoice == 0) {
                        showMessageDialog(Color.GRAY, "Game over", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/lose/That_was_Pathetic.wav");
                }
                else if (randomVoice == 1) {
                        showMessageDialog(Color.GRAY, "Game over", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/lose/You_Suck.wav");
                }
                else if (randomVoice == 2) {
                        showMessageDialog(Color.GRAY, "Game over", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/lose/Is_That_Your_Best.wav");
                }
                else {
                        showMessageDialog(Color.GRAY, "Game over", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/lose/Shao_Kahn_laughs.wav");
                }
        }

        private void win () {
                isGameStopped = true;
                int randomVoice = getRandomNumber(4);
                if (randomVoice == 0) {
                        showMessageDialog(Color.GRAY, "You win", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/victory/1_applause.wav");
                        Sound.playSound("./src/minesweeper/sound/victory/Excellent.wav");
                }
                else if (randomVoice == 1) {
                        showMessageDialog(Color.GRAY, "You win", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/victory/1_applause.wav");
                        Sound.playSound("./src/minesweeper/sound/victory/Superb.wav");
                }

                else if (randomVoice == 2) {
                        showMessageDialog(Color.GRAY, "You win", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/victory/1_applause.wav");
                        Sound.playSound("./src/minesweeper/sound/victory/Well_Done.wav");
                }
                else {
                        showMessageDialog(Color.GRAY, "You win", Color.BLACK, 42);
                        Sound.playSound("./src/minesweeper/sound/victory/1_applause.wav");
                        Sound.playSound("./src/minesweeper/sound/victory/I_pered_nami.wav");
                }
        }

        private void restart() {
                isGameStopped = false;
                countClosedTiles = SIDE * SIDE;  //возвращаем исходные значения переменных класса
                score = 0;
                setScore(score);
                countMinesOnField = 0;
                createGame(); //запускаем новую игру

        }



        @Override         // переопределяем метод, задающий нажатие ЛКМ: добавляем запуск нашего метода openTile, раскрывающего ячейку
        public void onMouseLeftClick(int x, int y) {
                if (isGameStopped == false) {
                        Sound.playSound("./src/minesweeper/sound/Mouse_Click.wav");
                        openTile(x, y);   //открывать ячейки мы можем, только если игра не окончена
                }
                else
                        restart(); //если игра окончена, то ЛКМ должен вызывать рестарт игры
        }

        @Override
        public void onMouseRightClick(int x, int y) {
                Sound.playSound("./src/minesweeper/sound/Mouse_Click.wav");
                markTile(x, y);
        }
}
