package SudokuLogic;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashSet;

/**
 * KillerSudokuGroup represents a group of squares with a given color and sum.
 */
public class KillerSudokuGroup {
    private int sum;
    /**
     * squareCoordinates is a set of Integer pairs containing the coordinates of the squares that belong to that
     * group. It uses the SimpleImmutableEntry class to store the x,y values of the squares. The key is the x coordinate
     * while the value is the y coordinate.
     */
    private HashSet<AbstractMap.SimpleImmutableEntry<Integer,Integer>> squareCoordinates;
    private Color color;

    public KillerSudokuGroup(int sum,Color color){
        this.sum = sum;
        this.color = color;
        squareCoordinates = new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public HashSet<AbstractMap.SimpleImmutableEntry<Integer, Integer>> getSquareCoordinates() {
        return (HashSet<AbstractMap.SimpleImmutableEntry<Integer, Integer>>) squareCoordinates.clone();
    }

    /**
     * contains checks if the square with the specified coordinates belongs to this group.
     * @param x The x coordinate of the square
     * @param y The y coordinate of the square
     * @return True if the square belongs to this group, false otherwise
     */
    public boolean contrains(int x,int y){
        // Create a temporary SimpleImmutableEntry and see if it is in the set
        AbstractMap.SimpleImmutableEntry<Integer,Integer> temp = new AbstractMap.SimpleImmutableEntry<>(x,y);
        return squareCoordinates.contains(temp);
    }

    /**
     * Adds the square with the specified coordinates to the group
     * @param x The x coordinate of the square
     * @param y The y coordinate of the square
     */
    public void addSquare(int x,int y){
        AbstractMap.SimpleImmutableEntry<Integer,Integer> temp = new AbstractMap.SimpleImmutableEntry<>(x,y);
        squareCoordinates.add(temp);
    }

    public Color getColor(){
        return color;
    }

    public int getSum(){
        return sum;
    }
}
