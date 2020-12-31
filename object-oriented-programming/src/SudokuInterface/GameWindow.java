package SudokuInterface;

import SudokuLogic.Puzzle;
import SudokuLogic.PuzzleDuidoku;
import SudokuLogic.PuzzleKillerSudoku;
import SudokuLogic.PuzzleSudoku;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Gamewindow represents a window that contains a board and additional tooltips(like help)
 */
class GameWindow {
    private JDialog gameWindow;
    private JPanel buttons;
    private SudokuBoard gameBoard;
    private MainMenu parent;
    private int mode;
    private Puzzle puzzle;
    private ResourceBundle messages;

    /**
     * @param parent The MainMenu that created this GameWindow object
     * @param puzzle The puzzle that this window represents
     * @param messages A ResourceBundle that contains the prompts of the game
     * @param wordoku Whether wordoku is enabled
     * @throws IOException When a puzzle cannot be read from disk
     */
    GameWindow(MainMenu parent, Puzzle puzzle, ResourceBundle messages, boolean wordoku) throws IOException {
        this.parent = parent;
        this.puzzle = puzzle;
        this.messages = messages;
        if(puzzle instanceof PuzzleKillerSudoku){
            mode = MainMenu.KILLER;
        }
        else if(puzzle instanceof PuzzleSudoku){
            mode = MainMenu.SUDOKU;
        }
        else if(puzzle instanceof PuzzleDuidoku){
            mode = MainMenu.DUIDOKU;
        }


        gameWindow = new JDialog(parent.getMainWindow(),true);
        gameWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        gameBoard = new SudokuBoard(puzzle, this, wordoku);

        createButtons();

        gameWindow.getContentPane().setLayout(new BorderLayout());
        gameWindow.add(buttons,BorderLayout.PAGE_START);
        gameWindow.add(gameBoard.getPanel(),BorderLayout.CENTER);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.pack();
        gameWindow.setSize(2*gameWindow.getWidth(),2*gameWindow.getHeight());
        gameWindow.setVisible(true);
    }

    /**
     * createButtons creates a panel that contains buttons that affect the game. It contains
     */
    private void createButtons() {
        buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setAlignmentY(Component.TOP_ALIGNMENT);
        // Create the help button
        JToggleButton hint = new JToggleButton(messages.getString("help"));
        JLabel label = new JLabel();
        hint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AbstractButton abstractButton = (AbstractButton) actionEvent.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                if (selected) {
                    gameBoard.setHelpMode(true);
                    label.setText(messages.getString("helpExplanation"));
                } else {
                    gameBoard.setHelpMode(false);
                    label.setText("");
                }
            }
        });
        buttons.add(hint);
        buttons.add(label);
    }

    void gameOver() {
        gameWindow.dispose();
        if(mode != MainMenu.DUIDOKU) {
            parent.gameOver(mode);
        }else {
            // Check if the player won the game
            PuzzleDuidoku p = (PuzzleDuidoku) puzzle;
            // If the last move was made by the player, then the player won.
            parent.duidokuGameOver(p.getLastMove() == PuzzleDuidoku.PLAYER_MOVE);
        }
    }
}
