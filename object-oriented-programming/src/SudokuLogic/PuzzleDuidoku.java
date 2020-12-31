package SudokuLogic;


/**
 * This class is a child of Puzzle
 * and has all the methods to handle the duidoku puzzle
 */
public class PuzzleDuidoku extends Puzzle {

    public static final int AI_MOVE = 1;
    public static final int PLAYER_MOVE = 2;
    private int lastMove;
    private AI ai;
    //array AIv contains the AI move
    private int[] AIv;

    public PuzzleDuidoku()
    {
        AIv=new int[3];
        ai= new AI();
        board=new Board(4,4);
    }

    @Override
    public boolean gameOver() {
        for(int i=0;i<board.getBoardDimension();i++){
            for(int j=0;j<board.getBoardDimension();j++){
                if(board.getSquareValue(i,j) == 0 && anyValidMoves(i,j)){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *Sets the value of the square in position (x,y) to v, if the move is ruled to be valid by isMoveValid
     *After that, AI try to set a value on the board
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     *@param v The value the square will be set to
     *@return True if value was set successfully, false otherwise
     */
    public boolean setSquareValue(int x,int y,int v){

        if(isMoveValid(x,y,v)){
            board.setSquareValue(x,y,v);
            // If the game is over the ai should not make a move.
            lastMove = PLAYER_MOVE;
            if(gameOver()){
                return true;
            }
            AIv=ai.SimpleMove(board);
            if(AIv!=null){
                board.setSquareValue(AIv[0],AIv[1],AIv[2]);
                lastMove = AI_MOVE;
            }
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
     * Checks if any moves are available for square at position row,col.
     * @param row The row of the square
     * @param col The column of the square
     * @return False if no moves are available, true if a move can be made on this square.
     */
    public boolean anyValidMoves(int row,int col){
        int[] moves = validMoves(row,col);
        for (int value : moves) {
            if (value != 0) {
                return true;
            }
        }
        return false;
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
    private boolean isMoveValid(int x,int y,int v) {
        if(board.getSquareValue(x,y)==0 && v!=0){
            return super.isMoveValid(board, x, y, v);
        }
        return false;
    }

    /**
     * @return The player who made the last move. It should be either AI_MOVE or PLAYER_MOVE.
     */
    public int getLastMove() {
        return lastMove;
    }
}
