package SudokuLogic;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @org.junit.jupiter.api.Test
    void testConstructor(){
        assertThrows(IllegalArgumentException.class,()->{
            new Board(9,10);
        });
    }

    @org.junit.jupiter.api.Test
    void testSetGetSquareValue() {
        Board b = new Board(4,4);
        assertThrows(IllegalArgumentException.class,()->{
            b.setSquareValue(5,3,5);
        });
        assertThrows(IllegalArgumentException.class,()->{
            b.setSquareValue(5,3,5);
        });
        assertThrows(IllegalArgumentException.class,()->{
            b.setSquareValue(4,4,5);
        });
        b.setSquareValue(1,1,3);
        assertEquals(3,b.getSquareValue(1,1));
        assertEquals(0,b.getSquareValue(2,3));
    }

    @org.junit.jupiter.api.Test
    void getBoardDimension() {
        Board b = new Board(4,4);
        assertEquals(4,b.getBoardDimension());
        b = new Board(9,9);
        assertEquals(9,b.getBoardDimension());
    }
}