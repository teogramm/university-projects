package SudokuLogic;

import SudokuInterface.MainMenu;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UserManagerTest {
    private UserManager um;
    private static final int availableSudokuPuzzles = 10;
    private static final int availableKillerPuzzles = 10;

    @BeforeAll
    static void deleteUsersFile(){
        File f = new File("puzzles/users/user.txt");
        if(f.exists()){
            f.delete();
        }
    }

    @BeforeEach
    void setUp(){
        try {
            um = new UserManager();
        }catch (IOException e){
            fail(e);
        }
    }

    @Test
    void addUser() {
        boolean userAdded = um.addUser("test1");
        assertTrue(userAdded);
        assertFalse(um.addUser("test1"));
        um.login("test1");
        assertEquals(um.getCurrentUserDuidokuWins(),0);
        assertEquals(um.getCurrentUserDuidokuDefeats(),0);
    }

    @Test
    void existUser() {
        um.addUser("test2");
        assertTrue(um.existUser("test2"));
        assertFalse(um.existUser("mitsos"));
    }

    @Test
    void isUserLogin() {
        assertFalse(um.login("userThatDoesNotExist"));
        um.addUser("test");
        um.login("test");
        assertTrue(um.isUserLogin());
    }

    @Test
    void addUserPlayed() {
        assertThrows(IllegalStateException.class,()->{
            um.addUserPlayed(MainMenu.SUDOKU,1);
        });

        um.addUser("test");
        um.login("test");
        um.addUserPlayed(MainMenu.SUDOKU,1);
        assertTrue(um.userHasPlayed(MainMenu.SUDOKU,1));
        assertFalse(um.userHasPlayed(MainMenu.SUDOKU,2));
        um.addUserPlayed(MainMenu.KILLER,1);
        assertTrue(um.userHasPlayed(MainMenu.KILLER,1));
        assertFalse(um.userHasPlayed(MainMenu.KILLER,2));
    }

    @Test
    void testUserHasPlayed(){
        assertThrows(IllegalStateException.class,()->{
            um.userHasPlayed(MainMenu.SUDOKU,1);
        });
        um.addUser("test");
        um.login("test");
        assertThrows(IllegalArgumentException.class,()->{
            um.userHasPlayed(188,1);
        });
    }

    @Test
    void addDuidokuStats() {
        assertThrows(IllegalStateException.class,()->{
            um.addDuidokuStats(true);
        });
        um.addUser("test");
        um.login("test");
        um.addDuidokuStats(true);
        assertEquals(um.getCurrentUserDuidokuWins(),1);
        um.addDuidokuStats(false);
        assertEquals(um.getCurrentUserDuidokuDefeats(),1);
    }

    @Test
    void testDuidokuStats(){
        // No user logged in
        assertThrows(IllegalStateException.class,()->{
            um.getCurrentUserDuidokuDefeats();
        });
        assertThrows(IllegalStateException.class,()->{
            um.getCurrentUserDuidokuWins();
        });
    }

    @Test
    void getUnplayedPuzzle() {
        assertThrows(IllegalArgumentException.class,()->{
            um.getUnplayedPuzzle(188);
        });

        int randomPuzzle = um.getUnplayedPuzzle(MainMenu.SUDOKU);
        assertTrue(randomPuzzle>0 && randomPuzzle<=availableSudokuPuzzles);

        randomPuzzle = um.getUnplayedPuzzle(MainMenu.KILLER);
        assertTrue(randomPuzzle>0 && randomPuzzle<=availableKillerPuzzles);

        um.addUser("test");
        um.login("test");
        um.addUserPlayed(MainMenu.SUDOKU,1);
        um.addUserPlayed(MainMenu.KILLER,1);
        randomPuzzle = um.getUnplayedPuzzle(MainMenu.SUDOKU);
        assertFalse(um.userHasPlayed(MainMenu.SUDOKU,randomPuzzle));
        randomPuzzle = um.getUnplayedPuzzle(MainMenu.KILLER);
        assertFalse(um.userHasPlayed(MainMenu.KILLER,randomPuzzle));
    }

    @Test
    void logoff() {
        assertFalse(um.isUserLogin());
        um.addUser("test");
        um.login("test");
        um.logoff();
        assertFalse(um.isUserLogin());
    }
}