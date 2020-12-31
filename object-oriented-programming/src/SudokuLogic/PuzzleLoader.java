package SudokuLogic;

import SudokuInterface.MainMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.awt.*;
import java.io.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * This class contains static methods responsible for creating Board for the respective Puzzle types.
 * Puzzles are stored under the puzzles directory. The puzzle with id ID is stored in the ID.txt file under the
 * respective directory.
 */
 class PuzzleLoader {

    private static final HashMap<String,Color>  killerColours = new HashMap<>(){{
        put("pink", Color.decode("#eddeeb"));
        put("green",Color.decode("#e7edde"));
        put("blue",Color.decode("#dee2ed"));
        put("red",Color.decode("#eddede"));
        put("darkgreen",Color.decode("#deedea"));
    }};

    /**
     * Creates a board to be used for the Sudoku game type.
     * @param id The id of the puzzle
     * @return A board containing the initial values of the puzzle.
     * @see PuzzleSudoku
     */
     static Board loadSudoku(int id) throws IOException{
        Board tempBoard = new Board(9,9);
        String seperator = File.separator;
        String filePath = System.getProperty("user.dir") + seperator + "puzzles" + seperator + "sudoku" + seperator
                + "Sudoku_" + id;
        int reader=0;
        int x=0;
        int y=0;
        int v=0;
        FileReader in=new FileReader(filePath);
        while(reader!=-1){
            x=Character.getNumericValue((char) in.read());
            reader=in.read();
            y=Character.getNumericValue((char) in.read());
            reader=in.read();
            v=Character.getNumericValue((char) in.read());
            reader=in.read();
            tempBoard.setSquareValue(x,y,v);
        }
        return tempBoard;
    }

    /**
     * Creates an array of the groups that comprise a Killer Sudoku board. The array will be used by the
     * PuzzleKillerSudoku class
     * @param id The id of the puzzle
    */
     static KillerSudokuGroup[] loadKiller(int id) throws IOException {
        // Assemble the filepath
        String seperator = File.separator;
        String filePath = System.getProperty("user.dir") + seperator + "puzzles" + seperator + "killer" + seperator
                + id + ".json";
        // Open the file
        FileReader reader = new FileReader(filePath);
        // We create an array that contains HashMaps for each group
        HashMap<String, Object>[] groupsFromJson = new Gson().fromJson(
                reader, new TypeToken<HashMap<String, Object>[]>() {}.getType()
        );
        KillerSudokuGroup[] groupsArray = new KillerSudokuGroup[groupsFromJson.length];
        int j = 0;
        for(HashMap h: groupsFromJson){
            // Get the colour name and then get its hex value
            String colourName;
            colourName = (String) h.get("colour");
            Color colourId = killerColours.get(colourName);
            // Need to process the sum as it is parsed as a double
            double sum = (double) h.get("sum");
            KillerSudokuGroup tempGroup = new KillerSudokuGroup((int) sum,colourId);
            // Arrays containing the x and y coordinates of the squares
            ArrayList<Double> xArray = (ArrayList<Double>) h.get("xvalues");
            ArrayList<Double> yArray = (ArrayList<Double>) h.get("yvalues");
            // Add each square to the group
            for(int i=0;i<xArray.size();i++){
                // Need to type cast to int separately again
                tempGroup.addSquare(xArray.get(i).intValue(), yArray.get(i).intValue());
            }
            // Add the group to the array containing all the groups
            groupsArray[j] = tempGroup;
            j++;
        }
        return groupsArray;
    }

    public static int getAvailablePuzzles(int mode){
        // Assemble the filepath
        String seperator = File.separator;
        String filePath = System.getProperty("user.dir") + seperator + "puzzles" + seperator;
        switch (mode){
            case MainMenu.SUDOKU:{
                filePath += "sudoku";
                break;
            }

            case MainMenu.KILLER:{
                filePath += "killer";
                break;
            }
            default:{
                throw new IllegalArgumentException();
            }
        }
        return new File(filePath).listFiles().length;
    }
}
