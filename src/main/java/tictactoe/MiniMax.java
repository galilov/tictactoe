package tictactoe;

class MiniMax {

    // "линия" на игровом поле
    private static class Line {
        // Три позиции в линии
        private final Pos[] line = new Pos[3];

        /**
         * Сюда передаем координаты 3-х позиций,
         * образующих линию.
         *
         * @param pos1 Позиция 1
         * @param pos2 Позиция 2
         * @param pos3 Позиция 3
         */
        Line(Pos pos1, Pos pos2, Pos pos3) {
            line[0] = pos1;
            line[1] = pos2;
            line[2] = pos3;
        }

        /**
         * Получить координаты позиции, составляющей линию
         *
         * @param index от 0 до 2
         * @return Соответствующая позиция с координатами row, col
         */
        Pos getPos(int index) {
            return line[index];
        }
    }

    // Линнии бывают разные: 3 горизонтальных, 3 вертикальных и 2 диагональных линии
    private static final Line[] lines = new Line[]
            {
                    new Line(new Pos(0, 0), new Pos(0, 1), new Pos(0, 2)),// строка 0
                    new Line(new Pos(1, 0), new Pos(1, 1), new Pos(1, 2)),// строка 1
                    new Line(new Pos(2, 0), new Pos(2, 1), new Pos(2, 2)),// строка 2
                    new Line(new Pos(0, 0), new Pos(1, 0), new Pos(2, 0)),// столбец 0
                    new Line(new Pos(0, 1), new Pos(1, 1), new Pos(2, 1)),// столбец 1
                    new Line(new Pos(0, 2), new Pos(1, 2), new Pos(2, 2)),// столбец 2
                    new Line(new Pos(0, 0), new Pos(1, 1), new Pos(2, 2)),// диагональ
                    new Line(new Pos(0, 2), new Pos(1, 1), new Pos(2, 0)),// другая диаг.
            };

    // Наше зерно - (крестик)
    private Seed ourSeed;
    // Зерно AI - (нолик)
    private Seed oppSeed;

    /**
     * Запускаем мощь интеллекта для поиска оптимальной позиции
     *
     * @param board Игровая доска с крестиками и ноликами
     * @param seed  кто ходит: крестики или нолики
     * @return Позиция, куда надо ставить seed
     */
    public Pos findOptimalMovement(Board board, Seed seed) {
        if (seed == Seed.Empty)
            throw new IllegalArgumentException("seed не должен быть Empty");
        ourSeed = seed;
        oppSeed = seed == Seed.O ? Seed.X : Seed.O;
        Score score = miniMax(board, ourSeed, 4);
        return score.getPos();
    }

