package sa;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimulatedAnnealing
{

    public static long seed = System.nanoTime();
    public static int cityCount = 40;
    public static double coolingRate = 0.00000005;
    public static double startTemp = 100000;
    public static Thread currT;
    public static CityPlanner curr;
    public static TourGui tourGui;

    public static void main(String[] args)
    {
        curr = new CityPlanner(startTemp, coolingRate, cityCount, seed);
        tourGui = new TourGui();
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {

                //EjE tourGui.draw.init();
                tourGui.setVisible(true);

                // EjE: Run runAnneal on background thread
                currT = new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        curr.runAnneal(tourGui);
                    }
                });
                currT.start();
                //SimulatedAnnealing.runAnneal(tourGui);
            }
        });
    }

    public static CityPlanner getCurr()
    {
        return curr;
    }

    public static void newCityPlanner(double startTemp, double coolingRate, int cityCount, long seed)
    {
        curr.terminate();
        curr = new CityPlanner(startTemp, coolingRate, cityCount, seed);
        try
        {
            currT.join();
        } catch (InterruptedException ex)
        {
            Logger.getLogger(SimulatedAnnealing.class.getName()).log(Level.SEVERE, null, ex);
        }
        currT = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                curr.runAnneal(tourGui);
            }
        });
        currT.start();
    }
}
