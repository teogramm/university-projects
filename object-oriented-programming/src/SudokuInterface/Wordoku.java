package SudokuInterface;

import java.util.HashMap;

/**
 * This class handles character conversion in Wordoku
 */
public class Wordoku {
    private HashMap<Integer,Character> integerToChar;
    private HashMap<Character,Integer> charToInteger;

    /**
     * Sets up a wordoku class that matches numbers from 1 to size given to consecutive letters int the alphabet
     * starting from A
     * @param size The ammount of numbers to assign
     */
    Wordoku(int size){
        if(size>26){
            throw new IllegalArgumentException("Cannot have more than 26 letters");
        }
        Character tempChar = 'A';
        integerToChar = new HashMap<>(size);
        charToInteger = new HashMap<>(size);
        for(int i=1;i<=size;i++){
            integerToChar.put(i,tempChar);
            charToInteger.put(tempChar,i);
            tempChar++;
        }
    }

    /**
     * @param i The number to match
     * @return The character that matches to this number, null if this number does not match to any character
     */
    public Character intToChar(Integer i){
        return integerToChar.getOrDefault(i, null);
    }

    /**
     * @param c The character to match
     * @return The number that matches to this character, null if this character does not match to any number
     */
    public Integer charToInt(Character c){
        return charToInteger.getOrDefault(c,null);
    }
}
