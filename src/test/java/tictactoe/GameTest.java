package tictactoe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {
    private Game game;
    private boolean stopGame = false;
    private boolean result;

    @BeforeEach
    void setUp() {
        result = false;
        game = new Game();
        game.setGameIsOverHandler((sender, winner) -> {
            stopGame = true;
            System.out.println(sender.getBoard());
            switch (winner) {
                case Empty:
                    System.out.println("Ничья");
                    result = true;
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
        Assertions.assertTrue(result);
    }


}