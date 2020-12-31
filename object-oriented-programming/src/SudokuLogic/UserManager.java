package SudokuLogic;
import SudokuInterface.MainMenu;

import java.util.HashSet;
import java.io.*;
import java.io.IOException;
import java.util.Random;

/**
 * This class save and load all the Users of the game in/from a text
 * and use all the informations about them to
 * choose a puzzle that they haven't played
 * and counts wins/defeats of duidoku game
 */
public class UserManager  {

    private HashSet<User> users;
    private User currentUser;


    public UserManager() throws IOException {
        users= new HashSet<User>();
        currentUser=null;
        loadFromDisc();
    }


    /**
     *Loads the users from the text
     */
    private void loadFromDisc() throws IOException{
        try(ObjectInputStream in =new ObjectInputStream(new FileInputStream("puzzles/users/user.txt")))
        {
            while(true) {
                User user = (User) in.readObject();
                if (user != null) {
                    users.add(user);
                }
                else{return;}
            }
        }
        catch (IOException e){ }
        catch (Throwable e){ }
    }


    /**
     *Saves the users on a text
     */
    public void saveToDisc() throws IOException{

        try(ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream("puzzles/users/user.txt")))
        {
            for(User user:users){
                out.writeObject(user);
            }
        }
        catch (IOException e)
        {
            System.out.println("IOException sto write");
        }
        catch (Throwable e)
        {
            System.out.println("Throwable sto write");

        }
    }


    /**
     *Adds a user on the set
     * @param name of the user
     * @return true if the user added
     */
    public boolean addUser(String name){
        if(!existUser(name)){
            users.add(new User(name));
            return true;
        }
        return false;
    };


    /**
     *Checks if the user is already on the set
     * @param name of the user
     * @return true if the user is already on the set
     */
    public boolean existUser(String name){
        for(User userName:users){
            if(userName.getName().equals(name)){
                return true;
            }
        }
        return  false;
    };


    /**
     * @param mode A gamemode
     * @param id The id of the puzzle
     * @return Whether the user has played the puzzle with the given id for this mode
     */
    public boolean userHasPlayed(int mode,int id){
        if(!isUserLogin()){
            throw new IllegalStateException();
        }
        if(mode == MainMenu.SUDOKU){
            return currentUser.hasSudokuPlayed(id);
        }
        else if(mode == MainMenu.KILLER){
            return currentUser.hasKillerPlayed(id);
        }
        else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * @return if the user is logged in
     */
    public boolean isUserLogin(){
        return currentUser != null;
    }


    /**
     *Log in the user if the user exist on the set
     * @param name: the name of the user
     * @return true if the user log in
     */
    public boolean login(String name){
        if(existUser(name)){
            for (User user:users){
                if(user.getName().equals(name)){
                    currentUser=user;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * If mode is Sudoku or Killer Sudoku marks the puzzle as played for the player that is currently logged in.
     * @param mode The game mode (SUDOKU or KILLER)
     * @param id The id of the puzzle if the mode is SUDOKU/KILLER.
     */
    public void addUserPlayed(int mode,int id){
        if(!isUserLogin()){
            throw new IllegalStateException();
        }

        if(mode == MainMenu.SUDOKU){
            currentUser.addSudokuPlay(id);
        }
        else if(mode == MainMenu.KILLER){
            currentUser.addKillerPlay(id);
        }
        try {
            saveToDisc();
        }
        catch (IOException ignored){

        }
    }

    /**
     * Adds a win/defeat to the player's duidoku stats
     * @param playerWon Whether the player won the game.
     */
    public void addDuidokuStats(boolean playerWon){
        if(!isUserLogin()){
            throw new IllegalStateException();
        }
        if(playerWon){
            currentUser.addDuidokuWins();
        }else {
            currentUser.addDuidokuDefeats();
        }
        try{
            saveToDisc();
        }catch (IOException ignored){};
    }


    /**
     *
     * @param mode (Sudoku/SudokuKiller)
     * @return an unplayed puzzle of current user
     */
    public int getUnplayedPuzzle(int mode){
        int available;
        try {
            available = PuzzleLoader.getAvailablePuzzles(mode);
        }
        catch (IllegalArgumentException e){
            throw e;
        }
        if(!isUserLogin()){
            // If no user is logged in get a random puzzle
            Random r = new Random();
            r.setSeed(System.currentTimeMillis());
            if(available == 1){
                return 1;
            }
            return r.nextInt(available-1) + 1;
        }
        // Get the first unplayed puzzle
        switch (mode){
            case MainMenu.SUDOKU:{
                for(int i=1;i<=available;i++){
                    if(!currentUser.hasSudokuPlayed(i)){
                        return i;
                    }
                }
                break;
            }

            case MainMenu.KILLER:{
                for(int i=1;i<=available;i++){
                    if(!currentUser.hasKillerPlayed(i)){
                        return i;
                    }
                }
            }
        }
        return -1;
    }


    /**
     * @return the duidoku wins of current user
     */
    public int getCurrentUserDuidokuWins(){
        if(isUserLogin()){
            return currentUser.getDuidokuWins();
        }
        throw new IllegalStateException();
    }


    /**
     *
     * @return the duidoku defeats of current user
     */
    public int getCurrentUserDuidokuDefeats(){
        if(isUserLogin()){
            return currentUser.getDuidokuDefeats();
        }
        throw new IllegalStateException();
    }

    /**
     *Log off the user
     */
    public void logoff(){
        currentUser=null;
    }

}

