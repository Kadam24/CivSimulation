import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Civilization {
    private int id;
    private int growthForce = 0;
    private int militaryForce = 0;
    private List<Point> fields = new ArrayList<Point>();
    private Board board;

    public Civilization(int civCount, Board board) {
        id = civCount;
        this.board = board;
    }

    public int CalculateMilitaryForce() {
        militaryForce = 0;
        for (Point field : fields) {
            militaryForce = militaryForce + field.getMilitaryValue();
        }
        return militaryForce;
    }

    public int CalculateGrowthForce() {
        growthForce = 0;
        for (Point field : fields) {
            growthForce = growthForce + field.getGrowthValue();
        }
        return growthForce;
    }

    public void addField(Point point) {
        fields.add(point);
    }

    public void removeField(Point point) {
        fields.remove(point);
    }


    // Rozrastanie się cywilizacji za pomocą growthForce ( zajmowanie pustych pól )
    public void spread(Point point) {
        List<Integer> neighbors = point.neighborTakenBy();
        int nearbyAllied = 0;
        for (Integer neighbor : neighbors) {
            if (neighbor == id)
                nearbyAllied += 1;
        }
        for (Integer neighbor : neighbors) {
            if (neighbor == id) {
                Random generator = new Random();
                if (generator.nextInt(10000) + 10 * nearbyAllied + 0.001 * CalculateGrowthForce() > 9980) {
                    point.setState(1, id);
                    addField(point);
                }
            }
        }
    }


    // Rozrastanie się cywilizacji za pomocą MilitaryForce ( przejmowanie zajętych pól )
    public void fight(Point point) {

        if (point.getCurrentCiv() == id) {
            return;
        }
        List<Integer> neighbors = point.neighborTakenBy();
        int nearbyAllied = 0;
        int nearbyEnemy = 0;
        int enemyId = 0;


        for (Integer neighbor : neighbors) {
            if (neighbor == id) {
                nearbyAllied = +1;
            } else if (neighbor != 0) {
                nearbyEnemy = +1;
                enemyId = neighbor;
            }
        }

        if (nearbyAllied > 0) {
            Random generator = new Random();
            if (enemyId == 0) {
                /*if (generator.nextInt(10000) + 0.001 * CalculateMilitaryForce() + 100 * nearbyAllied > 9980) {
                    point.setState(1, id);
                    addField(point);
                    board.getCiv(enemyId).removeField(point);
                }*/
            } else {
                if (generator.nextInt(10000) + 3000 * nearbyAllied + 0.1 * CalculateMilitaryForce() - 3000 * nearbyEnemy - 0.1 * board.getCiv(enemyId).CalculateMilitaryForce() > 9980) {
                    point.setState(1, id);
                    addField(point);
                    board.getCiv(enemyId).removeField(point);
                }
            }
        }
    }

    //Jeśli kratka jest otoczona kratkami tego samego koloru to również staje się tego samego koloru
    public void checkIfSurrounded(Point point) {
        int tmp = point.checkIfSurrounded();
        if (tmp != 0 && tmp != id) {
            point.setState(1, tmp);
            removeField(point);
            board.getCiv(tmp).addField(point);
        }
    }


    //Niskie prawdopodobieństwo na powstanie buntu ( punkt oraz wszyscy Jego sąsiedzi dołączają do frakcji 6 - buntowników )
    public void tryRevolt(Point point) {
        Random generator = new Random();
        if (getGrowthForce() > 200 && getGrowthForce() > getMilitaryForce() && point.getCurrentCiv() != 0) {
            if (generator.nextInt(50000) > 49998) {
                point.revolt();

                for (Point neighbor : point.getNeighbors()) {
                    removeField(neighbor);
                    board.getCiv(6).addField(neighbor);
                }
                removeField(point);
                board.getCiv(6).addField(point);
            }
        }
    }

    public int getNumberOfFields() {
        return fields.size();
    }

    public int getGrowthForce() {
        return growthForce;
    }

    public void setGrowthForce(int growthForce) {
        this.growthForce = growthForce;
    }

    public int getMilitaryForce() {
        return militaryForce;
    }

    public void setMilitaryForce(int militaryForce) {
        this.militaryForce = militaryForce;
    }
}
