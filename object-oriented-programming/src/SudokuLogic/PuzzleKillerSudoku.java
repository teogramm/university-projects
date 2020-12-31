package SudokuLogic;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * PuzzleKillerSudoku is a child of PuzzleSudoku that implements the additional
 * checks required for Killer Sudoku
 */
public class PuzzleKillerSudoku extends PuzzleSudoku {

    /**
     * groups is an array that contains all the groups that make up the board
     */
    private KillerSudokuGroup[] groups;

    public PuzzleKillerSudoku(int id) throws IOException {
        super();

        groups = PuzzleLoader.loadKiller(id);
    }

    @Override
    public boolean setSquareValue(int x, int y, int v) {
        if(isMoveValid(x,y,v)){
            board.setSquareValue(x,y,v);
            return true;
        }
        return false;
    }

    /**
     * isMoveValid implements the additional checks required for Killer Sudoku. Specifically,
     * it checks that the number is unique to this group and that the total sum is smaller than
     * the total sum of the group.
     * @param x The position of the square on the vertical axis
     * @param y The position of the square on the horizontal axis
     * @param v The value the square will be set to
     * @return if the move is allowed under killer sudoku rules
     */
    @Override
    protected boolean isMoveValid(int x, int y, int v) {
        if(super.isMoveValid(x, y, v)) {
            KillerSudokuGroup tempGroup = null;
            // Find the group that contains the number
            for (KillerSudokuGroup g : groups) {
                if (g.contrains(x, y)) {
                    tempGroup = g;
                    break;
                }
            }
            if (tempGroup == null) {
                return false;
            }

            // Get all the squares that belong in that group
            HashSet<AbstractMap.SimpleImmutableEntry<Integer, Integer>> squares = tempGroup.getSquareCoordinates();
            int sum = 0;
            for (AbstractMap.SimpleImmutableEntry<Integer, Integer> square : squares) {
                // The key is the vertical coordinate
                int squarex = square.getKey();
                int squarey = square.getValue();
                int squareValue = board.getSquareValue(squarex, squarey);
                if (squareValue == v && v!=0) {
                    return false;
                }
                sum += squareValue;
            }
            // Check if adding the value makes it go over the allowed sum. If it does not return true.
            return sum + v <= tempGroup.getSum();
        }
        return false;
    }

    @Override
    public int[] validMoves(int x, int y) {
        ArrayList<Integer> validNumbers=new ArrayList<>(board.getBoardDimension());
        for(int i=1;i<=board.getBoardDimension();i++){
            if (isMoveValid(board,x,y,i)) {
                validNumbers.add(i);
            }
        }
        int[] finalNumbers = new int[validNumbers.size()];
        int k = 0;
        for(Integer i:validNumbers){
            finalNumbers[k] = i;
            k++;
        }
        return finalNumbers;
    }


    /**
     * Returns a copy of the array that contains the groups of this puzzle.
     */
    public KillerSudokuGroup[] getGroups(){
        return groups.clone();
    }
}
