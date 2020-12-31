package SudokuLogic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {


    User user_1 = new User("John");
    User user_2 = new User("Paul");


    @Test
    public void hasSudokuPlayed() {
        assertFalse(user_1.hasSudokuPlayed(0));
        assertFalse(user_1.hasSudokuPlayed(1));
        assertFalse(user_1.hasSudokuPlayed(2));
        assertFalse(user_2.hasSudokuPlayed(0));
        assertFalse(user_2.hasSudokuPlayed(1));
        assertFalse(user_2.hasSudokuPlayed(2));
    }

    @Test
    public void hasKillerPlayed() {
        assertFalse(user_1.hasKillerPlayed(0));
        assertFalse(user_1.hasKillerPlayed(1));
        assertFalse(user_1.hasKillerPlayed(2));
        assertFalse(user_2.hasKillerPlayed(0));
        assertFalse(user_2.hasKillerPlayed(1));
        assertFalse(user_2.hasKillerPlayed(2));
    }

    @Test
    public void getDuidokuWins() {
        assertEquals(0,user_1.getDuidokuWins());
        assertEquals(0,user_2.getDuidokuWins());
    }

    @Test
    public void getDuidokuDefeats() {
        assertEquals(0,user_1.getDuidokuDefeats());
        assertEquals(0,user_2.getDuidokuDefeats());
    }

    @Test
    public void addDuidokuWins() {
        for(int i=0;i<10;i++){
            user_1.addDuidokuWins();
        }
        user_2.addDuidokuWins();
        assertEquals(10,user_1.getDuidokuWins());
        assertEquals(1,user_2.getDuidokuWins());
    }

    @Test
    public void addDuidokuDefeats() {
        for(int i=0;i<7;i++){
            user_2.addDuidokuDefeats();
        }
        user_1.addDuidokuDefeats();
        assertEquals(7,user_2.getDuidokuDefeats());
        assertEquals(1,user_1.getDuidokuDefeats());
    }

    @Test
    public void addSudokuPlay() {
        user_1.addSudokuPlay(6);
        user_1.addSudokuPlay(7);
        user_2.addSudokuPlay(8);
        user_2.addSudokuPlay(9);
        assertTrue(user_1.hasSudokuPlayed(6));
        assertTrue(user_1.hasSudokuPlayed(7));
        assertTrue(user_2.hasSudokuPlayed(8));
        assertTrue(user_2.hasSudokuPlayed(9));
        assertFalse(user_1.hasSudokuPlayed(0));
        assertFalse(user_2.hasSudokuPlayed(0));
        assertFalse(user_1.hasSudokuPlayed(8));
        assertFalse(user_2.hasSudokuPlayed(7));
    }

    @Test
    public void addKillerPlay() {
        user_1.addKillerPlay(2);
        user_1.addKillerPlay(3);
        user_2.addKillerPlay(4);
        user_2.addKillerPlay(5);
        assertFalse(user_1.hasKillerPlayed(0));
        assertFalse(user_2.hasKillerPlayed(0));
        assertFalse(user_1.hasKillerPlayed(4));
        assertFalse(user_2.hasKillerPlayed(3));
        assertFalse(user_1.hasKillerPlayed(5));
        assertFalse(user_2.hasKillerPlayed(2));
        assertTrue(user_1.hasKillerPlayed(2));
        assertTrue(user_1.hasKillerPlayed(3));
        assertTrue(user_2.hasKillerPlayed(4));
        assertTrue(user_2.hasKillerPlayed(5));
    }

    @Test
    public void getName() {
        assertEquals("John",user_1.getName());
        assertEquals("Paul",user_2.getName());
    }
}

