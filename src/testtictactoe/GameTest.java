package testtictactoe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tictactoe.Game;
import tictactoe.GameException;
import tictactoe.Pos;

import java.util.InputMismatchException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    private boolean stopGame = false;

    @BeforeEach
    void setUp() {
        game = new Game((sender, winner) -> {
            stopGame = true;
            System.out.println(sender.getBoard());
            switch (winner) {
                case Empty:
                    System.out.println("Ничья");
                    break;
                case X:
                    System.out.println("Победил ЧЕЛОВЕК");
                    break;
                case O:
                    System.out.println("Победил ИСКУССТВЕННЫЙ РАЗУМ");
                    break;
            }
        });
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void doPlay() {
        do {
            try {
                System.out.println(game.getBoard());
                game.doHumanMoveToAi();
                if (stopGame) break;
                game.doMachineMove();
                if (stopGame) break;
            } catch (GameException exception) {
                System.err.println(exception.getMessage());
            }
        } while (true);
    }


}