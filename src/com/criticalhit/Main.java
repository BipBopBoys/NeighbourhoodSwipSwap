package com.criticalhit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.*;
import java.util.List;

//Seattle Tupuhi 1286197
//Jesse Whitten 1311972
public class Main {

    private Solution currentSolution;
    private Solution globalBestSolution;
    private Solution previousSolution;
    private List<Box> poolOfAvailableBoxes = new ArrayList<>(); // This is where the destroy methods dump the removed boxes for the repair method.
    private int seed = 0;
    private int numBoxes = 0;
    private double[] destroyMethodWeights = new double[3]; // Contains the selection weight of each method.
    private double[] destroyMethodProbabilities = new double[3]; // Contains the selection weight of each method.
    private int lastUsedDestroyMethod = -1;
    private int iterationResult = -1;
    private Random rand = new Random();


    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println("Input is NeighbourhoodSwipSwap [filename] [SheetWidth]");
        else {
            System.out.println("Arguments Accepted: " + args[0] + " + " + args[1]);
            Main packingSolver = new Main();
            packingSolver.initialization(args[0], Integer.parseInt(args[1]));
            packingSolver.run();
        }
    }

    private void run() {
        for (int i = 0; i < 1000000; i++){
            chooseAndApplyDestroyMethod();
            //printLonelyBoxes();
            houseLonelyBoxes();
            newBest(currentSolution);
            updateDestroyWeights();
        }
    }

    private void updateDestroyWeights() {
        double gamma = 0.6; // Strength of adjustment.
        double GAMMA = -1;
        // Choose the reward values for each result.
        double w1 = 1.4; // Global.
        double w2 = 1.2; // Better than Prev.
        double w3 = 0.8; // Failed.

        // Get the GAMMA value.
        switch (iterationResult) {
            case 0: {
                GAMMA = w1;
            }
            case 1: {
                GAMMA = w2;
            }
            case 2: {
                GAMMA = w3;
            }
        }

        if (lastUsedDestroyMethod == -1) return; // Error!
        else if(lastUsedDestroyMethod == 0) { // Remove Box.
            destroyMethodWeights[0] = gamma*destroyMethodWeights[0] + (1-gamma)*GAMMA;
        } else if(lastUsedDestroyMethod == 1) { // Remove Neighbourhood.
            destroyMethodWeights[1] = gamma*destroyMethodWeights[1] + (1-gamma)*GAMMA;
        } else { // Swap Box.
            destroyMethodWeights[2] = gamma*destroyMethodWeights[2] + (1-gamma)*GAMMA;
        }
    }

    private void chooseAndApplyDestroyMethod() {
        double sumOfAllWeights = 0.00;

        // Get the sum of all weights.
        for(int i = 0; i < destroyMethodWeights.length; i++)
            sumOfAllWeights += destroyMethodWeights[i];

        // Find the probability that each one will be chosen.  All probs will add to 1.00.
        for(int i = 0; i < destroyMethodProbabilities.length; i++)
            destroyMethodProbabilities[i] = destroyMethodWeights[i]/sumOfAllWeights;

        double prediction = rand.nextDouble(); // Some double on range 0.00 to 1.00.

        if(prediction < destroyMethodProbabilities[0]) {
            destroyRemoveRandom();
            lastUsedDestroyMethod = 0;
        }else if(prediction < destroyMethodProbabilities[0] + destroyMethodProbabilities[1]) {
            destroyNeighbourhoodRandom();
            lastUsedDestroyMethod = 1;
        } else {
            destroySwapRandom();
            lastUsedDestroyMethod = 2;
        }
    }

    private void houseLonelyBoxes(){
        int i = 0;
        for (Box box: poolOfAvailableBoxes) {
            i++;
            currentSolution.repair(box);
        }
        //printLonelyBoxes();
        //System.out.println(i);
        poolOfAvailableBoxes.clear();
    }

    private void printLonelyBoxes(){
        for (Box box: poolOfAvailableBoxes) {
            System.out.print("("+box.getWidth()+","+box.getHeight()+"),");
        }
        System.out.print("\n");
    }
    private void initialization(String path, int width) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            String currentLine;
            String boxData[];
            int boxWidth, boxHeight;
            currentSolution = new Solution(width);
            while ((currentLine = reader.readLine()) != null) {
                boxData = currentLine.split(",");
                if (Integer.parseInt(boxData[0]) < Integer.parseInt(boxData[1])) {
                    boxWidth = Integer.parseInt(boxData[0]);
                    boxHeight = Integer.parseInt(boxData[1]);
                } else {
                    boxWidth = Integer.parseInt(boxData[1]);
                    boxHeight = Integer.parseInt(boxData[0]);
                }
                Box box = new Box(boxWidth, boxHeight);
                currentSolution.repair(box);
                numBoxes++;
            }
            currentSolution.sortNeighbourhoodsByWidth();
            globalBestSolution = new Solution(currentSolution);
            previousSolution = new Solution(currentSolution);
            globalBestSolution.printSolution();

            // Init weights.
            destroyMethodWeights[0] = 1.00;
            destroyMethodWeights[1] = 1.00;
            destroyMethodWeights[2] = 1.00;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    private void newBest(Solution solution) {
        // If we have found a global best.
        if (solution.totalHeight() < globalBestSolution.totalHeight()) {
            globalBestSolution = new Solution(solution);
            globalBestSolution.printSolution();
            iterationResult = 0;
        // If we are worse than the previous solution, revert to it and try again.
        }else if(solution.totalHeight() >= previousSolution.totalHeight()) {
            solution = new Solution(previousSolution);
            iterationResult = 2;
        } else
            iterationResult = 1; // Found a better solution.

        // Keep the solution for the next iteration.
        previousSolution = new Solution(solution);
    }

    // Destroy variables:
    private double d1DestructionVal = 0.4; // Percent of boxes to remove at random.
    private double d2DestructionVal = 0.2; // Percent of neighbourhoods to remove at random.
    private double d3DestructionVal = 0.6; // Percent of boxes to swap with partners at random.

    // Destroy method 1.
    // Removes a random selection of boxes from the current solution.
    private void destroyRemoveRandom() {
        int numBoxesToRemove = (int) (numBoxes * d1DestructionVal);

        for(int i = 0; i < numBoxesToRemove; i++)
        {
            // Find a random neighbourhood.
            Neighbourhood n = currentSolution.getNeighbourhood(rand.nextInt(currentSolution.getNumNeighbourhoods()));
            if(n.getBoxCount() <= 0) { // If neighbourhood is empty, retry.
                i--;
                continue;
            }
            // Find and take a random box.
            Box box = n.findAndRemove(rand.nextInt(n.getBoxCount()));
            // Add it to the pool.
            poolOfAvailableBoxes.add(box);
        }
    }

    // Destroy method 2.
    // Removes a random selection of neighbourhoods, leaving their boxes in the available box list.
    private void destroyNeighbourhoodRandom() {
        int numNeighbourhoodsToRemove = (int) (currentSolution.getNumNeighbourhoods() * d2DestructionVal);
        for(int i = 0; i < numNeighbourhoodsToRemove; i++)
        {
            // Find a random neighbourhood index.
            int index = rand.nextInt(currentSolution.getNumNeighbourhoods());
            // Get the boxes from that neighbourhood.
            List<Box> boxList = currentSolution.getNeighbourhood(index).getBoxes();
            // Place the boxes into the pool and remove the empty neighbourhood.
            poolOfAvailableBoxes.addAll(boxList);
            currentSolution.removeNeighbourhood(index);
        }
    }

    // Destroy method 3.
    private void destroySwapRandom() {
        int numBoxesToSwap = (int) (numBoxes * d3DestructionVal);

        for(int i = 0; i < numBoxesToSwap/2; i++) // Divide by two because we want to swap within the percentage of moving boxes.
        {
            // Find and remove two random boxes.
            Neighbourhood n1 = currentSolution.getNeighbourhood(rand.nextInt(currentSolution.getNumNeighbourhoods()));
            Neighbourhood n2 = currentSolution.getNeighbourhood(rand.nextInt(currentSolution.getNumNeighbourhoods()));
            if (n1.getBoxes().size() <= 0 || n2.getBoxes().size() <= 0 || n1.equals(n2)) { // If neighbourhoods are empty skip.
                continue;
            }

            Box box1 = n1.findAndRemove(rand.nextInt(n1.getBoxCount()));
            Box box2 = n2.findAndRemove(rand.nextInt(n2.getBoxCount()));

            // Add them to the others neighbourhoods.
            n1.setBox(box1);
            n2.setBox(box2);
        }
    }
}


