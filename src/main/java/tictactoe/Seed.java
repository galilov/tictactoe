package tictactoe;

public enum Seed {
    Empty("-"), O("O"), X("X");

    private final String value;

    Seed(String value) {
        this.value = value;
    }

    public String toString(){
        return value;
    }
}
