import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Civilization {

    private static final double BALANCE_SCALE = 1.1;

    private int id;
    private int globalGrowthForce;
    private int globalMilitaryForce;
    private int globalScienceForce;
    private List<Point> fields;
    private Board board;
    private boolean exists;
    private String color;
    private int doctrine;
    private int numberOfCities;
    private boolean isRebel = false;


    public Civilization(int civId, Board board, Point startingPoint) {
        init(civId, board);
        fields.add(startingPoint);
    }

    public Civilization(int civId, Board board) {
        init(civId, board);
    }

    private void init(int civId, Board board) {
        globalGrowthForce = 0;
        numberOfCities = 0;
        globalMilitaryForce = 0;
        globalScienceForce = 0;
        exists = false;
        id = civId;
        this.board = board;
        fields = new ArrayList<>();

        if (id == Integer.MAX_VALUE) {
            isRebel = true;
        }

        //doktryna rozwoju - wskazuje na współczynnik maksymalizowany : 0 - militaryForce ; 1 - balanced ; 2 -growthForce
        doctrine = new Random().nextInt(3);
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

    public int CalculateScienceForce() {
        globalScienceForce = 0;
        for (Point field : fields) {
            globalScienceForce = globalScienceForce + field.getLocalScienceForce();
        }
        return globalScienceForce;
    }

    public void addField(Point point) {
        if (point.isHabitable() && !(point.getCurrentCivId() == id))
            fields.add(point);
    }

    public void removeField(Point point) {
        if (fields.contains(point))
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
                if ((generator.nextInt(20000) + 100 * nearbyAllied + 0.001 * getGlobalGrowthForce() + 0.001 * getGlobalScienceForce()) > 19980) {
                    if (!isRebel) {
                        //addField(point);
                        board.getCiv(id).addField(point);
                        for (int i = 1; i < Board.MAX_CIV_NUMBER + 1; i++) {
                            if (i != id)
                                board.getCiv(i).removeField(point);
                        }
                        point.setState(1, id);
                    }
                }
            }
        }

    }

    public void initSpread(Point point) {
        if (!isRebel) {
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
                /*if (generator.nextInt(10000) + 0.001 * GetGlobalMilitaryForce() + 100 * nearbyAllied > 9980) {
                    point.setState(1, id);
                    addField(point);
                    board.getCiv(enemyId).removeField(point);
                }*/
            } else {
                /*if( == Integer.MAX_VALUE){
                    if (generator.nextInt(10000) + 5000 * nearbyAllied + 0.3 * getGlobalMilitaryForce() - 5000 * nearbyEnemy - 0.1 * board.getCiv(enemyId).getGlobalMilitaryForce() > 9980) {
                        if()
                        point.setState(1, id);
                        addField(point);
                        board.getCiv(id).addField(point);
                        board.getCiv(enemyId).removeField(point);
                    }*/
                if (generator.nextInt(10000) + 5000 * nearbyAllied + 0.001 * getGlobalMilitaryForce() + 0.001 * getGlobalScienceForce()
                        - 5000 * nearbyEnemy - 0.1 * board.getCiv(enemyId).getGlobalMilitaryForce() - 0.1 * board.getCiv(enemyId).getGlobalScienceForce() > 9980) {
                    if (!isRebel) {
                        board.getCiv(id).addField(point);
                        board.getCiv(enemyId).removeField(point);
                        point.setState(1, id);
                        //addField(point);
                    }
                }
            }
        }
    }

    public void initFight(Point point) {
        if (!isRebel) {
            List<Point> neighbourPointList = point.getNeighbours();
            List<Point> notOccupiedPoints = getNotOccupiedPointsInNeighbourhood(neighbourPointList);
            if (!notOccupiedPoints.isEmpty()) {
                if (doctrine == 0) {
                    notOccupiedPoints.sort(Comparator.comparing(Point::getLocalMilitaryForce).reversed());
                    if (
                            point.getLocalMilitaryForce() > globalMilitaryForce / (getNumberOfFields() != 0 ? getNumberOfFields() : 1) ||
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
        if (tmp != 0 && tmp != id && !(isRebel)) {
            point.setState(1, tmp);
            removeField(point);
            board.getCiv(tmp).addField(point);
        }
    }


    //Niskie prawdopodobieństwo na powstanie buntu ( punkt oraz wszyscy Jego sąsiedzi dołączają do frakcji Integer.MAX_VALUE - buntowników )
    public void tryRevolt(Point point) {
        Random generator = new Random();
        for (Point neighbor : point.getNeighbours()) {
            if (neighbor.getCurrentCivId() > 100)
                return;
        }
        if (getGlobalGrowthForce() > 200 && getGlobalGrowthForce() > getGlobalMilitaryForce() && point.getCurrentCivId() != 0) {
            if (generator.nextInt(50000) > 49998) {
                point.revolt();

                for (Point neighbour : point.getNeighbours()) {
                    removeField(neighbour);
                    //board.getCiv(Integer.MAX_VALUE).addField(neighbour);
                }
                removeField(point);
                // board.getCiv(Integer.MAX_VALUE).addField(point);
            }
        }
    }

    public void createCity() {
        if (numberOfCities == 0 && fields.size() > 0) {
            Random generator = new Random();
            int numberOfCityField = generator.nextInt(fields.size());
            Point cityField = fields.get(numberOfCityField);
            numberOfCities++;
        } else if (numberOfCities != 0) {
            if (globalGrowthForce / numberOfCities > 100) {
                Random generator = new Random();
                int numberOfCityField = generator.nextInt(fields.size());
                Point cityField = fields.get(numberOfCityField);
                if (!cityField.isCity()) {
                    cityField.createCity();
                    numberOfCities++;
                }
            }
        }

    }

    public void setRebel() {
        this.isRebel = true;
    }

    public boolean getRebel() {
        return isRebel;
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

    public int getGlobalScienceForce() {
        return globalScienceForce;
    }

    public void setGlobalScienceForce(int globalScienceForce) {
        this.globalScienceForce = globalScienceForce;
    }

    public List<Point> getFields() {
        return fields;
    }

    public String getColor() {
        return color;
    }

    public int getDoctrine() {
        return doctrine;
    }
}