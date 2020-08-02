package com.criticalhit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Seattle Tupuhi 1286197
//Jesse Whitten 1811972
public class Main {

    Solution currentSolution;
    Solution bestSolution;
    List<Double> probList = new ArrayList<>();

    public static void main(String[] args) {
        if(args.length != 2)
            System.out.println("Input is NeighbourhoodSwipSwap [filename] [SheetWidth]");
        else{
            System.out.println("Arguments Accepted: " + args[0] + " + " + args[1]);
            Main packingSolver = new Main();
            packingSolver.initialization(args[0],Integer.parseInt(args[1]));
        }


	    //  Init boxes

        // Init neighbourhoods

        //while
        // Choose destroy

        //repair
        //Acceptance
        //replace best
        //update prob

        // end while
    }

    private void run(){

    }
    private void initialization(String path, int width){
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(path));
            String currentLine;
            String boxData[];
            int boxWidth,boxHeight;
            currentSolution = new Solution(width);
            while((currentLine = reader.readLine()) != null){
                boxData = currentLine.split(",");
                if(Integer.parseInt(boxData[0])<Integer.parseInt(boxData[1])){
                    boxWidth = Integer.parseInt(boxData[0]);
                    boxHeight = Integer.parseInt(boxData[1]);
                }else{
                    boxWidth = Integer.parseInt(boxData[1]);
                    boxHeight = Integer.parseInt(boxData[0]);
                }
                Box box = new Box(boxWidth,boxHeight);
                currentSolution.initBox(box);
            }
            newBest(currentSolution);
            bestSolution.printSolution();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(reader != null)
                    reader.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }

        }
    }
    private void newBest(Solution solution){
        bestSolution = solution;
    }
    //Sortbywidth

    // repair bunnyhop

    // destroy1

    // destroy2

    // destroy3

}
