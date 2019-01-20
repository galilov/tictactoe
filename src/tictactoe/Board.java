package tictactoe;

import java.util.*;

class Board implements IBoard {
    private static final int N = 3;
    private final Seed cells[][] = new Seed[N][N];
    private final Set<Pos> emptyPositions = new HashSet<>();

    Board() {
        forEachElement((row, col) -> {
            cells[row][col] = Seed.Empty;
            emptyPositions.add(new Pos(row, col));
        });
    }

    private static void forEachElement(IHandler action) {
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                action.doAction(row, col);
            }
        }
    }

    @Override
    public Set<Pos> getEmptyPositions() {
        return Collections.unmodifiableSet(emptyPositions);
    }

    @Override
    public Seed getSeedAtPosition(Pos pos) {
        return cells[pos.getRow()][pos.getCol()];
    }

    @Override
    public void setSeedAtPosition(Pos pos, Seed seed) {
        Seed currentSeed = cells[pos.getRow()][pos.getCol()];
        if (currentSeed != Seed.Empty) {
            throw new GameException("Позиция " + pos + " уже занята!");
        }
        if (currentSeed == seed) return;
        cells[pos.getRow()][pos.getCol()] = seed;
        emptyPositions.remove(pos);
    }

    @Override
    public GameStatus getGameStatus() {
        final int rowScores[] = new int[N];
        final int colScores[] = new int[N];
        final int diag1Score[] = new int[1];
        final int diag2Score[] = new int[1];

        forEachElement((row, col) -> {
            Seed seed = this.cells[row][col];
            int delta = getDelta(seed);
            rowScores[row] += delta;
            colScores[col] += delta;

            if (row == col) {
                diag1Score[0] += delta;
            }

            if (row == N - col - 1) {
                diag2Score[0] += delta;
            }
        });

        for (Seed seed : new Seed[]{Seed.O, Seed.X}) {
            final int winPoints = N * getDelta(seed);
            for (int i = 0; i < N; i++) {
                if (rowScores[i] == winPoints || colScores[i] == winPoints)
                    return new GameStatus(true, seed);
            }
            if (diag1Score[0] == winPoints || diag2Score[0] == winPoints)
                return new GameStatus(true, seed);
        }

        return new GameStatus(getEmptyPositions().isEmpty(), Seed.Empty);
    }

    private int getDelta(Seed seed) {
        if (seed == Seed.X) return 1;
        else if (seed == Seed.O) return -1;
        return 0;
    }

    @Override
    public IBoard duplicate() {
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
