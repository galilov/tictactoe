package tictactoe;

public class Pos {
    private final int row;
    private final int col;

    public Pos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pos)) return false;
        Pos other = (Pos) o;
        if (other == this) return true;
        return other.row == row && other.col == col;
    }

    @Override
    public int hashCode() {
        return row ^ col;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }
}
