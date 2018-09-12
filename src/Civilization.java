import java.util.*;

public class Civilization {

    private static final double BALANCE_SCALE = 1.1;

    private int id;
    private int globalGrowthForce;
    private int globalMilitaryForce;
    private Set<Point> fields;
    private Board board;
    private boolean exists;
    private String color;
    private int doctrine;


    public Civilization(int civId, Board board, Point startingPoint) {
        init(civId, board);
        fields.add(startingPoint);
    }

    public Civilization(int civId, Board board) {
        init(civId, board);
    }

    private void init(int civId, Board board) {
        globalGrowthForce = 0;
        globalGrowthForce = 0;
        globalMilitaryForce = 0;
        exists = false;
        id = civId;
        this.board = board;
        fields = new HashSet<>();

        //doktryna rozwoju - wskazuje na współczynnik maksymalizowany : 0 - militaryForce ; 1 - balanced ; 2 -growthForce
        doctrine = new Random().nextInt(3) ;
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
        if (point.isHabitable())
            fields.add(point);
    }

    public void removeField(Point point) {
        fields.remove(point);
    }


    // Rozrastanie się cywilizacji za pomocą growthForce ( zajmowanie pustych pól )
    public void spread(Point point) {
        if (!point.isHabitable()) {
            return;
        }
        List<Integer> neighbours = point.neighbourTakenBy();

        int nearbyAllied = 0;
        for (Integer neighbour : neighbours) {
            if (neighbour == id)
                nearbyAllied += 1;
        }
        for (Integer neighbour : neighbours) {
            if (neighbour == id) {
                Random generator = new Random();
                if ((generator.nextInt(10000) + 10 * nearbyAllied + 0.001 * CalculateGrowthForce()) > 9980) {
                    point.setState(1, id);
                    addField(point);
                }
            }
        }

    }

    public void initSpread(Point point) {
        List<Point> neighbourPointList = point.getNeighbours();
        if (doctrine == 0) {
            neighbourPointList.sort(Comparator.comparing(Point::getLocalMilitaryForce).reversed());
            spreadTo(neighbourPointList, point);
        } else if (doctrine == 2) {
            neighbourPointList.sort(Comparator.comparing(Point::getLocalGrowthForce).reversed());
            spreadTo(neighbourPointList, point);
        } else {
            if (globalMilitaryForce > globalGrowthForce * BALANCE_SCALE) { // MilForce jest większa od GrForce o 10 %
                neighbourPointList.sort(Comparator.comparing(Point::getLocalGrowthForce).reversed());
                spreadTo(neighbourPointList, point);
            } else if (globalGrowthForce > globalMilitaryForce * BALANCE_SCALE) {
                neighbourPointList.sort(Comparator.comparing(Point::getLocalMilitaryForce).reversed());
                spreadTo(neighbourPointList, point);
            } else
                spread(point);
        }
    }

    private void spreadTo(List<Point> neighbourPointList, Point originalTarget) {
        for (Point target : neighbourPointList) {
            if (isInNeighbourhood(target)) {
                spread(target);
                return;
            }
        }
        spread(originalTarget);
    }

    private boolean isInNeighbourhood(Point point) {
        boolean isInNeighbourhood = false;
        List<Integer> neighbours = point.neighbourTakenBy();
        for (Integer neighbour : neighbours) {
            isInNeighbourhood = isInNeighbourhood || neighbour == id;
        }
        return isInNeighbourhood;
    }



