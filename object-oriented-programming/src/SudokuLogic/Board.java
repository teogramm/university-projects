package SudokuLogic;

/** The Board class represents a Sudoku board of set dimensions.
 * It is essentially a grid.
 * {@literal <-----y------->}
 * _______________
 * |__|__|__|__|__| |
 * |__|__|__|__|__| |
 * |__|__|__|__|__| x
 * |__|__|__|__|__| |
 */
 public class Board {
    private int[][] board;
    private int dimension;

    /** Constructs a new board with the specified dimensions
     *
     * @param x The number of squares on the vertical axis of the board
     * @param y The number of squares on the horizontal axis of the board
     */
    public Board(int x,int y){
        if(x != y){
            throw new IllegalArgumentException("Board must be square!");
        }
        board = new int[x][y];
        dimension=x;
    }

    /** Returns the value of the specified square on the board
     *
     * @param x The x position of the square
     * @param y The y position of the square
     * @return The value of the square
     */
     public int getSquareValue(int x,int y){
         if(x>=dimension || y>=dimension){
             throw new IllegalArgumentException("Coordinates out of bounds!");
         }
        return board[x][y];
    }

    /**
     * Changes the value of the specified square on the board
     * @param x The x position of the square
     * @param y The y position of the square
     * @param value The new value of the square
     */
     public void setSquareValue(int x,int y,int value){
         if(x>=dimension || y>=dimension){
             throw new IllegalArgumentException("Coordinates out of bounds!");
         }
        board[x][y] = value;
    }

    /**
     *
     * @return Returns the dimension of the board
     */
     public int getBoardDimension(){
        return dimension;
    }
}
