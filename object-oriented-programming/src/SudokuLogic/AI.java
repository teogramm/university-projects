package SudokuLogic;

/**
 *AI is the opponent of the player
 *the AI plays after the player's move
 */
class AI {
    //array AIarray contains the AI move(x,y,v)
    private int[] AIarray;

    AI(){
        AIarray=new int[3];
    }


    /**
    *This method makes a simple move
    *@param board the board that the AI makes the move
    *@return int[x,y,v] dimensions and value of the AI move
    */
    int[] SimpleMove(Board board) {

        for (int x = 0; x < board.getBoardDimension(); x++) {
            for (int y = 0; y < board.getBoardDimension(); y++) {
                for (int v = 1; v <= board.getBoardDimension(); v++) {
                    if (board.getSquareValue(x, y) == 0 && isMoveValid(board, x, y, v)) {
                        AIarray[0] = x;
                        AIarray[1] = y;
                        AIarray[2] = v;
                        return AIarray;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method checks if the specified value v is allowed to be placed at the square (x,y). This method
     * contains the rules of the Puzzle that decide whether a move is valid or not. It is called by SimpleMove
     * when the ai tries to make a move.
     *@param board The board that make the move
     *@param x The position of the square on the vertical axis
     *@param y The position of the square on the horizontal axis
     *@param v The value the square will be set to
     *@return True if move is allowed, false otherwise
     */
    private boolean isMoveValid(Board board, int x,int y,int v){
        //checks lines
        for (int i = 0; i < board.getBoardDimension(); i++) {
            if (board.getSquareValue(x, i) == v) {
                return false;
            }
            if (board.getSquareValue(i, y) == v) {
                return false;
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
}