    // Rozrastanie się cywilizacji za pomocą MilitaryForce ( przejmowanie zajętych pól )
    public void fight(Point point) {

        if (point.getCurrentCivId() == id || !point.isHabitable()) {
            return;
        }

        List<Integer> neighbours = point.neighbourTakenBy();
        int nearbyAllied = 0;
        int nearbyEnemy = 0;
        int enemyId = 0;


        for (Integer neighbour : neighbours) {
            if (neighbour == id) {
                nearbyAllied = +1;
            } else if (neighbour != 0) {
                nearbyEnemy = +1;
                enemyId = neighbour;
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

    public void initFight(Point point) {
        List<Point> neighbourPointList = point.getNeighbours();
        List<Point> notOccupiedPoints = getNotOccupiedPointsInNeighbourhood(neighbourPointList);
        if (!notOccupiedPoints.isEmpty()) {
            if (doctrine == 0) {
                notOccupiedPoints.sort(Comparator.comparing(Point::getLocalMilitaryForce).reversed());
                if (
                        point.getLocalMilitaryForce() > globalMilitaryForce/ (getNumberOfFields() != 0 ? getNumberOfFields() : 1) ||
                        notOccupiedPoints.get(0).getLocalMilitaryForce() > point.getLocalMilitaryForce()
                    ) {
                    initSpread(notOccupiedPoints.get(0));
                } else {
                    fight(point);
                }
            } else if (doctrine == 2) {
                neighbourPointList.sort(Comparator.comparing(Point::getLocalGrowthForce).reversed());
                initSpread(neighbourPointList.get(0));
            } else {
                if (globalMilitaryForce > globalGrowthForce * BALANCE_SCALE) {
                    notOccupiedPoints.sort(Comparator.comparing(Point::getLocalGrowthForce).reversed());
                    initSpread(notOccupiedPoints.get(0));
                } else if (globalGrowthForce > globalMilitaryForce * BALANCE_SCALE) {
                    notOccupiedPoints.sort(Comparator.comparing(Point::getLocalMilitaryForce).reversed());
                    initSpread(notOccupiedPoints.get(0));
                } else {
                    fight(point);
                }
            }
        } else {
            if (doctrine == 0) {
                neighbourPointList.sort(Comparator.comparing(Point::getLocalMilitaryForce).reversed());
                fightTo(neighbourPointList, point);
            } else if (doctrine == 2) {
                neighbourPointList.sort(Comparator.comparing(Point::getLocalGrowthForce).reversed());
                fightTo(neighbourPointList, point);
            } else {
                if (globalMilitaryForce > globalGrowthForce * BALANCE_SCALE) {
                    neighbourPointList.sort(Comparator.comparing(Point::getLocalGrowthForce).reversed());
                    fightTo(neighbourPointList, point);
                } else if (globalGrowthForce > globalMilitaryForce * BALANCE_SCALE) {
                    neighbourPointList.sort(Comparator.comparing(Point::getLocalMilitaryForce).reversed());
                    fightTo(neighbourPointList, point);
                } else {
                    fight(point);
                }
            }
        }
    }


    private List<Point> getNotOccupiedPointsInNeighbourhood(List<Point> neighbourPointList) {
        List<Point> notOccupiedPoints = new ArrayList<>();
        for (Point point : neighbourPointList) {
            if (point.getCurrentCivId() == 0) {
                notOccupiedPoints.add(point);
            }
        }
        return notOccupiedPoints;
    }

    private void fightTo(List<Point> neighbourPointList, Point originalTarget) {
        for (Point target : neighbourPointList) {
            if (isInNeighbourhood(target)) {
                fight(target);
                return;
            }
        }
        spread(originalTarget);
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


    //Niskie prawdopodobieństwo na powstanie buntu ( punkt oraz wszyscy Jego sąsiedzi dołączają do frakcji Integer.MAX_VALUE - buntowników )
    public void tryRevolt(Point point) {
        Random generator = new Random();
        if (getGlobalGrowthForce() > 200 && getGlobalGrowthForce() > getGlobalMilitaryForce() && point.getCurrentCivId() != 0) {
            if (generator.nextInt(50000) > 49998) {
                point.revolt();

                for (Point neighbour : point.getNeighbours()) {
                    removeField(neighbour);
                    board.getCiv(Integer.MAX_VALUE).addField(neighbour);
                }
                removeField(point);
                board.getCiv(Integer.MAX_VALUE).addField(point);
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

    public Set<Point> getFields() {
        return fields;
    }

    public String getColor() {
        return color;
    }

    public int getDoctrine() {
        return doctrine;
    }
}
