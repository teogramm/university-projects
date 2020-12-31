package SudokuLogic;
import java.io.Serializable;
import java.util.HashSet;


/**
 * This class has all the informations of a user,
 * sudoku and killer sudoku puzzles that the user has played
 * and user's wins and defeats of duidoku puzzles
 */
public class User implements Serializable {

    private String name;
    private HashSet<Integer> sudokuPlayed;
    private HashSet<Integer> killerPlayed;
    private int duidokuWins;
    private int duidokuDefeats;

    public User(String name){
        sudokuPlayed=new HashSet<Integer>();
        killerPlayed=new HashSet<Integer>();
        duidokuDefeats=0;
        duidokuWins=0;
        this.name=name;
    }


    /**
     *Checks if the user has played a sudoku puzzle
     * @param id: the id of a specific sudoku puzzle
     * @return true if the user has already play the puzzle
     */
    boolean hasSudokuPlayed(int id){
        return sudokuPlayed.contains(id);
    }


    /**
     * Checks if the user has played a killer sudoku game
     * @param id: the id of a specific killer sudoku puzzle
     * @return true if the user has already play the puzzle
     */
    boolean hasKillerPlayed(int id){
        return killerPlayed.contains(id);
    }



    /**
     * @return all the wins of the user
     */
     int getDuidokuWins(){
        return duidokuWins;
    }



    /**
     * @return all the defeats of the user
     */
     int getDuidokuDefeats(){
        return duidokuDefeats;
    }



    /**
     *Adds one win more if the user win the duidoku game
     */
     void addDuidokuWins(){
        duidokuWins++;
    }


    /**
     *Adds one defeat more if the user lose the duidoku game
     */
     void addDuidokuDefeats(){
        duidokuDefeats++;
    }


    /**
     *Adds a sudoku puzzle on the set
     * of the user has played
     * @param id:the id of the sudoku puzzle
     */
     void addSudokuPlay(int id){
        sudokuPlayed.add(id);
    }


    /**
     * Adds a killer sudoku puzzle on the set
     * of the user has played
     * @param id:the id of the killer sudoku puzzle
     */
     void addKillerPlay(int id){
        killerPlayed.add(id);
    }


    /**
     * @return The name of the user
     */
     String getName(){
        return name;
    }
}
