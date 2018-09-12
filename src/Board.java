import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {

    Logger log = Logger.getGlobal();

    private static final long serialVersionUID = 1L;

    private static final double globalHabitability = 0.8;

    public static final int MAX_CIV_NUMBER = 5;
    private static int CIV_COUNTER = 0;

    private static Point[][] points;

    private int size;
    private int iteration;

    private List<Civilization> civilizations = new ArrayList<>();

    public Board(int length, int height) {
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);

        iteration = 0;
        size = 14;

        System.out.println("Some things take time.... like initializing the Board");
        initialize(length, height);
        System.out.println("Some things take time.... but we're finished here...");
    }

    // single iteration
    public void iterate() {
//        this.repaint();
        iteration++;


        // Powiedzmy, że symulacja jest asynchroniczna dla cywilizacji
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].getCurrentCivId() == 0) {
                    for (Civilization civilization : civilizations)
                        civilization.initSpread(points[x][y]);
                } else {
                    for (Civilization civilization : civilizations)
                        civilization.initFight(points[x][y]);
                }
                for (Civilization civilization : civilizations)
                    civilization.checkIfSurrounded(points[x][y]);

                for (Civilization civilization : civilizations)
                    civilization.tryRevolt(points[x][y]);
            }
        }
        for (Civilization civilization : civilizations) {
            if(civilization.isExists() && civilization.getFields().isEmpty()) {
                civilizations.remove(civilization);
            }
        }

        /*for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y].changeState();*/
        logBoardState(50);

        this.repaint();

    }

    private void logBoardState(int iterationCount) {
        if (iteration % iterationCount == 0) {
            log.log(Level.INFO, "\n\nIteration #" + iteration);
            for (Civilization civ : civilizations) {
//                System.out.println("CIV "+ civ.getId() +" Growth: " + civ.CalculateGrowthForce() + " Military: " + civ.CalculateMilitaryForce() + " Fields: " + civ.getNumberOfFields());
                log.log(Level.INFO, "\n\nCIV " + civ.getId() +
                        "\nGlobal Growth: " + civ.CalculateGrowthForce() +
                        "\nGlobal Military: " + civ.CalculateMilitaryForce() +
                        "\nFields Count: " + civ.getNumberOfFields() +
                        "\nDoctrine: "+ (civ.getDoctrine() == 0 ? "Militaristic" : civ.getDoctrine()==2 ? "Economic" : "Balanced") + "\n");
            }
           /* System.out.println("BLUE Growth: " + civilizations.get(0).CalculateGrowthForce() + " Military: " + civilizations.get(0).CalculateMilitaryForce() + " Fields: " + civilizations.get(0).getNumberOfFields());
            System.out.println("GREEN Growth: " + civilizations.get(1).CalculateGrowthForce() + " Military: " + civilizations.get(1).CalculateMilitaryForce() + " Fields: " + civilizations.get(1).getNumberOfFields());
            System.out.println("RED Growth: " + civilizations.get(2).CalculateGrowthForce() + " Military: " + civilizations.get(2).CalculateMilitaryForce() + " Fields: " + civilizations.get(2).getNumberOfFields());
            System.out.println("YELLOW Growth: " + civilizations.get(3).CalculateGrowthForce() + " Military: " + civilizations.get(3).CalculateMilitaryForce() + " Fields: " + civilizations.get(3).getNumberOfFields());
            System.out.println("PINK Growth: " + civilizations.get(4).CalculateGrowthForce() + " Military: " + civilizations.get(4).CalculateMilitaryForce() + " Fields: " + civilizations.get(4).getNumberOfFields());
            System.out.println("REBEL Growth: " + civilizations.get(5).CalculateGrowthForce() + " Military: " + civilizations.get(5).CalculateMilitaryForce() + " Fields: " + civilizations.get(5).getNumberOfFields());
            System.out.println(" ");*/
        }
    }

    private void logBoardState(int x, int y) {
        log.log(Level.INFO,
                "\n\nCIV " + points[x][y].getCurrentCivId() +
                      "\nField (x : y) : (" + x + " : " + y +") " +
                      "\nLocal Growth: " + points[x][y].getLocalGrowthForce() +
                      "\nLocal Military: " + points[x][y].getLocalMilitaryForce() +
                      "\nState: " + points[x][y].getState() +
                      "\nHabitable: " + points[x][y].isHabitable() +
                      "\n"
        );
    }

    public void createCiv(int x, int y) {

        Civilization civilization = new Civilization(CIV_COUNTER+1, this, points[x][y]);
        points[x][y].setState(1, CIV_COUNTER +1);
        points[x][y].setLocalGrowthForce(5);
        points[x][y].setLocalMilitaryForce(5);
        civilizations.add(civilization);

        CIV_COUNTER++;
        if (CIV_COUNTER == MAX_CIV_NUMBER) {
            Civilization rebels = new Civilization(Integer.MAX_VALUE, this);
            civilizations.add(rebels);
        }
    }


    public Civilization getCiv(int id) {
        for (Civilization c : civilizations) {
            if (c.getId() == id)
                return c;
        }
        return null;
    }

    // clearing board
    public void clear() {
        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].setState(0, 0);
            }
        }
        for (Civilization civilization : civilizations) {
            civilization.setGlobalGrowthForce(0);
            civilization.setGlobalMilitaryForce(0);
        }
        civilizations.clear();
        CIV_COUNTER = 0;
        iteration = 0;
    }

    private void initialize(int length, int height) {
        String methodName = "initialize";
        System.out.println("Entering "+ methodName);

        createPointsOnBoard (length / size, height / size);
        createNeighbourhoods();
        generateCivilizations();

        System.out.println("Exiting "+ methodName);
    }

    private void createNeighbourhoods() {
        String methodName = "createNeighbourhoods";
        System.out.println("Entering "+ methodName);

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                createNeighbourhoodOfPoint(x,y);
            }
        }
        System.out.println("Exiting "+ methodName);
    }

    private void createNeighbourhoodOfPoint(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ( !(i == 0 && j == 0) && isOnBoardAndHabitable(x+i,y+j))
                    points[x][y].addNeighbour(points[x + i][y + j]);
            }
        }
    }

    private boolean isOnBoardAndHabitable(int x, int y) {
        return x > 0 && x < points.length && y > 0 && y < points[x].length && points[x][y].isHabitable();
    }

    private void createPointsOnBoard(int length, int height) {
        String methodName = "createPointsOnBoard";
        System.out.println("Entering "+ methodName);

        points = new Point[length][height];

//        System.out.println("points length : " + points.length);
//        System.out.println("points height : " + points[points.length-1].length);

        for (int x = 0; x < points.length; ++x) {

            for (int y = 0; y < points[x].length; ++y) {
                points[x][y] = new Point();

                double notHabitableRandomnessIndex = new Random().nextDouble();

                if (notHabitableRandomnessIndex > globalHabitability) {
                    points[x][y].setHabitable(false);
                    points[x][y].setCurrentCivId(Integer.MAX_VALUE);
                    points[x][y].setState(1, 0);
                } else {
//                    log.info("Point " + x +":"+ y + " - " + "is habitable");
                    points[x][y].setHabitable(true);
                    points[x][y].setCurrentCivId(0);
                    points[x][y].setState(0, 0);
                }
            }
        }
        System.out.println("Exiting "+ methodName);
    }

    private void generateCivilizations() {
        String methodName = "generateCivilizations";
        System.out.println("Entering "+ methodName);

        Random generator = new Random();
        while (CIV_COUNTER < MAX_CIV_NUMBER) {
            int randX = generator.nextInt(points.length);
            int randY = generator.nextInt(points[points.length-1].length);
            if (isOnBoardAndHabitable(randX, randY) && points[randX][randY].getCurrentCivId() == 0) {
                createCiv(randX, randY);
                System.out.println("(randX : randY) - ("+randX+" : "+randY+") state : "+points[randX][randY].getState()+" civId : "+points[randX][randY].getCurrentCivId());
            }
        }
        System.out.println("Exiting "+ methodName);
    }

    public Point getPoint(int x, int y) {
        return points[x][y];
    }



    //paint background and separators between cells
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.DARK_GRAY);
        drawNetting(g, size);
    }

    // draws the background netting
    private void drawNetting(Graphics g, int gridSpace) {
        Insets insets = getInsets();
        int firstX = insets.left;
        int firstY = insets.top;
        int lastX = this.getWidth() - insets.right;
        int lastY = this.getHeight() - insets.bottom;

        int x = firstX;
        while (x < lastX) {
            g.drawLine(x, firstY, x, lastY);
            x += gridSpace;
        }

        int y = firstY;
        while (y < lastY) {
            g.drawLine(firstX, y, lastX, y);
            y += gridSpace;
        }


        for (x = 0; x < points.length; ++x) {
            for (y = 0; y < points[x].length; ++y) {
//                    System.out.println("drawNetting --- " +x+" : "+y+" state : "+points[x][y].getState());
                if (points[x][y].getState() != 0) {
                    if (points[x][y].isHabitable()) {
                        /*String coords = "(x : y) - ("+x+" : "+y+") ";
                        String state = "state : "+points[x][y].getState()+" ";
                        String civId = "civId : "+points[x][y].getCurrentCivId()+" ";
                        String habitability = "habitable : "+points[x][y].isHabitable()+" H-H";
                        System.out.println(coords+civId+state+habitability);*/
                        switch (points[x][y].getCurrentCivId()) {

                            case 1:
//                                System.out.println("drawNetting --- civId : " + points[x][y].getCurrentCivId());
                                g.setColor(new Color(0x0000ff));
//                             System.out.println("drawNetting --- color : " + g.getColor().toString());
                                break;
                            case 2:
//                              System.out.println("drawNetting --- civId : " + points[x][y].getCurrentCivId());
                                g.setColor(new Color(0x00ff00));
//                              System.out.println("drawNetting --- color : " + g.getColor().toString());
                                break;
                            case 3:
//                                System.out.println("drawNetting --- civId : " + points[x][y].getCurrentCivId());
                                g.setColor(new Color(0xff0000));
//                                System.out.println("drawNetting --- color : " + g.getColor().toString());
                                break;
                            case 4:
//                                System.out.println("drawNetting --- civId : " + points[x][y].getCurrentCivId());
                                g.setColor(new Color(0xf4f442));
//                                System.out.println("drawNetting --- color : " + g.getColor().toString());
                                break;
                            case 5:
//                                System.out.println("drawNetting --- civId : " + points[x][y].getCurrentCivId());
                                g.setColor(new Color(0xf441d3));
//                                System.out.println("drawNetting --- color : " + g.getColor().toString());
                                break;
                            case Integer.MAX_VALUE:
//                                System.out.println("drawNetting --- civId : " + points[x][y].getCurrentCivId());
                                g.setColor(new Color(0x000000));
//                              System.out.println("drawNetting --- color : " + g.getColor().toString());
                                break;
                        }
                    } else if (!points[x][y].isHabitable()){
                        g.setColor(new Color(85,85,85));
                        /*String coords = "(x : y) - ("+x+" : "+y+") ";
                        String state = "state : "+points[x][y].getState()+" ";
                        String civId = "civId : "+points[x][y].getCurrentCivId()+" ";
                        String habitability = "habitable : "+points[x][y].isHabitable()+" N-H";
                        System.out.println(coords+civId+state+habitability);*/
                    }

                    g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            //points[x][y].clicked();
            //this.repaint();
        }
        logBoardState(x, y);
    }

    public void componentResized(ComponentEvent e) {
        int dlugosc = this.getWidth();
        int wysokosc = this.getHeight();
        clear();
        initialize(dlugosc, wysokosc);
    }

    public void mouseDragged(MouseEvent e) {
        /*int x = e.getX() / size;
        int y = e.getY() / size;
        if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
            points[x][y].setState(1);
            this.repaint();
        }*/
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }
}
