package tictactoe;

class Player {

    private final Seed seed;
    private final Board board;
    private final MiniMax ai;

    Player(Seed seed, Board board, MiniMax ai) {
        if (seed == Seed.Empty)
            throw new IllegalArgumentException("seed не должен быть Empty");
        this.seed = seed;
        this.board = board;
        this.ai = ai;
    }

    public Pos moveToAi() {
        Pos pos = ai.findOptimalMovement(board, seed);
        board.setSeedAtPosition(pos, seed);
        return pos;
    }

    public void moveTo(Pos pos) {
        board.setSeedAtPosition(pos, seed);
    }
}
