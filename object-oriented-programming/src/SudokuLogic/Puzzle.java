package SudokuLogic;

import java.lang.Math;

/**
The Puzzle class defines a common set of methods and fields that Puzzle subclasses must implement.
 */
public abstract class Puzzle {
    protected Board board;

    /**
    Returns whether the game is over.
    @return True if game is over, false otherwise
    */
    public abstract boolean gameOver();

    /**
     *Sets the value of the square in position (x,y) to v, if the move is ruled to be valid by isMoveValid
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     *@param v The value the square will be set to
     *@return True if value was set successfully, false otherwise
    // *@see Puzzle#isMoveValid(int x, int y, int z)
    */

    public abstract boolean setSquareValue(int x,int y,int v);
    /**
     * Returns an array containing the valid numbers that can be placed on the square at position (x,y).
     * Intended to be used when the user asks for a hint.
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     */
   public abstract int[] validMoves(int x,int y);

    /**
     * @return The dimension of the board
     */
   public int getDimension(){return board.getBoardDimension();}

    /**
     * This method checks if the specified value v is allowed to be placed at the square (x,y). This method
     * contains the rules of the Puzzle that decide whether a move is valid or not. It is called by setSquareValue
     * when the user tries to make a move.
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     *@param v The value the square will be set to
     *@return True if move is allowed, false otherwise
     */
    protected boolean isMoveValid(Board board,int x,int y,int v){

        //checks the numbers
        if (x<0 || x>=board.getBoardDimension() || y<0 || y>=board.getBoardDimension() || v<0 || v>board.getBoardDimension()) {
            return false;
        }



        //checks lines
        if(v!=0) {
            for (int i = 0; i < board.getBoardDimension(); i++) {
                if (board.getSquareValue(x, i) == v) {
                    return false;
                }
                if (board.getSquareValue(i, y) == v) {
                    return false;
                }
            }
        }

        int  d= (int) Math.sqrt(board.getBoardDimension());
        //checks the box
        if(v!=0) {
            for (int i = (int) (d* (x / d)); i <= d * (x / d) + (d-1); i++) {
                for (int j = (int) (d * (y / d)); j <= d * (y / d) + (d-1); j++) {
                    if (board.getSquareValue(i,j) == v) {
                        return false;
                    }

                }
            }
        }

        return true;
    }

    /**
     *Gets the value of the square in position (row,col)
     *@param row The position of the square on the vertical axis
     *@param col The position of the square on the horizontal axis
     *@return The value of the square in position(row,col)
     */
    public int getSquareValue(int row,int col){
        return board.getSquareValue(row, col);
    }

}
