package tictactoe;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class UI implements ActionListener, GameOverHandler {
    private final static int N = 3;
    private final JFrame frame = new JFrame();
    private final JButton[] buttons = new JButton[N * N];
    private final Game game;
    private final Executor executor;
    private boolean isGameOver;

    UI(Game game) {
        this.game = game;
        game.setGameIsOverHandler(this);
        executor = Executors.newSingleThreadExecutor();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.getContentPane().setBackground(new Color(50, 50, 50));
        frame.setTitle("Tic Tac Toe");
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        var buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(N, N));
        buttonPanel.setBackground(new Color(150, 150, 150));
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                var button = new JButton();
                button.setActionCommand(String.format("%d%d", row, col));
                buttonPanel.add(button);
                button.setFont(new Font("Ink Free", Font.BOLD, 120));
                button.setFocusable(false);
                button.addActionListener(this);
                buttons[row * N + col] = button;
            }
        }
        frame.add(buttonPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        var button = (JButton) e.getSource();
        var actionCommand = button.getActionCommand();
        var row = Integer.parseInt(actionCommand.substring(0, 1));
        var col = Integer.parseInt(actionCommand.substring(1, 2));
        button.setText("X");
        button.setEnabled(false);
        game.doHumanMoveTo(new Pos(row, col));
        if (!isGameOver) {
            executor.execute(() -> {
                Pos pos = game.doMachineMove();
                EventQueue.invokeLater(() -> uiShowAiStep(pos));
            });
        }
    }

    private void uiShowAiStep(Pos pos) {
        var button = buttons[pos.getRow() * N + pos.getCol()];
        button.setText("O");
        button.setEnabled(false);
    }

    @Override
    public void handleGameIsOver(Game game, Seed winner) {
        isGameOver = true;
        EventQueue.invokeLater(() -> uiHandleGameIsOver(winner));
    }

    private void uiHandleGameIsOver(Seed winner) {
        for (var i = 0; i < N * N; i++) {
            buttons[i].setEnabled(false);
        }
        var message = "???";
        switch (winner) {
            case Empty -> message = "НИЧЬЯ";
            case O -> message = "Выиграла МАШИНА";
            case X -> message = "Выиграл ЧЕЛОВЕК";
        }
        JOptionPane.showMessageDialog(frame, message);
        reset();
    }

    private void reset() {
        for (var i = 0; i < N * N; i++) {
            var button = buttons[i];
            button.setText("");
            button.setEnabled(true);
        }
        isGameOver = false;
        game.reset();
    }
}
