import javax.swing.*;
import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.concurrent.CompletableFuture;
import java.util.function.ToIntFunction;

/*
Γραμμένος Θεόδωρος 3294 grammenot@csd.auth.gr
 */
public class Mines {

    /**
     * numberArray contains the coordinates of every mine stored as pairs and sorted by
     * The first number is the x position, while the second is the y position.
     */
    public ArrayList<SimpleImmutableEntry<Integer,Integer>> numberArray;
    public SimpleImmutableEntry<Integer,Integer> start,treasure;

    Mines(String fileName){
        loadFile(fileName);
    }

    /**
     * Loads the data for the problem from the specified file
     * @param fileName the name of the file that contains the data
     */
    private void loadFile(String fileName) {
        HashSet<SimpleImmutableEntry<Integer,Integer>> numberSet = new HashSet<>();
        BufferedReader inputReader = null;
        try {
            inputReader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            System.err.println("File " + fileName + " not found!");
            System.exit(1);
        }
        String line;
        try {
            // Read start coordinates
            line = inputReader.readLine();
            String[] numArray = line.split(" ");
            start = new SimpleImmutableEntry<>(Integer.parseInt(numArray[0]),Integer.parseInt(numArray[1]));
            // Read end coordinates
            line = inputReader.readLine();
            numArray = line.split(" ");
            treasure = new SimpleImmutableEntry<>(Integer.parseInt(numArray[0]),Integer.parseInt(numArray[1]));
            // Add them to the number set
//            numberSet.add(start);
//            numberSet.add(treasure);
            // Read the mine points
            while ((line = inputReader.readLine()) != null) {
                numArray = line.split(" ");
                SimpleImmutableEntry<Integer,Integer> tempPair = new SimpleImmutableEntry<>(Integer.parseInt(numArray[0]),Integer.parseInt(numArray[1]));
                numberSet.add(tempPair);
            }
        }catch (Throwable e){
            e.printStackTrace();
        }
        createSortedArray(numberSet);
    }
    private void createSortedArray(HashSet<SimpleImmutableEntry<Integer,Integer>> numberSet){
        numberArray = new ArrayList<SimpleImmutableEntry<Integer,Integer>>();
        numberArray.addAll(numberSet);
        // Compare the x values
        // In case the x values are the same sort by the y value
        numberArray.sort(Comparator.comparingInt((ToIntFunction<SimpleImmutableEntry<Integer, Integer>>) SimpleImmutableEntry::getKey).thenComparingInt(SimpleImmutableEntry::getValue));
    }

    /**
     * Calculates the top and bottom hulls and returns the hull with the smallest length
     * @return a string with the description of the shortest hull
     */
    public String quickHull(){
        // First split the points in 2 groups, left of the pStart->pEnd and right of pStart->pEnd
        ArrayList<SimpleImmutableEntry<Integer,Integer>> leftPoints = new ArrayList<>();
        ArrayList<SimpleImmutableEntry<Integer,Integer>> rightPoints = new ArrayList<>();
        for(SimpleImmutableEntry<Integer,Integer> point:numberArray){
            // start is q1,treasure is q2,point is q3
            // S = x1*y2+x3*y1+x2*y3-x3*y2-x2*y1-x1*y3
            int determinant = triangleSurface(start,treasure,point);
            if(determinant > 0){
                leftPoints.add(point);
            }else{
                rightPoints.add(point);
            }
        }
        // Calculate the top and bottom hulls add the first and last points
        LinkedList<SimpleImmutableEntry<Integer,Integer>> topHull = findHull(leftPoints,start,treasure);
        topHull.addFirst(start);
        topHull.addLast(treasure);
        LinkedList<SimpleImmutableEntry<Integer,Integer>> bottomHull = findHull(rightPoints,treasure,start);
        bottomHull.addFirst(treasure);
        bottomHull.addLast(start);
        // Find the shortest path and select an iterator for the list.
        Iterator<SimpleImmutableEntry<Integer,Integer>> it;
        double topDistance = calculatePathDistance(topHull);
        double bottomDistance = calculatePathDistance(bottomHull);
        double minDistance;
        if(topDistance<bottomDistance){
            it = topHull.iterator();
            minDistance = topDistance;
        }else{
            // On the bottom hull we use a reverse iterator as the points go from the end to the start
            it = bottomHull.descendingIterator();
            minDistance = bottomDistance;
        }
        StringBuilder description = new StringBuilder();
        // Print the distance
        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        description.append("The shortest distance is ");
        description.append(df.format(minDistance));

        description.append("\nThe shortest path is:");
        while(it.hasNext()){
            SimpleImmutableEntry<Integer,Integer> nextPoint = it.next();
            description.append("(");
            description.append(nextPoint.getKey());
            description.append(",");
            description.append(nextPoint.getValue());
            description.append(")");
            if(it.hasNext()){
                description.append("-->");
            }
        }
        description.append('\n');
        description.append(topHull.size() + bottomHull.size() -2 );
        return description.toString();
    }

