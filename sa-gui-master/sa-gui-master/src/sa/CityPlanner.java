/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author benel
 */
public class CityPlanner
{
    //******************************

    public long seed = 0;
    //******************************

    Random rnd;
    //rnd.setSeed(50);
    public static TourGui tourGui;
    public Tour currentSolution;
    public Tour best;
    
    public int initialDistance = 0;

    public boolean p = false;

    // Set initial temp
    public double temp;
    public double startTemp = 0.0;
    // Cooling rate
    public double coolingRate = 0.0;
    public double secondsLeft;
    public double cyclesLeft;

    public int minimum = 5;
    public int maximum = 200;
    public int cityCount = 0;

    public boolean running = true;
    double spmil = 0;
    double totTime = 0;
    double escStartTime;
    double startTime;
    double endTime;
    
    public double getTime()
    {
        double hold = System.nanoTime();
        totTime += ( hold - escStartTime);
        escStartTime = hold;
        return (totTime) / 1000000000;
    }

    public void terminate()
    {
        running = false;
    }

    public CityPlanner(double startTemp, double coolingRate, int cityCount, long seed)
    {
        this.startTemp = startTemp;
        this.coolingRate = coolingRate;
        this.cityCount = cityCount;
        this.seed = seed;
        rnd = new Random(seed);
    }

    // Calculate the acceptance probability
    public double acceptanceProbability(int energy, int newEnergy, double temperature)
    {
        // If the new solution is better, accept it
        if (newEnergy < energy)
        {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((energy - newEnergy) / temperature);
    }

    public void setupCities()
    {
        TourManager.resetCities();
        for (int i = 0; i < cityCount; i++)
        {
            City city = new City(minimum + (int) (rnd.nextDouble() * maximum), minimum + (int) (rnd.nextDouble() * maximum));
            TourManager.addCity(city);
        }
    }

    public void pause(boolean p)
    {
        this.p = p;
        if(p)
        {
            endTime = System.nanoTime();
            spmil += (endTime - startTime) / 1000000000;
            totTime += (endTime - escStartTime); 
        }
        else
        {
            startTime = System.nanoTime();
            escStartTime = startTime;
        }
    }

    public void runAnneal(final TourGui tourGui)
    {

        //while(true){
        // Create and add our cities
        setupCities();
        int cycles = 0;
        // Initialize intial solution
        currentSolution = new Tour(seed);
        currentSolution.generateIndividual();
        initialDistance = currentSolution.getDistance();
        System.out.println("Initial solution distance: " + initialDistance);
        temp = startTemp;
        // Set as current best
        best = new Tour(currentSolution.getTour(), seed);

        startTime = System.nanoTime();
        escStartTime = startTime;

        // Loop until system has cooled
        while (temp > 1 && running)
        {
            while (p)
            {

            }
            boolean bRepaint = false;

            if (cycles % 100000 == 0 && cycles != 0)
            {

                endTime = System.nanoTime();
                cyclesLeft = (-Math.log10(temp * 1.0000005)) / Math.log10(1 - coolingRate);

                spmil += (endTime - startTime) / 1000000000;
                
                startTime = endTime;
                secondsLeft = (spmil / cycles) * cyclesLeft;
                
                bRepaint = true;

            }
            // Create new neighbour tour
            Tour newSolution = new Tour(currentSolution.getTour(), seed);

            // Get a random positions in the tour
            int tourPos1 = (int) (newSolution.tourSize() * rnd.nextDouble());
            int tourPos2 = (int) (newSolution.tourSize() * rnd.nextDouble());

            // Get the cities at selected positions in the tour
            City citySwap1 = newSolution.getCity(tourPos1);
            City citySwap2 = newSolution.getCity(tourPos2);

            // Swap them
            newSolution.setCity(tourPos2, citySwap1);
            newSolution.setCity(tourPos1, citySwap2);

            // Get energy of solutions
            int currentEnergy = currentSolution.getDistance();
            int neighbourEnergy = newSolution.getDistance();

            // Decide if we should accept the neighbour
            if (acceptanceProbability(currentEnergy, neighbourEnergy, temp) > rnd.nextDouble())
            {
                currentSolution = new Tour(newSolution.getTour(), seed);
            }

            // Keep track of the best solution found
            if (currentSolution.getDistance() < best.getDistance())
            {
                best = new Tour(currentSolution.getTour(), seed);
                // draw.bestDist = best.getDistance();
                //draw.repaint();
                bRepaint = true;
            }

            // Cool system
            temp *= 1 - coolingRate;
            tourGui.draw.temp = (int) temp;
            tourGui.draw.cycles = cycles;

            //EjE Must paint on main? thread (see below). tourGui.getContentPane().repaint();
            if (bRepaint)
            {
                try
                {
                    java.awt.EventQueue.invokeAndWait(new Runnable()
                    {
                        public void run()
                        {
                            tourGui.getContentPane().repaint();
                        }
                    });
                } catch (Exception ex)
                {
                    Logger.getLogger(SimulatedAnnealing.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            cycles++;
        }
        if (running)
        {
            secondsLeft = 0.0;
            cyclesLeft = 0.0;
            try
            {
                java.awt.EventQueue.invokeAndWait(new Runnable()
                {
                    public void run()
                    {
                        tourGui.getContentPane().repaint();
                    }
                });
            } catch (Exception ex)
            {
                Logger.getLogger(SimulatedAnnealing.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Final solution distance: " + best.getDistance());
            System.out.println("Tour: " + best);
        }
    }
}
