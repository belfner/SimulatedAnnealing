/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa;

/**
 *
 * @author benel
 */
import java.awt.*;       // Using AWT's Graphics and Color
import java.awt.event.*; // Using AWT event classes and listener interfaces
import javax.swing.*;    // Using Swing's components and containers
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Custom Drawing Code Template
 */
// A Swing application extends javax.swing.JFrame
//public class Draw extends JFrame {
//    // Define constants
//
//    // Declare an instance of the drawing canvas,
//    // which is an inner class called DrawCanvas extending javax.swing.JPanel.
//    private DrawCanvas canvas;
// 
//    // Constructor to set up the GUI components and event handlers
//    public Draw() {
//    }
//    public void init(){
//        canvas = new DrawCanvas();    // Construct the drawing canvas
//        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
// 
//        // Set the Drawing JPanel as the JFrame's content-pane
//        Container cp = getContentPane();
//        cp.add(canvas);
//        // or "setContentPane(canvas);"
// 
//        setDefaultCloseOperation(EXIT_ON_CLOSE);   // Handle the CLOSE button
//        pack();              // Either pack() the components; or setSize()
//        setTitle("Simulated Annealing");  // "super" JFrame sets the title
//        setVisible(true);    // "super" JFrame show
//    }
/**
 * Define inner class DrawCanvas, which is a JPanel used for custom drawing.
 */
public class Draw extends JPanel
{

    public int temp;
    public double cycles;
    public static final int CANVAS_WIDTH = 900;
    public static final int CANVAS_HEIGHT = 500;

    // Override paintComponent to perform your own painting
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);     // paint parent's background
        setBackground(Color.WHITE);  // set background color for this JPanel

        if (SimulatedAnnealing.curr == null)
        {
            g.setFont(new Font("Monospaced", Font.PLAIN, 20));
            g.drawString("Not Initialized", 30, 30);
            return;
        }

        int range = Math.min(getWidth(), getHeight());
        int radius = 5 * range / 600;
        drawBox(range, g);
        drawCities(radius, range, g);
        drawTour(range, g);
        drawText(range, g);
    }

    public void drawBox(int range, Graphics g)
    {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, range, range);
        g.setColor(Color.WHITE);
        g.fillRect(5, 5, range - 10, range - 10);

    }

    public void drawCities(int radius, int range, Graphics g)
    {
        City c;
        g.setColor(Color.BLACK);
        for (int i = 0; i < TourManager.numberOfCities(); i++)
        {
            c = TourManager.getCity(i);
            g.fillOval((c.getX()) * range / 222 + 5 - radius, (c.getY()) * range / 222 + 5 - radius, radius * 2, radius * 2);
        }
    }

    public void drawTour(int range, Graphics g)
    {
        Tour hold;
        if (SimulatedAnnealing.tourGui.drawBest)
        {
            hold = SimulatedAnnealing.curr.best;
        } else
        {
            hold = SimulatedAnnealing.curr.currentSolution;
        }
        g.setColor(Color.BLACK);
        City c;
        int lastX = 0;
        int lastY = 0;
        for (int i = 0; i < hold.tourSize(); i++)
        {
            c = hold.getCity(i);
            if (i != 0)
            {
                g.drawLine((c.getX()) * range / 222 + 5, (c.getY()) * range / 222 + 5, lastX, lastY);
                lastX = (c.getX()) * range / 222 + 5;
                lastY = (c.getY()) * range / 222 + 5;
                g.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g.drawString(String.valueOf(i + 1), (c.getX()) * range / 222 - 2, (c.getY()) * range / 222 - 2);
            } else
            {
                lastX = (c.getX()) * range / 222 + 5;
                lastY = (c.getY()) * range / 222 + 5;
                g.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g.drawString(String.valueOf(i + 1), (c.getX()) * range / 222 - 2, (c.getY()) * range / 222 - 2);
            }
        }
        c = hold.getCity(0);
        g.drawLine((c.getX()) * range / 222 + 5, (c.getY()) * range / 222 + 5, lastX, lastY);
    }

    public void drawText(int range, Graphics g)
    {
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Monospaced", Font.PLAIN, 20));
        
        
        g.drawString("Trial Seeed: " + SimulatedAnnealing.curr.seed, range, 30);
        
        g.drawString("Number of Cycles(M): " + df.format(cycles / 1000000), range, 70);
        g.drawString("Cycles Left(M): " + df.format(SimulatedAnnealing.curr.cyclesLeft / 1000000), range, 90);
        
        g.drawString("Seconds Elapsed: " + df.format(SimulatedAnnealing.curr.getTime()),range,130);
        g.drawString("Seconds Left: " + df.format(SimulatedAnnealing.curr.secondsLeft), range, 150);
        
        g.drawString("Current Tempurature: " + temp, range, 190);
        g.drawString("Initial solution distance: " + SimulatedAnnealing.curr.initialDistance,range,210);
        g.drawString("Current Distance: " + SimulatedAnnealing.curr.currentSolution.getDistance(),range,230);
        g.drawString("Shortest Distance: " + SimulatedAnnealing.curr.best.getDistance(), range, 250);
        
        g.drawString("Number of Cities: " + SimulatedAnnealing.curr.cityCount, range, 290);
        g.drawString("Start Temperature: " + SimulatedAnnealing.curr.startTemp, range, 310);
        g.drawString("Cooling Rate: " + SimulatedAnnealing.curr.coolingRate, range, 330);
    }
}
