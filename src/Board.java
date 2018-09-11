import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Board with Points that may be expanded (with automatic change of cell
 * number) with mouse event listener
 */

public class Board extends JComponent implements MouseInputListener, ComponentListener {
    private static final long serialVersionUID = 1L;
    private static Point[][] points;
    private int size = 14;
    private int civCount = 0;
    private int iteration = 0;
    private List<Civilization> civilizations = new ArrayList<Civilization>();

    public Board(int length, int height) {
        addMouseListener(this);
        addComponentListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    // single iteration
    public void iteration() {
        iteration++;
        Random generator = new Random();

        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                if (points[x][y].getCurrentCiv() == 0) {
                    for (Civilization civilization : civilizations)
                        civilization.spread(points[x][y]);
                } else
                    for (Civilization civilization : civilizations)
                        civilization.fight(points[x][y]);

                for (Civilization civilization : civilizations)
                    civilization.checkIfSurrounded(points[x][y]);

                for (Civilization civilization : civilizations)
                    civilization.tryRevolt(points[x][y]);
            }

        /*for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y].changeState();*/

        for (int x = 5; x < points.length - 5; ++x)
            for (int y = 5; y < points[x].length - 5; ++y)
                if (generator.nextInt(10000) > 9998 && civCount < 5 && points[x][y].getState() == 0) {
                    createCiv(x, y);
                }


        if (iteration % 50 == 0) {
            System.out.println("BLUE Growth: " + civilizations.get(0).CalculateGrowthForce() + " Military: " + civilizations.get(0).CalculateMilitaryForce() + " Fields: " + civilizations.get(0).getNumberOfFields());
            System.out.println("GREEN Growth: " + civilizations.get(1).CalculateGrowthForce() + " Military: " + civilizations.get(1).CalculateMilitaryForce() + " Fields: " + civilizations.get(1).getNumberOfFields());
            System.out.println("RED Growth: " + civilizations.get(2).CalculateGrowthForce() + " Military: " + civilizations.get(2).CalculateMilitaryForce() + " Fields: " + civilizations.get(2).getNumberOfFields());
            System.out.println("YELLOW Growth: " + civilizations.get(3).CalculateGrowthForce() + " Military: " + civilizations.get(3).CalculateMilitaryForce() + " Fields: " + civilizations.get(3).getNumberOfFields());
            System.out.println("PINK Growth: " + civilizations.get(4).CalculateGrowthForce() + " Military: " + civilizations.get(4).CalculateMilitaryForce() + " Fields: " + civilizations.get(4).getNumberOfFields());
            System.out.println("REBEL Growth: " + civilizations.get(5).CalculateGrowthForce() + " Military: " + civilizations.get(5).CalculateMilitaryForce() + " Fields: " + civilizations.get(5).getNumberOfFields());
            System.out.println(" ");
        }
        this.repaint();

    }

    public void createCiv(int x, int y) {
        civCount++;
        Civilization civilization = new Civilization(civCount, this);
        points[x][y].setState(1, civCount);
        points[x][y].setGrowthValue(5);
        points[x][y].setMilitaryValue(5);
        civilization.addField(points[x][y]);
        civilizations.add(civilization);
        if (civCount == 5) {
            Civilization rebels = new Civilization(6, this);
            civilizations.add(rebels);
        }
    }


    public Civilization getCiv(int id) {
        return civilizations.get(id - 1);
    }

    // clearing board
    public void clear() {
        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y) {
                points[x][y].setState(0, 0);
            }
        this.repaint();
        for (Civilization civilization : civilizations) {
            civilization.setGrowthForce(0);
            civilization.setMilitaryForce(0);
        }
        civilizations.clear();
        civCount = 0;
    }

    private void initialize(int length, int height) {
        points = new Point[length][height];

        for (int x = 0; x < points.length; ++x)
            for (int y = 0; y < points[x].length; ++y)
                points[x][y] = new Point();

        for (int x = 0; x < points.length; ++x) {
            for (int y = 0; y < points[x].length; ++y) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if ((x + i > 0 && x + i < points.length - 1) && (y + j > 0 && y + j < points[x].length - 1) && !(i == 0 && j == 0))
                            points[x][y].addNeighbor(points[x + i][y + j]);
                    }
                }
            }
        }
    }

    //paint background and separators between cells
    protected void paintComponent(Graphics g) {
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        g.setColor(Color.GRAY);
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
                if (points[x][y].getState() != 0) {
                    switch (points[x][y].getCurrentCiv()) {
                        case 1:
                            g.setColor(new Color(0x0000ff));
                            break;
                        case 2:
                            g.setColor(new Color(0x00ff00));
                            break;
                        case 3:
                            g.setColor(new Color(0xff0000));
                            break;
                        case 4:
                            g.setColor(new Color(0xf4f442));
                            break;
                        case 5:
                            g.setColor(new Color(0xf441d3));
                            break;
                        case 6:
                            g.setColor(new Color(0x000000));
                            break;
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
            points[x][y].clicked();
            this.repaint();
        }
    }

    public void componentResized(ComponentEvent e) {
        int dlugosc = (this.getWidth() / size) + 1;
        int wysokosc = (this.getHeight() / size) + 1;
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

    public Point getPoint(int x, int y) {
        return points[x][y];
    }
}
