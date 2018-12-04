package game.kozyanko.super2048;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class Main extends AppCompatActivity implements GestureDetector.OnGestureListener {

    private static final String TAG = Main.class.getSimpleName();

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private static final int LOG_COUNT = 36;

    private GestureDetectorCompat gd;

    /**
     * Кнопка для отображения набранных очков
     */
    private Button Bscore;

    /**
     * Кнопка для отображения направления жестов, введенных пользователем
     */
    private Button log;

    /**
     * Кнопка для отображения количества введённых жестов
     */
    private Button logcount;

    /**
     * Кнопки для отображения цифр
     */
    private Button[][] cells;

    /**
     * Флаг игры
     */
    private boolean Playing;

    /**
     * Набранные очки
     */
    private int Score;

    /**
     * Строка с введенными жестами пользователей
     */
    private String logging;

    /**
     * количество введенных жестов
     */
    private int loggingcount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // обрабоотчик жестов
        gd = new GestureDetectorCompat(this, this);

        cells = new Button[4][4];
        // кнопки 1-го ряда
        cells[0][0] = (Button) findViewById(R.id.cell_1);
        cells[0][1] = (Button) findViewById(R.id.cell_2);
        cells[0][2] = (Button) findViewById(R.id.cell_3);
        cells[0][3] = (Button) findViewById(R.id.cell_4);
        // кнопки 2-го ряда
        cells[1][0] = (Button) findViewById(R.id.cell_5);
        cells[1][1] = (Button) findViewById(R.id.cell_6);
        cells[1][2] = (Button) findViewById(R.id.cell_7);
        cells[1][3] = (Button) findViewById(R.id.cell_8);
        // кнопки 3-го ряда
        cells[2][0] = (Button) findViewById(R.id.cell_9);
        cells[2][1] = (Button) findViewById(R.id.cell_10);
        cells[2][2] = (Button) findViewById(R.id.cell_11);
        cells[2][3] = (Button) findViewById(R.id.cell_12);
        // кнопки 4-го ряда
        cells[3][0] = (Button) findViewById(R.id.cell_13);
        cells[3][1] = (Button) findViewById(R.id.cell_14);
        cells[3][2] = (Button) findViewById(R.id.cell_15);
        cells[3][3] = (Button) findViewById(R.id.cell_16);

        Bscore = ((Button) findViewById(R.id.score));
        log = findViewById(R.id.LOG);
        logcount = findViewById(R.id.LOGCOUNT);

        logging = "";
        loggingcount = 0;

        init();
    }


    public void onClick(View view) {
        init();
    }


    /**
     * Инициализация данных
     */
    private void init() {
        clear();
        newGame();
        Playing = true;
        Score = 0;
        //вывод счета
        Bscore.setText(((Integer) Score).toString());
        logMove("R,");
    }

    private void clear() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                setValue(i, j, "");
            }

        }
    }

    private void newGame() {
        GenerateNewBtn();
        GenerateNewBtn();
    }

    /**
     * Генерация значений на кнопке     *
     */
    private void GenerateNewBtn() {
        Random r = new Random();
        int row, col;
        do {
            // генерация значения по строке
            row = r.nextInt(4);
            // генерация значения по столбцу
            col = r.nextInt(4);
            // генерация продолжается, пока полученная ячейка не будет пустой
        } while (cells[row][col].getText() != getString(R.string.empty));
        // генерация значения ячейки
        int value = r.nextInt(4);
        // если сгенерированное значение ячейки равно 0, то вывести 4, иначе - 2
        setValue(row, col, (value == 0) ? 4 : 2);
    }

    /**
     * Установить значение на кнопку
     *
     * @param row   индекс кнопки по строке
     * @param col   индекс кнопки по столбцу
     * @param value значение кнопки
     */
    private void setValue(int row, int col, int value) {
        cells[row][col].setText(value);
        setBtnSryle(row, col);
    }

    /**
     * Установить значение на кнопку
     *
     * @param row   индекс кнопки по строке
     * @param col   индекс кнопки по столбцу
     * @param value значение кнопки
     */
    private void setValue(int row, int col, String value) {
        cells[row][col].setText(value);
    }

    /**
     * Окончание игры
     */
    private void gameOver() {
        Playing = false;
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.gameOver))
                .setMessage(getString(R.string.score) + Score)
                .setPositiveButton(R.string.restart, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        newGame();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * @param points
     */
    private void onMove(int points) {
        Score += points;
        // вывод счета
        Bscore.setText(((Integer) Score).toString());
        GenerateNewBtn();
        if (!doValidMovesExist()) {
            int x = 0;
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    Log.i(TAG, "Cell " + x + ": " + cells[i][j].getText());
                    x++;
                }
            }
            gameOver();
        }
    }

    private boolean doValidMovesExist() {
        if (!isLeftValid() && !isRightValid() && !isUpValid() && !isDownValid()) {
            return false;
        }
        return true;
    }

    private boolean isLeftValid() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (canMoveLeft(i, j)) {
                    return true;
                }
            }

        }
        return false;
    }

    private boolean isRightValid() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (canMoveRight(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isUpValid() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (canMoveUp(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isDownValid() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (canMoveDown(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Проверка на возможность сдвига кнопки влево
     *
     * @param row номер строки, где находится кнопка
     * @param col номер столбца, где находится кнопка
     * @return true - можно сдвинуть вправо, иначе - false
     */
    private boolean canMoveLeft(int row, int col) {
        if (col == 0 || getBtnText(row, col).equals(getString(R.string.empty))) {
            return false;
        } else {
            int leftPosition = col - 1;
            if (getBtnText(row, leftPosition).equals(getString(R.string.empty)) ||
                    getBtnText(row, leftPosition).equals(getBtnText(row, col))) {
                return true;
            }
            if (canMoveLeft(row, leftPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на возможность сдвига кнопки вправо
     *
     * @param row номер строки, где находится кнопка
     * @param col номер столбца, где находится кнопка
     * @return true - можно сдвинуть вправо, иначе - false
     */
    private boolean canMoveRight(int row, int col) {
        if (col == 3 || getBtnText(row, col).equals(getString(R.string.empty))) {
            return false;
        } else {
            int rightPosition = col + 1;
            if (getBtnText(row, rightPosition).equals(getString(R.string.empty)) ||
                    getBtnText(row, rightPosition).equals(getBtnText(row, col))) {
                return true;
            }
            if (canMoveRight(row, rightPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на возможность сдвига кнопки вверх
     *
     * @param row номер строки, где находится кнопка
     * @param col номер столбца, где находится кнопка
     * @return true - можно сдвинуть вправо, иначе - false
     */
    private boolean canMoveUp(int row, int col) {
        if (row == 0 || getBtnText(row, col).equals(getString(R.string.empty))) {
            return false;
        } else {
            int upPosition = row - 1;
            if (getBtnText(upPosition, col).equals(getString(R.string.empty)) ||
                    getBtnText(upPosition, col).equals(getBtnText(row, col))) {
                return true;
            }
            if (canMoveUp(upPosition, col)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверка на возможность сдвига кнопки вниз
     *
     * @param row номер строки, где находится кнопка
     * @param col номер столбца, где находится кнопка
     * @return true - можно сдвинуть вправо, иначе - false
     */
    private boolean canMoveDown(int row, int col) {
        if (row == 3 || getBtnText(row, col).equals(getString(R.string.empty))) {
            return false;
        } else {
            int downPosition = row + 1;
            if (getBtnText(downPosition, col).equals(getString(R.string.empty)) ||
                    getBtnText(downPosition, col).equals(getBtnText(row, col))) {
                return true;
            }
            if (canMoveDown(downPosition, col)) {
                return true;
            }
        }
        return false;
    }

    private void onLeftSwipe() {
        if (isLeftValid()) {
            onMove(moveLeft());
        }
    }

    private void onRightSwipe() {
        if (isRightValid()) {
            onMove(moveRight());
        }
    }

    private void onUpSwipe() {
        if (isUpValid()) {
            onMove(moveUp());
        }
    }

    private void onDownSwipe() {
        if (isDownValid()) {
            onMove(moveDown());
        }
    }

    /**
     * Сдвиг влево
     *
     * @return количество очков при сдвиге влево
     */
    private int moveLeft() {
        //log
        logMove("L,");
        int points = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                points += moveLeft(i, j);
            }
        }
        return points;
    }

    /**
     * Сдвиг влево значения кнопки
     *
     * @param row индекс строки, где находится кнопка
     * @param col индекс столбца, где находится кнопка
     * @return количество очков, получаемых при сдвиге
     */
    private int moveLeft(int row, int col) {
        // если невозможно сдвинуть
        if (!canMoveLeft(row, col)) {
            return 0; // то вернуть 0 очков
        }
        // значение кнопки
        String image = getBtnText(row, col);
        int leftPosition = col - 1;
        // если кнопка слева пустая
        if (getBtnText(row, leftPosition).equals(getString(R.string.empty))) {
            // текущая  ячейка очищается
            cells[row][col].setText(R.string.empty);
            // то значения сдвигвется влево
            setValue(row, leftPosition, image);
            // сдвигаем ячейку слева
            return moveLeft(row, leftPosition);
            // иначе если значение слева равно значению текущей кнопки
        } else if (getBtnText(row, leftPosition).equals(image)) {
            // то значения складываются
            int amount = Integer.parseInt(image) << 1;
            // текущая кноопкая очищается
            cells[row][col].setText(R.string.empty);
            // сумма записывается влево
            setValue(row, leftPosition, amount);
            // возврат полученного числа при сложении
            return amount;
        }
        return 0;
    }

    /**
     * Сдвиг вправо
     *
     * @return количество очков при сдвиге влево
     */
    private int moveRight() {
        //log
        logMove("R,");
        int points = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                points += moveRight(i, j);
            }
        }
        return points;
    }

    /**
     * Сдвиг вправо значения кнопки
     *
     * @param row индекс строки, где находится кнопка
     * @param col индекс столбца, где находится кнопка
     * @return количество очков, получаемых при сдвиге
     */
    private int moveRight(int row, int col) {
        if (!canMoveRight(row, col)) {
            return 0;
        }
        String image = getBtnText(row, col);
        int rightPosition = col + 1;
        if (getBtnText(row, rightPosition).equals(getString(R.string.empty))) {
            cells[row][col].setText(R.string.empty);
            setValue(row, rightPosition, image);
            return moveRight(row, rightPosition);
        } else if (getBtnText(row, rightPosition).equals(image)) {
            int amount = Integer.parseInt(image) << 1;
            cells[row][col].setText(R.string.empty);
            setValue(row, rightPosition, amount);
            return amount;
        }
        return 0;
    }

    /**
     * Сдвиг вверх
     *
     * @return количество очков при сдвиге влево
     */
    private int moveUp() {
        //log
        logMove("U,");
        int points = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                points += moveUp(i, j);
            }
        }
        return points;
    }

    /**
     * Сдвиг вверх значения кнопки
     *
     * @param row индекс строки, где находится кнопка
     * @param col индекс столбца, где находится кнопка
     * @return количество очков, получаемых при сдвиге
     */
    private int moveUp(int row, int col) {
        if (!canMoveUp(row, col)) {
            return 0;
        }
        String image = getBtnText(row, col);
        int upPosition = row - 1;
        if (getBtnText(upPosition, col).equals(getString(R.string.empty))) {
            cells[row][col].setText(R.string.empty);
            setValue(upPosition, col, image);
            return moveUp(upPosition, col);
        } else if (getBtnText(upPosition, col).equals(image)) {
            int amount = Integer.parseInt(image) << 1;
            cells[row][col].setText(R.string.empty);
            setValue(upPosition, col, amount);
            return amount;
        }
        return 0;
    }

    /**
     * Сдвиг вниз
     *
     * @return количество очков при сдвиге влево
     */
    private int moveDown() {
        //log
        logMove("D,");
        int points = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                points += moveDown(i, j);
            }
        }
        return points;
    }

    /**
     * Сдвиг вниз значения кнопки
     *
     * @param row индекс строки, где находится кнопка
     * @param col индекс столбца, где находится кнопка
     * @return количество очков, получаемых при сдвиге
     */
    private int moveDown(int row, int col) {
        if (!canMoveDown(row, col)) {
            return 0;
        }
        String image = getBtnText(row, col);
        int downPosition = row + 1;
        if (getBtnText(downPosition, col).equals(getString(R.string.empty))) {
            cells[row][col].setText(R.string.empty);
            setValue(downPosition, col, image);
            return moveDown(downPosition, col);
        } else if (getBtnText(downPosition, col).equals(image)) {
            int amount = Integer.parseInt(image) << 1;
            cells[row][col].setText(R.string.empty);
            setValue(downPosition, col, amount);
            return amount;
        }
        return 0;
    }

    private void logMove(String direction) {
        if ((logging).length() > LOG_COUNT) {
            logging = logging.substring(1);
        }
        logging += direction;
        loggingcount++;
        logcount.setText(((Integer) loggingcount).toString());
        log.setText(logging);
    }

    /**
     * Получение текста с кнопки
     *
     * @param row индекс строки, где расположена кнопка
     * @param col индекс столбца, где расположена кнопка
     * @return текст кнопки
     */
    private String getBtnText(int row, int col) {
        return cells[row][col].getText().toString();
    }


    /**
     * Установить стиль кнопки
     *
     * @param row индекс строки, где расположена кнопка
     * @param col индекс столбца, где расположена кнопка
     */
    private void setBtnSryle(int row, int col) {
        String numb = getBtnText(row, col);
        Button btn = cells[row][col];
        switch (numb) {
            case "":
                btn.setTextAppearance(R.style.btn0);
                break;
            case "2":
                btn.setTextAppearance(R.style.btn2);
                break;
            case "4":
                btn.setTextAppearance(R.style.btn4);
                break;
            case "8":
                btn.setTextAppearance(R.style.btn8);
                break;
            case "16":
                btn.setTextAppearance(R.style.btn16);
                break;
            case "32":
                btn.setTextAppearance(R.style.btn32);
                break;
            case "64":
                btn.setTextAppearance(R.style.btn64);
                break;
            case "128":
                btn.setTextAppearance(R.style.btn128);
                break;
            case "256":
                btn.setTextAppearance(R.style.btn256);
                break;
            case "512":
                btn.setTextAppearance(R.style.btn512);
                break;
            case "1024":
                btn.setTextAppearance(R.style.btn1024);
                break;
            case "2048":
                btn.setTextAppearance(R.style.btn2048);
                break;
            default:
                ;
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!Playing) {
            return true;
        }
        try {
            // left swipe -> right to left
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onLeftSwipe();
            }
            // right swipe -> left to right
            else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onRightSwipe();
            }
            // up swipe -> down to up
            else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                onUpSwipe();
            }
            // down swipe -> up to down
            else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                onDownSwipe();
            }
        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e.toString());
        }
        return true;
    }
}
