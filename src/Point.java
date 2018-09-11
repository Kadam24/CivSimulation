import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point {
    private ArrayList<Point> neighbors;
    private int currentState;
    private int nextState;
    private int numStates = 6;
    private int growthValue;
    private int militaryValue;
    private int currentCiv;

    public Point() {
        Random generator = new Random();
        int tmp = generator.nextInt(4);
        currentCiv = 0;
        growthValue = 4 - tmp;
        militaryValue = tmp;
        currentState = 0;
        nextState = 0;
        neighbors = new ArrayList<Point>();
    }

    public void clicked() {
        currentState = (++currentState) % numStates;
    }

    public int getState() {
        return currentState;
    }

    public void setState(int s, int civ) {
        currentState = s;
        currentCiv = civ;
    }

    public void calculateNewState() {
        //TODO: insert logic which updates according to currentState and
        if (currentState == 0 && countNeighbours() > 3)
            nextState = 1;
        /*else if (currentState == 1 && (countNeighbours()>3 || countNeighbours() <2) )
            nextState = 0;*/
        else
            nextState = currentState;
        //System.out.print("elo");
    }

    public void changeState() {
        currentState = nextState;
    }

    public void addNeighbor(Point nei) {
        neighbors.add(nei);
    }

    private int countNeighbours() {
        int count = 0;
        for (Point n : neighbors) {
            if (n.currentState == 1)
                count += 1;
        }
        return count;
    }

    public int getGrowthValue() {
        return growthValue;
    }

    public void setGrowthValue(int growthValue) {
        this.growthValue = growthValue;
    }

    public int getMilitaryValue() {
        return militaryValue;
    }

    public void setMilitaryValue(int militaryValue) {
        this.militaryValue = militaryValue;
    }

    public int getCurrentCiv() {
        return currentCiv;
    }

    public void setCurrentCiv(int currentCiv) {
        this.currentCiv = currentCiv;
    }

    public List<Integer> neighborTakenBy() {
        List<Integer> civs = new ArrayList<Integer>();
        for (Point neighbor : neighbors) {
            if (neighbor.getCurrentCiv() != 0) {
                civs.add(neighbor.getCurrentCiv());
            }
        }
        return civs;
    }

    public int checkIfSurrounded() {
        int hostileNeighbors = 0;
        int idToReturn = 0;
        for (Point neighbor : neighbors) {
            if (neighbor.getCurrentCiv() != currentCiv && neighbor.getCurrentCiv() != 0) {
                hostileNeighbors++;
                idToReturn = neighbor.currentCiv;
            }
        }
        if (hostileNeighbors > 7) {
            return idToReturn;
        } else
            return 0;
    }

    public void revolt() {
        setCurrentCiv(6);
        for (Point neighbor : neighbors) {
            neighbor.setCurrentCiv(6);
        }
    }

    public List<Point> getNeighbors() {
        List<Point> result = new ArrayList<>();
        for (Point neighbor : neighbors) {
            result.add(neighbor);
        }
        return result;
    }

}
