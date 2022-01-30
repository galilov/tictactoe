package tictactoe;

class Score {
    private final Pos pos;
    private final int scorePoints;

    Pos getPos() {
        return pos;
    }

    int getScorePoints() {
        return scorePoints;
    }

    Score(Pos pos, int scorePoints) {
        this.pos = pos;
        this.scorePoints = scorePoints;
    }
}
