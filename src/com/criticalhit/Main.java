package com.criticalhit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.util.*;
import java.util.List;

//Seattle Tupuhi 1286197
//Jesse Whitten 1311972
public class Main extends Application {

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



    @Override
    public void start(Stage primaryStage) throws Exception {
        //String[] args = new String[]{"m1a.csv","40"};
        //String[] args = new String[]{"m1c.csv","100"};
        String[] args = new String[]{"m1d.csv","100"};

        if (args.length != 2 && args.length != 3)
            System.out.println("Input is NeighbourhoodSwipSwap [filename] [SheetWidth]");
        else {
            System.out.println("Arguments Accepted: " + args[0] + " + " + args[1]);
            Main packingSolver = new Main();
            packingSolver.initialization(args[0], Integer.parseInt(args[1]));
            int best = packingSolver.run(primaryStage);
            if(args.length == 3) {
                while(best > Integer.parseInt(args[2])){
                    packingSolver.initialization(args[0], Integer.parseInt(args[1]));
                    best = packingSolver.run(primaryStage);
                }
            }
        }

    }

    private int run(Stage primaryStage) {
        for (int i = 0; i < 100000; i++){
            //System.out.println(i);
            chooseAndApplyDestroyMethod();
            //printLonelyBoxes();
            houseLonelyBoxes();
            newBest(currentSolution);
            updateDestroyWeights();


        }
        globalBestSolution.finalPass();
        globalBestSolution.printSolution();

        drawBoxes(primaryStage, globalBestSolution);

        return globalBestSolution.totalHeight();
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
        if(numBoxes != globalBestSolution.getBoxCount()){
            System.err.println("Lost Boxes");
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
            numBoxes = 0;
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
                poolOfAvailableBoxes.add(box);

                //currentSolution.repair(box);
                numBoxes++;
            }
            //Collections.sort(poolOfAvailableBoxes, new SortBoxes());
            //printLonelyBoxes();
            houseLonelyBoxes();
            //currentSolution.sortNeighbourhoodsByWidth();
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
            globalBestSolution.printHeight();
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

            // Add them to the others neighbourhoods if they can fit.
            if(!n1.setBox(box1))
                poolOfAvailableBoxes.add(box1);
            if(!n2.setBox(box2))
                poolOfAvailableBoxes.add(box2);
        }
    }
    // Draw method
    private void drawBoxes(Stage primaryStage, Solution solution) {

        // Draw the optimal solution.
        Pane pane = new Pane();
        Canvas canvas = new Canvas(1000, 1000);
        pane.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        primaryStage.setScene(new Scene(pane));
        primaryStage.show();

        try {
            gc.setFill(Color.BLUE);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            int xLoc = 0;
            int yLoc = (int) canvas.getHeight();

            int sizeScalar = 3;

            for(int i = 0; i < solution.getNumNeighbourhoods(); i++)
            {
                Neighbourhood neighbourhood = solution.getNeighbourhood(i);
                if(neighbourhood == null) continue;

                for(int j = 0; j < solution.getNeighbourhood(i).getBoxes().size(); j++)
                {
                    Box box = solution.getNeighbourhood(i).getBoxes().get(j);
                    if(box == null) continue;


                    // Provide the offset from the bottom of the neighbourhood.
                    yLoc -= box.getHeight()*sizeScalar;

                    // Draw the box.
                    gc.fillRect(xLoc, yLoc, box.getWidth()*sizeScalar, box.getHeight()*sizeScalar);
                    gc.strokeRect(xLoc, yLoc, box.getWidth()*sizeScalar, box.getHeight()*sizeScalar);

                    xLoc += box.getWidth()*sizeScalar; // Move gc along to the next box.
                    yLoc += box.getHeight()*sizeScalar; // Remove the offset provided earlier.
                }
                // Move gc to the start of the next neighbourhood.
                xLoc = 0;
                yLoc -= neighbourhood.getTotalHeight()*sizeScalar;
            }




        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
class SortBoxes implements Comparator<Box>{
    public int compare(Box a, Box b){
        return b.getHeight() - a.getHeight();
    }
}

