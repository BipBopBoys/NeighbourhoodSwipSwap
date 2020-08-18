package com.criticalhit;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

//Seattle Tupuhi 1286197
//Jesse Whitten 1311972
public class Main {

    private Solution currentSolution;
    private Solution bestSolution;
    private List<Box> poolOfAvailableBoxes = new ArrayList<>(); // This is where the destroy methods dump the removed boxes for the repair method.
    private int seed = 0;
    private int numboxes = 0;

    public static void main(String[] args) {
        if (args.length != 2)
            System.out.println("Input is NeighbourhoodSwipSwap [filename] [SheetWidth]");
        else {
            System.out.println("Arguments Accepted: " + args[0] + " + " + args[1]);
            Main packingSolver = new Main();
            packingSolver.initialization(args[0], Integer.parseInt(args[1]));
            packingSolver.run();
        }


        //while
        // Choose destroy

        //repair
        //Acceptance
        //replace best
        //update prob

        // end while
    }

    private void run() {
        for (int i = 0;i<10;i++){
            //destroyNeighbourhoodRandom();
            destroyRemoveRandom();
            //printLonelyBoxes();
            houseLonelyBoxes();
            newBest(currentSolution);
        }
    }
    private void houseLonelyBoxes(){
        int i = 0;
        for (Box box: poolOfAvailableBoxes) {
            i++;
            currentSolution.repair(box);
        }
        printLonelyBoxes();
        System.out.println(i);
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
                numboxes++;
            }
            currentSolution.sortNeighbourhoodsByWidth();
            bestSolution = new Solution(currentSolution);
            bestSolution.printSolution();
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
        if(solution.totalHeight() < bestSolution.totalHeight()) {
            bestSolution = new Solution(solution);
            bestSolution.printSolution();
            solution.printSolution();
        }
    }


    // repair bunnyhop

    // Destroy variables:
    private double d1DestructionVal = 0.10; // Percent of boxes to remove at random.
    private double d2DestructionVal = 0.30; // Percent of neighbourhoods to remove at random.
    private double d3DestructionVal = 0.10; // Percent of boxes to swap with partners at random.
    private List<Double> probOfDestroyMethodsList = new ArrayList<>(); // Contains the selection weight of each method.

    // Destroy method 1.
    // Removes a random selection of boxes from the current solution.
    private void destroyRemoveRandom() {
        Random rand = new Random(seed);
        int numNeighbourhoods = currentSolution.getNumNeighbourhoods();
        int numBoxes = numboxes;
        int numBoxesToRemove = (int) (numBoxes * d1DestructionVal);
        List<Integer> boxIndexesToRemove = new ArrayList<>();

        // Get a list of boxes to remove.
        for (int i = 0; i < numBoxesToRemove; i++)
            boxIndexesToRemove.add(i, rand.nextInt(numBoxes));
        System.out.println(boxIndexesToRemove);

        Collections.sort(boxIndexesToRemove);
        int prevValue = -1; // This is for checking if we double up a value.
        int currentIndexTraversed = 0;
        int indexOfCurrentBox = 0;

        for(int i = 0; i < numNeighbourhoods; i++)
        {
            Neighbourhood curN = currentSolution.getNeighbourhood(i); // Go through each neighbourhood.

            if(boxIndexesToRemove.get(indexOfCurrentBox) - currentIndexTraversed > curN.getBoxCount())
            {
                currentIndexTraversed += curN.getBoxCount(); // Add this neighbourhood box count to the traverse.
                continue; // Go to next Neighbourhood.
            }
            else
            {
                // We can find and remove the box from the neighbourhood, while adding it to the pool of available boxes.
                poolOfAvailableBoxes.add(curN.findAndRemove(boxIndexesToRemove.get(indexOfCurrentBox) - currentIndexTraversed));

                // Update the index to remove.
                prevValue = boxIndexesToRemove.get(indexOfCurrentBox);
                indexOfCurrentBox++;
                if(indexOfCurrentBox == boxIndexesToRemove.size()) return; // If we are done.

                // Here we skip any duplicate box indexes that may have been generated by the random object.
                while(prevValue == boxIndexesToRemove.get(indexOfCurrentBox)) {
                    indexOfCurrentBox++;
                    if(indexOfCurrentBox == boxIndexesToRemove.size()) return; // If we are done.
                }
            }
        }
    }

    // Destroy method 2.
    // Removes a random selection of neighbourhoods, leaving their boxes in the available box list.
    private void destroyNeighbourhoodRandom() {
        Random rand = new Random(seed);
        int numNeighbourhoodsToRemove = (int) (currentSolution.getNumNeighbourhoods() * d2DestructionVal);
        for(int i = 0; i < numNeighbourhoodsToRemove; i++)
        {
            int index = rand.nextInt(currentSolution.getNumNeighbourhoods());
            List<Box> boxList = currentSolution.getNeighbourhood(index).getBoxes();
            poolOfAvailableBoxes.addAll(boxList);
            currentSolution.removeNeighbourhood(index);

        }
    }

    // Destroy method 3.
    private void destroySwapRandom() {
        Random rand = new Random(seed);
        int numNeighbourhoods = currentSolution.getNumNeighbourhoods();
        int numBoxes = numboxes;
        int numBoxesToSwap = (int) (numBoxes * d3DestructionVal);
        List<Integer> boxIndexesToSwap = new ArrayList<>();

        // Get a list of boxes to remove.
        for (int i = 0; i < numBoxesToSwap; i++)
            boxIndexesToSwap.add(i, rand.nextInt(numBoxes));

        Collections.sort(boxIndexesToSwap);

        int prevValue; // This is for checking if we double up a index value.
        int currentIndexTraversed = 0;
        int indexOfCurrentBox = 0;

        for(int i = 0; i < numNeighbourhoods; i++)
        {
            Neighbourhood curN = currentSolution.getNeighbourhood(i); // Go through each neighbourhood.

            if(boxIndexesToSwap.get(indexOfCurrentBox) - currentIndexTraversed > curN.getBoxCount())
            {
                currentIndexTraversed += curN.getBoxCount(); // Add this neighbourhood box count to the traverse.
                continue; // Go to next Neighbourhood.
            }
            else
            {
                // Find a random box from a random neighbourhood.
                Box sourceBox = curN.findAndRemove(boxIndexesToSwap.get(indexOfCurrentBox) - currentIndexTraversed);
                Neighbourhood targetN = currentSolution.getNeighbourhood(rand.nextInt(numNeighbourhoods));
                Box targetBox = targetN.findAndRemove(rand.nextInt(targetN.getBoxCount()));

                // Swap the two boxes.
                targetN.setBox(sourceBox);
                curN.setBox(targetBox);

                // Update the index to remove.
                prevValue = boxIndexesToSwap.get(indexOfCurrentBox);
                indexOfCurrentBox++;
                if(indexOfCurrentBox == boxIndexesToSwap.size()) return; // If we are done.

                // Here we skip any duplicate box indexes that may have been generated by the random object.
                while(prevValue == boxIndexesToSwap.get(indexOfCurrentBox)) {
                    indexOfCurrentBox++;
                    if(indexOfCurrentBox == boxIndexesToSwap.size()) return; // If we are done.
                }
            }
        }
    }
}


