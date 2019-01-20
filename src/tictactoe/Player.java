package tictactoe;

class Player implements IPlayer {

    private final Seed ourSeed;
    private final IBoard board;
    private final IAI ai;

    Player(Seed seed, IBoard board, IAI ai) {
        this.ourSeed = seed;
        this.board = board;
        this.ai = ai;
    }

    @Override
    public Pos moveToAi() {
        Pos pos = ai.findOptimalMovement(board, getSeed());
        board.setSeedAtPosition(pos, ourSeed);
        return pos;
    }

    @Override
    public void moveTo(Pos pos) {
        board.setSeedAtPosition(pos, getSeed());
    }

    @Override
    public Seed getSeed() {
        return ourSeed;
    }
}
