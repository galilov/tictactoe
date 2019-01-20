package tictactoe;

import java.util.Set;

public interface IBoard {
    Set<Pos> getEmptyPositions();
    Seed getSeedAtPosition(Pos pos);
    void setSeedAtPosition(Pos pos, Seed symbol);
    GameStatus getGameStatus();
    IBoard duplicate();
}
