package SudokuLogic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This class is a child of Puzzle
 * and has all the methods to handle the classic sudoku puzzle
 */
public class PuzzleSudoku extends Puzzle {

    //firstX and firstY contains the positions of
    //the Boards starting values
    //that numbers are not allowed to change during the game
    private ArrayList<Integer> firstX;
    private ArrayList<Integer> firstY;


    public PuzzleSudoku(){
        board=new Board(9,9);
    }

    /**
     *The constructor of Puzzle Sudoku loads a Sudoku game
     * @param number (1-10) sudoku games
     */
    public PuzzleSudoku(int number) throws IOException {
        super();
        board= PuzzleLoader.loadSudoku(number);
        firstX = new ArrayList<Integer>();
        firstY = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board.getSquareValue(i, j) != 0) {
                    firstX.add(i);
                    firstY.add(j);
                }
            }
        }
    }

    /**
     *Sets the value of the square in position (x,y) to v, if the move is ruled to be valid by isMoveValid
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     *@param v The value the square will be set to
     *@return True if value was set successfully, false otherwise
     */
    public boolean setSquareValue(int x,int y, int v){
        if(isMoveValid(x,y,v)){
            board.setSquareValue(x,y,v);
            return true;
        }
        return false;
    }


    /**
     * Returns an array containing the valid numbers that can be placed on the square at position (x,y).
     * Intended to be used when the user asks for a hint.
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     */
    public int[] validMoves(int x,int y){
        int[] validNumbers=new int[board.getBoardDimension()];
        for(int i=1;i<=board.getBoardDimension();i++){
            if (isMoveValid(board,x,y,i)) {
                validNumbers[i-1]=i;
            }
        }
        return validNumbers;
    }

    /**
     * This method checks if the specified value v is allowed to be placed at the square (x,y). This method
     * contains the rules of the Puzzle that decide whether a move is valid or not. It is called by setSquareValue
     * when the user tries to make a move.
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     *@param v The value the square will be set to
     *@return True if move is allowed, false otherwise
     */
    protected boolean isMoveValid(int x, int y, int v) {
        boolean isValid=true;
        isValid=super.isMoveValid(board,x,y,v);

        //Checks the first numbers
        // In killer sudoku there are no inital values to check
        if(!(this instanceof PuzzleKillerSudoku)) {
            Iterator<Integer> a = firstX.iterator();
            Iterator<Integer> b = firstY.iterator();
            while (a.hasNext()) {
                int nextx = a.next();
                int nexty = b.next();
                if (nextx == x && nexty == y) {
                    return false;
                }
            }
        }
        return isValid;
    }

    @Override
    public boolean gameOver(){
        for(int i=0;i<board.getBoardDimension();i++){
            for(int j=0;j<board.getBoardDimension();j++){
                if(board.getSquareValue(i,j)==0){
                    return false;
                }
            }
        }
        return true;
    }
}
