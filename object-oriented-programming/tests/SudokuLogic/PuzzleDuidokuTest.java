package SudokuLogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleDuidokuTest {

    PuzzleDuidoku duidoku=new PuzzleDuidoku();

    @Test
    void gameOver() {
        assertFalse(duidoku.gameOver());
        assertTrue(duidoku.setSquareValue(0,0,1));
        assertTrue(duidoku.setSquareValue(0,2,3));
        assertTrue(duidoku.setSquareValue(1,0,3));
        assertTrue(duidoku.setSquareValue(1,2,1));
        assertTrue(duidoku.setSquareValue(2,0,2));
        assertTrue(duidoku.setSquareValue(2,2,4));
        assertTrue(duidoku.setSquareValue(3,0,4));
        assertTrue(duidoku.setSquareValue(3,2,2));
        assertTrue(duidoku.gameOver());
    }

    @Test
    void setSquareValue() {
        assertTrue(duidoku.setSquareValue(0,0,1));
        assertFalse(duidoku.setSquareValue(0,1,3));
        assertFalse(duidoku.setSquareValue(0,3,1));
    }

    @Test
    void validMoves_anyValidMoves() {
        int[] validNums=new int[4];
        duidoku.setSquareValue(0,0,1);
        validNums[2]=3;
        validNums[3]=4;
        assertArrayEquals(validNums,duidoku.validMoves(0,2));
        assertTrue(duidoku.anyValidMoves(0,2));
        duidoku.setSquareValue(1,2,4);
        assertFalse(duidoku.anyValidMoves(0,3));

    }


    @Test
    void getLastMove() {
        assertTrue(duidoku.setSquareValue(0,0,1));
        assertEquals(1,duidoku.getLastMove());
    }
}