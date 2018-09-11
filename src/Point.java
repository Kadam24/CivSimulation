import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Point {
    private ArrayList<Point> neighbourList;
    private int currentState;
    private int nextState;
    private int numStates = 6;
    private int localGrowthForce;
    private int localMilitaryForce;
    private int currentCivId;

    private boolean habitable;

    public Point() {
        Random generator = new Random();
        int tmp = generator.nextInt(4);
        currentCivId = 0;
        localGrowthForce = 4 - tmp;
        localMilitaryForce = tmp;
        currentState = 0;
        nextState = 0;
        neighbourList = new ArrayList<Point>();
    }

    public void clicked() {
        currentState = (++currentState) % numStates;
    }

    public int getState() {
        return currentState;
    }

    public void setState(int s, int civ) {
        currentState = s;
        currentCivId = civ;
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

    public void addNeighbour(Point nei) {
        neighbourList.add(nei);
    }

    private int countNeighbours() {
        int count = 0;
        for (Point n : neighbourList) {
            if (n.currentState == 1)
                count += 1;
        }
        return count;
    }

    public int getLocalGrowthForce() {
        return localGrowthForce;
    }

    public void setLocalGrowthForce(int localGrowthForce) {
        this.localGrowthForce = localGrowthForce;
    }

    public int getLocalMilitaryForce() {
        return localMilitaryForce;
    }

    public void setLocalMilitaryForce(int localMilitaryForce) {
        this.localMilitaryForce = localMilitaryForce;
    }

    public int getCurrentCivId() {
        return currentCivId;
    }

    public void setCurrentCivId(int currentCivId) {
        this.currentCivId = currentCivId;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public boolean isHabitable() {
        return habitable;
    }

    public void setHabitable(boolean habitable) {
        this.habitable = habitable;
    }

    public List<Integer> neighbourTakenBy() {
        List<Integer> civs = new ArrayList<Integer>();
        for (Point neighbour : neighbourList) {
            if (neighbour.getCurrentCivId() != 0) {
                civs.add(neighbour.getCurrentCivId());
            }
        }
        return civs;
    }

    public int checkIfSurrounded() {
        int hostileNeighbours = 0;
        int idToReturn = 0;
        for (Point neighbour : neighbourList) {
            if (neighbour.getCurrentCivId() != currentCivId && neighbour.getCurrentCivId() != 0) {
                hostileNeighbours++;
                idToReturn = neighbour.currentCivId;
            }
        }
        if (hostileNeighbours > 7) {
            return idToReturn;
        } else
            return 0;
    }

    public void revolt() {
        setCurrentCivId(Integer.MAX_VALUE);
        for (Point neighbour : neighbourList) {
            neighbour.setCurrentCivId(Integer.MAX_VALUE);
        }
    }

    public List<Point> getNeighbours() {
        List<Point> result = new ArrayList<>();
        for (Point neighbour : neighbourList) {
            result.add(neighbour);
        }
        return result;
    }

}
