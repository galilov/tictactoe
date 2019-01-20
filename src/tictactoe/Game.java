package tictactoe;

public class Game {
    private final IBoard board;
    private final IPlayer playerHuman, playerMachine;
    private final IGameOverHandler gameIsOverHandler;
    private IPlayer nextPlayer;

    public Game(IGameOverHandler gameIsOverHandler) {
        this.gameIsOverHandler = gameIsOverHandler;
        board = new Board();
        IAI ai = new MiniMax();
        playerHuman = new Player(Seed.X, board, ai);
        playerMachine = new Player(Seed.O, board, ai);
        this.nextPlayer = playerHuman;
    }

    public IBoard getBoard() {
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

    private void turnToTheOppositePlayer(IPlayer oppositePlayer) {
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

    private void checkPlayer(IPlayer player) {
        if (nextPlayer != player) {
            throw new GameException("Сейчас не ваш ход!");
        }
    }

}
