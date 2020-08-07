package com.criticalhit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Solution {
    private List<Neighbourhood> rows = new ArrayList<>();
    private int numBoxes = 0;
    private int width;
    public Solution(int _width) {
        width = _width;
        createNewRow();
    }
    public Solution(Solution solution){
        this.width = solution.width;
        this.rows = new ArrayList<>(solution.rows);
    }
    private void createNewRow(){
        Neighbourhood neighbourhood = new Neighbourhood(width);
        rows.add(neighbourhood);
    }
    private void createNewRow(Neighbourhood neighbourhood){
        rows.add(neighbourhood);
    }
    public void initBox(Box newBox){
        if(!rows.get(rows.size()-1).setBox(newBox)){
            createNewRow();
        }
        rows.get(rows.size()-1).setBox(newBox);
        numBoxes++; // Count the absolute total number of boxes.
    }
    public void printSolution(){
        for (Neighbourhood row: rows) {
            row.printRow();
        }
        System.out.println("Total Height:"+totalHeight());
    }
    public int totalHeight(){
        int height = 0;
        for (Neighbourhood row: rows) {
            height += row.getTotalHeight();
        }
        return height;
    }

    public int getNumNeighbourhoods()
    {
        return rows.size();
    }
    public int getNumBoxes()
    {
        return numBoxes;
    }
    public Neighbourhood getNeighbourhood(int i)
    {
        return rows.get(i);
    }
    public void removeNeighbourhood(int index)
    {
        if(index >= rows.size()) return;
        rows.remove(index);
    }


    public Boolean shakeupNeighbourhood(Neighbourhood neighbourhood){
        Boolean broken = false;
        Neighbourhood tmpNeighbourhood = new Neighbourhood(neighbourhood);
        Neighbourhood bestTmpNeighbourhood = new Neighbourhood(neighbourhood);
        Solution miniSolution,bestMiniSolution;

        tmpNeighbourhood.shuffleOrBoogie();
        if(tmpNeighbourhood.getTotalWidth() > width){
            miniSolution = new Solution(width);
            for (Box box: tmpNeighbourhood.getBoxes()) {
                miniSolution.initBox(box);
            }
            if(miniSolution.totalHeight() < bestMiniSolution.getTotalHeight())
                bestMiniSolution = new Solution(miniSolution);
        }else if(tmpNeighbourhood.getTotalHeight()<bestTmpNeighbourhood.getTotalHeight())
            bestTmpNeighbourhood = new Neighbourhood(tmpNeighbourhood);



        return broken;
    }


    public void sortNeighbourhoodsByWidth(){
        Collections.sort(rows, new SortByWidth());
    }
    public void sortNeighbourhoodsByHeight(){
        Collections.sort(rows, new SortByHeight());
    }
}
class SortByWidth implements Comparator<Neighbourhood> {
    public int compare(Neighbourhood a, Neighbourhood b){
        return a.getTotalWidth() - b.getTotalWidth();
    }
}
class SortByHeight implements Comparator<Neighbourhood> {
    public int compare(Neighbourhood a, Neighbourhood b){
        return a.getTotalHeight() - b.getTotalHeight();
    }
}
