package tictactoe;

public class GameStatus {
    private final boolean isOver;
    private final Seed winner;

    public GameStatus(boolean isFinished, Seed winnerSeed) {
        this.isOver = isFinished;
        this.winner = winnerSeed;
    }

    public boolean isOver() {
        return isOver;
    }

    public Seed getWinnerSeed() {
        return winner;
    }
}
