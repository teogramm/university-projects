package SudokuLogic;

import SudokuInterface.MainMenu;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PuzzleLoaderTest {
    public static final int AVAILABLE_SUDOKU = 10;
    public static final int AVAILABLE_KILLER = 10;

    @Test
    void loadSudoku() {

        PuzzleLoader loader=new PuzzleLoader();
        Board board=new Board(9,9);
        try{
            board=loader.loadSudoku(1);
        }
        catch (IOException e){fail(e);}

        Board testBoard=new Board(9,9);
        testBoard.setSquareValue(0,1,2);
        testBoard.setSquareValue(0,4,1);
        testBoard.setSquareValue(0,5,5);
        testBoard.setSquareValue(0,7,9);
        testBoard.setSquareValue(1,5,2);
        testBoard.setSquareValue(1,6,8);
        testBoard.setSquareValue(1,8,4);
        testBoard.setSquareValue(2,0,1);
        testBoard.setSquareValue(2,3,7);
        testBoard.setSquareValue(2,5,4);
        testBoard.setSquareValue(2,6,3);
        testBoard.setSquareValue(3,1,1);
        testBoard.setSquareValue(3,2,5);
        testBoard.setSquareValue(3,3,6);
        testBoard.setSquareValue(3,4,9);
        testBoard.setSquareValue(3,5,3);
        testBoard.setSquareValue(3,6,2);
        testBoard.setSquareValue(4,3,5);
        testBoard.setSquareValue(4,5,1);
        testBoard.setSquareValue(5,2,9);
        testBoard.setSquareValue(5,3,4);
        testBoard.setSquareValue(5,4,2);
        testBoard.setSquareValue(5,5,8);
        testBoard.setSquareValue(5,6,1);
        testBoard.setSquareValue(5,7,5);
        testBoard.setSquareValue(6,2,1);
        testBoard.setSquareValue(6,3,2);
        testBoard.setSquareValue(6,5,7);
        testBoard.setSquareValue(6,8,3);
        testBoard.setSquareValue(7,0,3);
        testBoard.setSquareValue(7,2,4);
        testBoard.setSquareValue(7,3,1);
        testBoard.setSquareValue(8,1,5);
        testBoard.setSquareValue(8,3,3);
        testBoard.setSquareValue(8,4,4);
        testBoard.setSquareValue(8,7,8);
        for(int i=0;i<9;i++){
            for(int j=0;j<9;j++){
                assertEquals(board.getSquareValue(i,j),testBoard.getSquareValue(i,j));
            }
        }
    }

    @Test
    void loadKiller() {
        // Test some boxes as a sample
        KillerSudokuGroup[] groups;
        try {
            groups = PuzzleLoader.loadKiller(1);
        }catch (IOException e){
            fail(e);
            return;
        }
        assertTrue(groups[0].contrains(0,0));
        assertEquals(Color.decode("#eddede"),groups[0].getColor());
        assertEquals(11,groups[0].getSum());
        boolean found = false;
        for (KillerSudokuGroup group : groups) {
            if (group.contrains(3, 0)) {
                assertEquals(Color.decode("#dee2ed"),group.getColor());
                assertEquals(40,group.getSum());
                found = true;
                break;
            }
        }
        if(!found){
            fail("Box 3,0 was not found in any group!");
        }
    }

    @Test
    void getAvailablePuzzles() {
        assertEquals(AVAILABLE_SUDOKU,PuzzleLoader.getAvailablePuzzles(MainMenu.SUDOKU));
        assertEquals(AVAILABLE_KILLER,PuzzleLoader.getAvailablePuzzles(MainMenu.KILLER));
    }
}