package SudokuInterface;

import SudokuLogic.KillerSudokuGroup;
import SudokuLogic.Puzzle;
import SudokuLogic.PuzzleDuidoku;
import SudokuLogic.PuzzleKillerSudoku;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

import java.util.AbstractMap;
import java.util.HashSet;

/**
 * SudokuBoard puts SudokuBoxes on a grid layout to create a complete board.
 */
public class SudokuBoard {
    private JPanel frame;
    private SudokuBox[][] boxes;
    private Puzzle puzzle;
    private GameWindow parent;
    private boolean helpMode = false;
    private boolean isWordoku;

    /**
     * Creates a SudokuBoard that represents the specified Puzzle.
     * @param puzzle The Puzzle this SudokuBoard represents
     * @param parent The GameWindow that this SudokuBoard is in
     * @param wordoku Whether wordoku is enabled
     */
    public SudokuBoard(Puzzle puzzle, GameWindow parent, boolean wordoku){
        this.puzzle = puzzle;
        this.parent = parent;
        frame = new JPanel();

        // Wordoku Stuff
        this.isWordoku = wordoku;

        // Set up the board
        int rows = puzzle.getDimension();
        int cols = puzzle.getDimension();
        frame.setLayout(new GridLayout(rows,cols,0,0));

        createBoxes(rows,cols);

        frame.setBackground(Color.black);

        refreshBoard();
    }

    /**
     * This method is called by SudokuBox every time the user tries to enter a number in a box. It checks if the number
     * is accepted by the puzzle, then it checks if the game has ended and finally if the game is Duidoku it calls
     * a method.
     * @return Whether the number was set successfully
     */
    boolean placeNumber(int x, int y, int n){
        boolean ok =  puzzle.setSquareValue(x,y,n);
        if(ok && puzzle.gameOver()){
            parent.gameOver();
        }
        if(ok && puzzle instanceof PuzzleDuidoku){
            disableBoxesWithNoMoves();
            refreshBoard();
        }
        return ok;
    }

    /**
     * showHints is called by a SudokuBox when Help Mode is active and displays the valid moves for that square.
     * @param row The row of the square that the user clicked.
     * @param col The column of the square the user clicked.
     */
    void showHints(int row,int col){
        Wordoku w = null;
        int[] validMoves = puzzle.validMoves(row,col);
        StringBuilder text = new StringBuilder("Valid moves are:");
        if(isWordoku){
            w = new Wordoku(9);
        }
        for(int i: validMoves){
            if(i!=0) {
                text.append(" ");
                if(isWordoku){
                    text.append(w.intToChar(i));
                }else {
                    text.append(i);
                }
                text.append(" ");
            }
        }
        JOptionPane.showMessageDialog(frame, text);
    }

    boolean isHelpMode() {
        return helpMode;
    }

    void setHelpMode(boolean b){
        helpMode = b;
    }

    /**
     * createBoxes populates the 2-dimensional array boxes and sets up each box with its correct parameters
     * @param rows The number of rows
     * @param cols The number of columns
     */
    private void createBoxes(int rows,int cols){
        boxes = new SudokuBox[rows][cols];
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                boxes[i][j] = new SudokuBox(this,i,j, new Wordoku(puzzle.getDimension()));
                boxes[i][j].setBorder(createBorder(i,j));
            }
        }

        if(puzzle instanceof PuzzleKillerSudoku){
            setupKillerBoxes();
        }

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                frame.add(boxes[i][j]);
            }
        }
    }

    /**
     * setupKillerBoxes is called when the puzzle is Killer Sudoku. It adds the features to the boxes that are unique
     * to Killer Sudoku. Specifically it adds color to squares of the same group and displays the sum of the group in
     * one of its squares.
     */
    private void setupKillerBoxes(){
        KillerSudokuGroup[] groups = ((PuzzleKillerSudoku) puzzle).getGroups();
        for(KillerSudokuGroup group: groups){
            HashSet<AbstractMap.SimpleImmutableEntry<Integer,Integer>> pairs = group.getSquareCoordinates();
            Color groupColor = group.getColor();
            boolean sumDisplayed = false;
            for(AbstractMap.SimpleImmutableEntry<Integer,Integer> pair: pairs){
                int row = pair.getKey();
                int col = pair.getValue();
                boxes[row][col].setColor(groupColor);
                // Set the sum to display on a square of each group
                if(!sumDisplayed){
                    boxes[row][col].setSum(group.getSum());
                    sumDisplayed = true;
                }
            }
        }
    }

    public JPanel getPanel(){
        return frame;
    }

    /**
     * Displays a simple dialog when the user's move is denied. To be called by SudokuBox.
     */
    void moveNotAllowed(){
        JOptionPane.showMessageDialog(frame, "This move is not allowed!");
    }

    /**
     * createBorder creates a MatteBorder that should be placed on a Box at the specified coordinates. For example
     * a box at the top must only have a border at the top.
     * @param row The row of the Box
     * @param col The column of the Box
     * @return A MatteBorder to be placed at the box
     */
    private MatteBorder createBorder(int row,int col){
        // Base border is 1
        int top = 1,left = 1,bottom = 1, right = 1;
        int boardDim = puzzle.getDimension();
        int largeSquareDim = (int) Math.sqrt(boardDim);
        // Decrease boardDim by 1 because of zero based indexing. If we have 9 rows, the first one is 0 and the last is 8.
        boardDim -= 1;
        // First row
        if(row==0){
            top = 10;
        }
        // First column
        if(col == 0){
            left = 10;
        }
        // Last row
        if(row == boardDim){
            bottom = 10;
        }
        // Last column
        if(col == boardDim){
            right = 10;
        }
        if(row != boardDim){
            if(row%largeSquareDim == largeSquareDim -1){
                bottom = 5;
            }
        }
        if(col != boardDim){
            if(col%largeSquareDim == largeSquareDim -1){
                right = 5;
            }
        }
        return BorderFactory.createMatteBorder(top,left,bottom,right,Color.BLACK);
    }

    /**
     * This method is used to update the board, inserting new values when the computer has made a move(for duidoku)
     * or to set the initial square values(in sudoku).
     */
    private void refreshBoard(){
        for(int i=0;i<puzzle.getDimension();i++){
            for(int j=0;j<puzzle.getDimension();j++){
                    boxes[i][j].setValueOverride(puzzle.getSquareValue(i,j));
            }
        }
    }

    /**
     * Disables all the boxes that have no moves available.
     */
    private void disableBoxesWithNoMoves(){
        if(!(puzzle instanceof PuzzleDuidoku)){
            throw new IllegalArgumentException();
        }
        PuzzleDuidoku p = (PuzzleDuidoku) puzzle;
        for(int i=0;i<puzzle.getDimension();i++){
            for(int j=0;j<puzzle.getDimension();j++) {
                // If puzzle box has not been set and no moves are available
                if (puzzle.getSquareValue(i,j) == 0 && !p.anyValidMoves(i,j)){
                    boxes[i][j].disableBox();
                }
            }
        }
    }

    /**
     * @return Whether wordoku is enabled
     */
    boolean isWordoku() {
        return isWordoku;
    }
}
