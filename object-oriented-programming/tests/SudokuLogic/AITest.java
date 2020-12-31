package SudokuLogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AITest {



    @Test
    void simpleMove() {
        Board board=new Board(4,4);
        AI ai=new AI();
        int[] array=new int[3];
        array[2]=1;
        assertArrayEquals(array,ai.SimpleMove(board));
        board.setSquareValue(0,0,1);
        board.setSquareValue(0,1,2);
        array[1]=2;
        array[2]=3;
        assertArrayEquals(array,ai.SimpleMove(board));


    }
}