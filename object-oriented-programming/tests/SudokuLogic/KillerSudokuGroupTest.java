package SudokuLogic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class KillerSudokuGroupTest {
    private KillerSudokuGroup g;

    @BeforeEach
    void setUp(){
        g = new KillerSudokuGroup(11, Color.red);
    }

    @Test
    void addSquare() {
        g.addSquare(1,1);
        assertTrue(g.contrains(1,1));
        g.addSquare(8,8);
        assertTrue(g.contrains(8,8));
    }

    @Test
    void getColor() {
        assertEquals(Color.red,g.getColor());
    }

    @Test
    void getSum() {
        assertEquals(11,g.getSum());
    }
}