    /**
     * Calculates the distance of the path formed by the points of the list given
     * @param points a sequence of points
     * @return the total distance
     */
    private double calculatePathDistance(LinkedList<SimpleImmutableEntry<Integer,Integer>> points){
        double distance = 0;
        Iterator<SimpleImmutableEntry<Integer,Integer>> it = points.iterator();
        SimpleImmutableEntry<Integer,Integer> currentPoint,nextPoint;
        currentPoint = it.next();
        while(it.hasNext()){
            nextPoint = it.next();
            int x2,x1,y2,y1;
            x2 = nextPoint.getKey();
            x1 = currentPoint.getKey();
            y2 = nextPoint.getValue();
            y1 = currentPoint.getValue();
            distance = distance + Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
            currentPoint = nextPoint;
        }
        return distance;
    }

    /**
     * Calculates the surface of the triangle formed by the points supplied. Also returns a positive number if
     * q3 is to the left of q1->q2
     * @param q1 the start point of the line
     * @param q2 the ending point of the line
     * @param q3 the third point
     * @return the surface of the triangle that is formed, positive if q3 is to the left of the line q1->q2
     */
    private int triangleSurface(SimpleImmutableEntry<Integer,Integer> q1,SimpleImmutableEntry<Integer,Integer> q2,SimpleImmutableEntry<Integer,Integer> q3){
        return q1.getKey()*q2.getValue() + q3.getKey()*q1.getValue()+q2.getKey()*q3.getValue()-q3.getKey()*q2.getValue()
                -q2.getKey()*q1.getValue()-q1.getKey()*q3.getValue();
    }

    /**
     * Calculates the angle ABC
     * @return the angle formed by ABC
     */
    private double angle(SimpleImmutableEntry<Integer,Integer> a,SimpleImmutableEntry<Integer,Integer> b,SimpleImmutableEntry<Integer,Integer> c){
        // AB*BC = ||AB|| * ||BC|| * cos(Θ) where θ the angle ABC
        SimpleImmutableEntry<Integer,Integer> AB = new SimpleImmutableEntry<>(b.getKey()-a.getKey(),b.getValue()-a.getValue());
        SimpleImmutableEntry<Integer,Integer> BC = new SimpleImmutableEntry<>(c.getKey()-b.getKey(),c.getValue()-b.getValue());
        // Lengths
        double lenAB = Math.sqrt(Math.pow(AB.getKey(),2)+Math.pow(AB.getValue(),2));
        double lenBC = Math.sqrt(Math.pow(BC.getKey(),2)+Math.pow(BC.getValue(),2));

        double dotProduct = AB.getKey()*BC.getKey() + AB.getValue()*BC.getValue();

        return Math.acos(dotProduct/(lenAB*lenBC));
    }

    /**
     * Calculates the hull between two points from the given point set
     * @param points a set containing the points
     * @param start the start point
     * @param end the end point
     * @return a linkedlist of the points in the hull
     */
    public LinkedList<SimpleImmutableEntry<Integer,Integer>> findHull(ArrayList<SimpleImmutableEntry<Integer,Integer>> points,
                         SimpleImmutableEntry<Integer,Integer> start,
                         SimpleImmutableEntry<Integer,Integer> end){
        // Create a list containing the hull points
        LinkedList<SimpleImmutableEntry<Integer,Integer>> hullList = new LinkedList<>();
        // If the point set is empty return an empty list
        if(points.isEmpty()){
            return new LinkedList<>();
        }
        // Find the point that maximizes the surface of the triangle Pmax Pstart Pend
        int maxSurface = -1;
        SimpleImmutableEntry<Integer,Integer> maxPoint = null;
        for(SimpleImmutableEntry<Integer,Integer> point: points){
            // start is q1,end is q2,point is q3
            // S = x1*y2+x3*y1+x2*y3-x3*y2-x2*y1-x1*y3
            int currentSurface = triangleSurface(start,end,point);
            if(currentSurface > maxSurface) {
                maxSurface = currentSurface;
                maxPoint = point;
            }
            else if(currentSurface == maxSurface){
                // Pick the point that maximises the angle
                if(angle(start,point,end)>angle(start,maxPoint,end)){
                    maxPoint = point;
                }
            }
        }
        hullList.add(maxPoint);
        // Find the points to the left of p1->pMax and to the left of pMax->pEnd`
        // We say that point q3 is to the left of the line q1q2 directed from point q1 to point
        // q2 if q1 q2 q3 forms a counterclockwise cycle.
        ArrayList<SimpleImmutableEntry<Integer,Integer>> listStart = new ArrayList<>(points.size()/2);
        ArrayList<SimpleImmutableEntry<Integer,Integer>> listEnd = new ArrayList<>(points.size()/2);
        for (SimpleImmutableEntry<Integer, Integer> point : points) {
            // Check relative to p1->pMax
            // start is q1,pMax is q2,point is q3
            int position = triangleSurface(start,maxPoint,point);
            if (position > 0) {
                listStart.add(point);
            }else{
                // Check if point is left of pMax->pEnd
                // Same as before but q1 is pEnd,q2 is pMax and q3 is current point
                position = triangleSurface(maxPoint,end,point);
                if(position > 0){
                    listEnd.add(point);
                }
            }
        }
        // Add the points that are on the left of pMax->pEnd
        hullList.addAll(1,findHull(listEnd,maxPoint,end));
        // Add the points of the hull that are left of pStar->pMax
        hullList.addAll(0,findHull(listStart,start,maxPoint));
        return hullList;
    }

    public static void main(String[] args) {
        Mines m = new Mines(args[0]);
        System.out.println(m.quickHull());
    }
}
