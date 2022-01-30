package tictactoe;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class Board {
    /**
     * Вспомогательный интерфейс. Нужен для forEachElement.
     */
    private interface Action {
        void doAction(int row, int col);
    }

    private static final int N = 3; // Доска 3*3
    private final Seed[][] cells = new Seed[N][N];
    private final Set<Pos> freePositions = new HashSet<>(N * N);

    Board() {
        forEachElement((row, col) -> {
            cells[row][col] = Seed.Empty;
            freePositions.add(new Pos(row, col));
        });
    }

    /**
     * Обходим игровую доску в двойном цикле и вызываем doAction интерфейса Action
     * с параметрами row и col
     *
     * @param action
     */
    private static void forEachElement(Action action) {
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                action.doAction(row, col);
            }
        }
    }

    /**
     * Вернуть копию набора свободных позиций.
     *
     * @return
     */
    public Set<Pos> getFreePositions() {
        return Collections.unmodifiableSet(freePositions);
    }

    public Seed getSeedAtPosition(Pos pos) {
        return cells[pos.getRow()][pos.getCol()];
    }

    public void setSeedAtPosition(Pos pos, Seed seed) {
        Seed currentSeed = cells[pos.getRow()][pos.getCol()];
        if (currentSeed != Seed.Empty && seed != Seed.Empty) {
            throw new GameException("Позиция " + pos + " уже занята!");
        }
        if (currentSeed == seed) return;
        cells[pos.getRow()][pos.getCol()] = seed;
        if (seed == Seed.Empty) {
            freePositions.add(pos);
        } else {
            freePositions.remove(pos);
        }
    }

    public GameStatus getGameStatus() {
        final int[] rowScores = new int[N];
        final int[] colScores = new int[N];
        final int[] diag1Score = new int[1];
        final int[] diag2Score = new int[1];
        // Расчитываем баллы, по которым определим кто победил
        forEachElement((row, col) -> {
            Seed seed = this.cells[row][col];
            int delta = getDelta(seed);
            // Добавляем в баллы для строки row
            // 1, если в [row][col] X,
            // -1, если [row][col]  O
            // 0, если это свободная позиция
            rowScores[row] += delta;
            // Аналогично работаем и со столбцом col
            colScores[col] += delta;
            // Если строка и столбец равны,
            // то это главная диагональ
            if (row == col) {
                diag1Score[0] += delta;
            }
            // А этот случай для второй диагонали
            if (row == N - col - 1) {
                diag2Score[0] += delta;
            }
        });

        // Выясняем, кто победил. Если сумма по какой-то строке, столбцу или диагонали
        // равна N*delta, то в этой строке, столбце или диагонале размещено
        // N одинаковых знаков - крестиков или ноликов:
        for (Seed seed : new Seed[]{Seed.O, Seed.X}) {
            final int winPoints = N * getDelta(seed);
            for (int i = 0; i < N; i++) {
                if (rowScores[i] == winPoints || colScores[i] == winPoints)
                    return new GameStatus(true, seed);
            }
            if (diag1Score[0] == winPoints || diag2Score[0] == winPoints)
                return new GameStatus(true, seed);
        }
        // Ничья
        return new GameStatus(getFreePositions().isEmpty(), Seed.Empty);
    }

    private int getDelta(Seed seed) {
        if (seed == Seed.X) return 1;
        else if (seed == Seed.O) return -1;
        return 0;
    }

    /**
     * Используется в рекурсивном вызове miniMax
     *
     * @return полная копия игровой доски со всеми крестиками и ноликами
     */
    public Board createFullCopy() {
        Board board = new Board();
        forEachElement((row, col) -> {
            Pos pos = new Pos(row, col);
            Seed seed = getSeedAtPosition(pos);
            board.setSeedAtPosition(pos, seed);
        });
        return board;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  | 0 | 1 | 2 |\n");
        sb.append("--+---+---+---+\n");
        forEachElement((row, col) -> {
            if (col == 0) {
                sb.append(row);
                sb.append(" | ");
            }
            sb.append(cells[row][col]);
            sb.append(" | ");
            if (col == N - 1) {
                sb.append("\n--+---+---+---+\n");
            }
        });
        return sb.toString();
    }
}