    /**
     * Реализация алгоритма MiniMax
     *
     * @param board Игровая доска с крестиками и ноликами
     * @param seed  Чей сейчас ход: крестик или нолик
     * @param depth Максимальная глубина рекурсии
     * @return Ответ содержит оценку для самой оптимальной позиции и саму эту позицию для seed
     */
    private Score miniMax(Board board, Seed seed, int depth) {
        if (seed == Seed.Empty)
            throw new IllegalArgumentException("seed не должен быть Empty");
        // ourSeed зерно - ищем максимум bestScore, oppSeed зерно - ищем минимум bestScore
        int bestScore = (seed == ourSeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        Pos bestPos = null;
        if (depth == 0 || board.getGameStatus().isOver()) {
            // Конец игры или достигнут предел глубины рекурсии
            // Вычисляем оценку игровой ситуации на доске
            bestScore = evaluate(board);
        } else {
            // Клонируем игровую доску со всеми уже поставленными крестиками и ноликами
            // и используем эту копию в рекурсивных вызовах miniMax в теле цикла
            Board clonedBoard = board.createFullCopy();
            // Проходим в цикле по позициям, НЕ ЗАНЯТЫМ зернами
            for (Pos freePos : board.getFreePositions()) {
                // Делаем ход
                clonedBoard.setSeedAtPosition(freePos, seed);
                if (seed == ourSeed) {  // Если сыграл ourSeed
                    // Передаем ход oppSeed
                    int currentScore = miniMax(clonedBoard, oppSeed, depth - 1).getScorePoints();
                    // Ищем максимум оценки игровой ситуации и сохраняем наилучшую для ourSeed позицию
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestPos = freePos;
                    }
                } else {  // Если сыграл oppSeed
                    // Передаем ход ourSeed
                    int currentScore = miniMax(clonedBoard, ourSeed, depth - 1).getScorePoints();
                    // Ищем минимум оценки игровой ситуации и сохраняем наилучшую для oppSeed позицию
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestPos = freePos;
                    }
                }
                // Отменяем ход чтобы восстановить игровую ситуацию перед следующей итерацией цикла
                clonedBoard.setSeedAtPosition(freePos, Seed.Empty);
            }
        }
        return new Score(bestPos, bestScore);
    }

    /**
     * Примитивная функция оценки ситуации для игровой доски.
     * Работает при глубине рекурсии 8 (или просто при отключенной
     * проверке глубины рекурсии).
     *
     * @param board - игровая доска
     * @return Сумма эвристик по всем возможным линиям
     */
    private int evaluateSimple(Board board) {
        GameStatus status = board.getGameStatus();
        if (status.isOver()) {
            return switch (status.getWinnerSeed()) {
                case Empty -> 0;
                case O, X -> ourSeed == status.getWinnerSeed() ? 1 : -1;
            };
        }
        return 0;
    }

    /**
     * Функция оценки ситуации для игровой доски.
     * Хорошо работает при глубине рекурсии 4 и более.
     *
     * @param board - игровая доска
     * @return Сумма эвристик по всем возможным линиям
     */
    private int evaluate(Board board) {
        int score = 0;
        // Вычисление суммарной эвристики по всем 8 линиям доски: 3 строки, 3 стобца, 2 диагонали
        for (Line line : lines) {
            score += evaluateLine(board, line);
        }
        return score;
    }

    /**
     * Эвристическая функция для заданной тремя позициями линии
     *
     * @param board - игровая доска
     * @return +100, +10, +1 для 3-, 2-, 1- поставленных в линию зерен ourSeed.
     * -100, -10, -1 для 3-, 2-, 1- поставленных в линию зерен oppSeed.
     * 0 если в линии есть оба зерна X и O или если доска пустая
     */
    private int evaluateLine(Board board, Line line) {
        int score = 0;

        Seed cell1 = board.getSeedAtPosition(line.getPos(0));
        Seed cell2 = board.getSeedAtPosition(line.getPos(1));
        Seed cell3 = board.getSeedAtPosition(line.getPos(2));

        // cell1
        if (cell1 == ourSeed) {
            score = 1;
        } else if (cell1 == oppSeed) {
            score = -1;
        }

        // cell2
        if (cell2 == ourSeed) {
            if (score == 1) {   // cell1 это ourSeed
                score = 10;
            } else if (score == -1) {  // cell1 это oppSeed
                return 0;
            } else {  // cell1 пустая
                score = 1;
            }
        } else if (cell2 == oppSeed) {
            if (score == -1) { // cell1 это oppSeed
                score = -10;
            } else if (score == 1) { // cell1 это ourSeed
                return 0;
            } else {  // cell1 пустая
                score = -1;
            }
        }

        // cell3
        if (cell3 == ourSeed) {
            if (score > 0) {  // cell1 и/или cell2 это ourSeed
                score *= 10;
            } else if (score < 0) {  // cell1 и/или cell2 это oppSeed
                return 0;
            } else {  // cell1 и cell2 пустые
                score = 1;
            }
        } else if (cell3 == oppSeed) {
            if (score < 0) {  // cell1 и/или cell2 это oppSeed
                score *= 10;
            } else if (score > 0) {  // cell1 и/или cell2 это ourSeed
                return 0;
            } else {  // cell1 и cell2 пустые
                score = -1;
            }
        }
        return score;
    }
}
