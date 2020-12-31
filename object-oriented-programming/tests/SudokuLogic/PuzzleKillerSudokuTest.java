package SudokuLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleKillerSudokuTest {
    private PuzzleKillerSudoku killerSudoku;

    @BeforeEach
    void setUp() {
        try {
            // If puzzle 1 is changed, tests will fail
            killerSudoku = new PuzzleKillerSudoku(1);
            assertEquals(9,killerSudoku.getDimension());
        }catch (IOException e){
            fail("Puzzle 1 does not exist on disk!", e);
        }
    }

    @Test
    void setSquareValue() {
        killerSudoku.setSquareValue(0,0,1);
        // Check that placing on the same row is not allowed
        assertFalse(killerSudoku.setSquareValue(0,6,1));
        // Same column
        assertFalse(killerSudoku.setSquareValue(4,0,1));
        // Same box
        assertFalse(killerSudoku.setSquareValue(1,1,1));
        // Check that going over the sum in a group is not allowed
        killerSudoku.setSquareValue(1,0,9);
        assertFalse(killerSudoku.setSquareValue(1,1,3));
    }

    @Test
    void validMoves() {
        int[] validMoves = killerSudoku.validMoves(0,0);
        // We have nothing on the board so every number can be placed
        for(int i=1;i<=9;i++){
            assertEquals(i,validMoves[i-1]);
        }
        // Place a number on the same row
        killerSudoku.setSquareValue(0,4,1);
        validMoves = killerSudoku.validMoves(0,0);
        for(int i:validMoves){
            assertNotEquals(1,i);
        }
        // Place a number on the same column
        killerSudoku.setSquareValue(3,0,2);
        validMoves = killerSudoku.validMoves(0,0);
        for(int i:validMoves){
            assertNotEquals(2,i);
        }
        // Place a number on the same group
        killerSudoku.setSquareValue(1,1,3);
        validMoves = killerSudoku.validMoves(0,0);
        for(int i:validMoves){
            assertNotEquals(3,i);
        }
        // Place a number on the same box
        killerSudoku.setSquareValue(2,2,4);
        validMoves = killerSudoku.validMoves(0,0);
        for(int i:validMoves){
            assertNotEquals(4,i);
        }
    }

}