package tictactoe;

interface IPlayer {
    Pos moveToAi();
    void moveTo(Pos pos);
    Seed getSeed();
}
