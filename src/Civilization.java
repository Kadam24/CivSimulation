import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Civilization {
    private int id;
    private int globalGrowthForce;
    private int globalMilitaryForce;
    private List<Point> fields;
    private Board board;
    private boolean exists;
    private String color;

    public Civilization(int civId, Board board) {
        globalGrowthForce = 0;
        globalGrowthForce = 0;
        globalMilitaryForce = 0;
        exists = true;
        id = civId;
        this.board = board;
        fields = new ArrayList<>();
    }

    public int CalculateMilitaryForce() {
        globalMilitaryForce = 0;
        for (Point field : fields) {
            globalMilitaryForce = globalMilitaryForce + field.getLocalMilitaryForce();
        }
        return globalMilitaryForce;
    }

    public int CalculateGrowthForce() {
        globalGrowthForce = 0;
        for (Point field : fields) {
            globalGrowthForce = globalGrowthForce + field.getLocalGrowthForce();
        }
        return globalGrowthForce;
    }

    public void addField(Point point) {
        fields.add(point);
    }

    public void removeField(Point point) {
        fields.remove(point);
    }


    // Rozrastanie się cywilizacji za pomocą growthForce ( zajmowanie pustych pól )
    public void spread(Point point) {
        if (point.isHabitable()) {
            List<Integer> neighbors = point.neighbourTakenBy();
            int nearbyAllied = 0;
            for (Integer neighbor : neighbors) {
                if (neighbor == id)
                    nearbyAllied += 1;
            }
            for (Integer neighbour : neighbors) {
                if (neighbour == id) {
                    Random generator = new Random();
                    if ((generator.nextInt(10000) + 10 * nearbyAllied + 0.001 * CalculateGrowthForce()) > 9980) {
                        point.setState(1, id);
                        addField(point);
                    }
                }
            }
        }
    }


    // Rozrastanie się cywilizacji za pomocą MilitaryForce ( przejmowanie zajętych pól )
    public void fight(Point point) {

        if (point.getCurrentCivId() == id) {
            return;
        }
        List<Integer> neighbors = point.neighbourTakenBy();
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
        if (getGlobalGrowthForce() > 200 && getGlobalGrowthForce() > getGlobalMilitaryForce() && point.getCurrentCivId() != 0) {
            if (generator.nextInt(50000) > 49998) {
                point.revolt();

                for (Point neighbour : point.getNeighbours()) {
                    removeField(neighbour);
                    board.getCiv(6).addField(neighbour);
                }
                removeField(point);
                board.getCiv(6).addField(point);
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getNumberOfFields() {
        return fields.size();
    }

    public int getGlobalGrowthForce() {
        return globalGrowthForce;
    }

    public void setGlobalGrowthForce(int growthForce) {
        this.globalGrowthForce = growthForce;
    }

    public int getGlobalMilitaryForce() {
        return globalMilitaryForce;
    }

    public void setGlobalMilitaryForce(int militaryForce) {
        this.globalMilitaryForce = militaryForce;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
