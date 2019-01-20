package tictactoe;

class MiniMax implements IAI {

    private static class Line {
        private final Pos[] line = new Pos[3];

        Line(Pos pos1, Pos pos2, Pos pos3) {
            line[0] = pos1;
            line[1] = pos2;
            line[2] = pos3;
        }

        Pos getPos(int index) {
            return line[index];
        }
    }

    private static final Line[] lines = new Line[]
            {
                    new Line(new Pos(0, 0), new Pos(0, 1), new Pos(0, 2)),// row 0
                    new Line(new Pos(1, 0), new Pos(1, 1), new Pos(1, 2)),// row 1
                    new Line(new Pos(2, 0), new Pos(2, 1), new Pos(2, 2)),// row 2
                    new Line(new Pos(0, 0), new Pos(1, 0), new Pos(2, 0)),// col 0
                    new Line(new Pos(0, 1), new Pos(1, 1), new Pos(2, 1)),// col 1
                    new Line(new Pos(0, 2), new Pos(1, 2), new Pos(2, 2)),// col 2
                    new Line(new Pos(0, 0), new Pos(1, 1), new Pos(2, 2)),// diagonal
                    new Line(new Pos(0, 2), new Pos(1, 1), new Pos(2, 0)),// alternate diagonal
            };

    private Seed ourSeed;
    private Seed oppSeed;

    @Override
    public Pos findOptimalMovement(IBoard board, Seed seed) {
        ourSeed = seed;
        oppSeed = invertSeed(ourSeed);
        Score score = miniMax(board, ourSeed, 4);
        return score.getPos();
    }

    private static Seed invertSeed(Seed seed) {
        if (seed == Seed.Empty) return seed;
        return seed == Seed.O ? Seed.X : Seed.O;
    }

    private Score miniMax(IBoard board, Seed seed, int depth) {
        // ourSeed зерно - ищем максимум, oppSeed зерно - ищем минимум
        int bestScore = (seed == ourSeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Pos bestPos = null;
        if (depth == 0 || board.getGameStatus().isOver()) {
            // Game over, вычисляем очки для сложившейся игровой ситуации
            bestScore = evaluate(board);
        } else {
            // Проходим в цикле по позициям, НЕ ЗАНЯТЫМ зернами
            for (Pos move : board.getEmptyPositions()) {
                IBoard clonedBoard = board.duplicate();
                // Пробуем сделать ход для текущего игрока
                clonedBoard.setSeedAtPosition(move, seed);
                if (seed == ourSeed) {  // для ourSeed ищем максимум
                    int currentScore = miniMax(clonedBoard, oppSeed, depth - 1).getScorePoints();
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestPos = move;
                    }
                } else {  // для oppSeed ищем минимум
                    int currentScore = miniMax(clonedBoard, ourSeed, depth - 1).getScorePoints();
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestPos = move;
                    }
                }
            }
        }
        return new Score(bestPos, bestScore);

    }

    /**
     * Простейшая функция анализа игровой ситуации
     *
     * @param board игровая доска
     * @return оценка
     */
    private int evaluateSimple(IBoard board) {
        GameStatus status = board.getGameStatus();
        if (status.getWinnerSeed() == ourSeed) {
            return 100;
        } else if (status.getWinnerSeed() == oppSeed) {
            return -100;
        } else {
            return 0;
        }
    }

    /**
     * Эвристическая функция для игровой доски
     *
     * @param board - игровая доска
     * @return Сумма эвристик по всем возможным линиям
     */
    private int evaluate(IBoard board) {
        int score = 0;
        // Вычисление суммарной эвристики по всем 8 линиям доски: 3 строки, 3 стобца, 2 диагонали
        for (Line line : lines) {
            score += evaluateLine(board, line);
        }
        return score;
    }

    /**
     * Эвристическая функция для заданной тремя позизиями линии
     *
     * @param board - игровая доска
     * @return +100, +10, +1 для 3-, 2-, 1- поставленных в линию зерен ourSeed.
     * -100, -10, -1 для 3-, 2-, 1- поставленных в линию зерен oppSeed.
     * 0 если в линии есть оба зерна: X и O
     */
    private int evaluateLine(IBoard board, Line line) {
        int score = 0;

        Seed cell1 = board.getSeedAtPosition(line.getPos(0));
        Seed cell2 = board.getSeedAtPosition(line.getPos(1));
        Seed cell3 = board.getSeedAtPosition(line.getPos(2));

        // Первая ячейка
        if (cell1 == ourSeed) {
            score = 1;
        } else if (cell1 == oppSeed) {
            score = -1;
        }

        // Вторая ячейка
        if (cell2 == ourSeed) {
            if (score == 1) {   // cell1 is ourSeed
                score = 10;
            } else if (score == -1) {  // cell1 is oppSeed
                return 0;
            } else {  // cell1 is empty
                score = 1;
            }
        } else if (cell2 == oppSeed) {
            if (score == -1) { // cell1 is oppSeed
                score = -10;
            } else if (score == 1) { // cell1 is ourSeed
                return 0;
            } else {  // cell1 is empty
                score = -1;
            }
        }

        // Третья ячейка
        if (cell3 == ourSeed) {
            if (score > 0) {  // cell1 and/or cell2 is ourSeed
                score *= 10;
            } else if (score < 0) {  // cell1 and/or cell2 is oppSeed
                return 0;
            } else {  // cell1 and cell2 are empty
                score = 1;
            }
        } else if (cell3 == oppSeed) {
            if (score < 0) {  // cell1 and/or cell2 is oppSeed
                score *= 10;
            } else if (score > 1) {  // cell1 and/or cell2 is ourSeed
                return 0;
            } else {  // cell1 and cell2 are empty
                score = -1;
            }
        }
        return score;
    }
}
