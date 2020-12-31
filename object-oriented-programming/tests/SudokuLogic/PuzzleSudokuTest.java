package SudokuLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleSudokuTest {

    PuzzleSudoku sudoku;

    @BeforeEach
    void setSudoku(){
        try
        {
            sudoku=new PuzzleSudoku(1);
        }
        catch (IOException e){
            fail(e);
        }
    }

    @Test
    void setSquareValue() {
        //Because it's first numbers of sudoku game no.1
        assertFalse(sudoku.setSquareValue(0,1,4));
        assertFalse(sudoku.setSquareValue(0,4,3));
        //Because there is this value in this line
        assertFalse(sudoku.setSquareValue(0,0,5));
        //Because there is this value in this box
        assertFalse(sudoku.setSquareValue(4,0,9));
        //This is a valid value
        assertTrue(sudoku.setSquareValue(0,0,4));
    }

    @Test
    void validMoves() {
        int[] valid=new int[9];
        valid[7]=8;
        assertArrayEquals(valid,sudoku.validMoves(0,3));
        valid[2]=3;
        valid[5]=6;
        valid[6]=7;
        valid[7]=0;
        valid[8]=9;
        assertArrayEquals(valid,sudoku.validMoves(1,1));

    }

    @Test
    void isMoveValid() {
        assertTrue(sudoku.setSquareValue(1,1,3));
        assertTrue(sudoku.setSquareValue(1,1,6));
        assertTrue(sudoku.setSquareValue(1,1,7));
        assertTrue(sudoku.setSquareValue(1,1,9));
        assertFalse(sudoku.setSquareValue(1,1,1));
        assertFalse(sudoku.setSquareValue(1,1,2));
        assertFalse(sudoku.setSquareValue(1,1,4));
        assertFalse(sudoku.setSquareValue(1,1,5));
        assertFalse(sudoku.setSquareValue(1,1,8));
    }
}