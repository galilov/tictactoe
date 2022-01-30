package tictactoe;

public class Game {
    private Board board;
    private Player playerHuman, playerMachine;
    private GameOverHandler gameIsOverHandler;
    private Player nextPlayer;

    public Game() {
        reset();
    }

    public void reset() {
        board = new Board();
        MiniMax ai = new MiniMax();
        playerHuman = new Player(Seed.X, board, ai);
        playerMachine = new Player(Seed.O, board, ai);
        this.nextPlayer = playerHuman;
    }

    public Board getBoard() {
        return board;
    }

    public void doHumanMoveTo(Pos pos) {
        checkPlayer(playerHuman);
        playerHuman.moveTo(pos);
        turnToTheOppositePlayer(playerMachine);
    }

    public Pos doHumanMoveToAi() {
        checkPlayer(playerHuman);
        Pos pos = playerHuman.moveToAi();
        turnToTheOppositePlayer(playerMachine);
        return pos;
    }

    public Pos doMachineMove() {
        checkPlayer(playerMachine);
        Pos pos = playerMachine.moveToAi();
        turnToTheOppositePlayer(playerHuman);
        return pos;
    }

    public void setGameIsOverHandler(GameOverHandler gameIsOverHandler) {
        this.gameIsOverHandler = gameIsOverHandler;
    }

    private void turnToTheOppositePlayer(Player oppositePlayer) {
        GameStatus status = board.getGameStatus();
        if (status.isOver()) {
            nextPlayer = null;
            if (gameIsOverHandler != null) {
                gameIsOverHandler.handleGameIsOver(this, status.getWinnerSeed());
            }
        } else {
            nextPlayer = oppositePlayer;
        }
    }

    private void checkPlayer(Player player) {
        if (nextPlayer != player) {
            throw new IllegalArgumentException("Сейчас не ваш ход!");
        }
    }
}
