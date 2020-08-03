package com.criticalhit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Solution {
    List<Neighbourhood> rows = new ArrayList<>();
    int width;
    public Solution(int _width) {
        width = _width;
        createNewRow();
    }
    public Solution(Solution solution){
        this.width = solution.width;
        this.rows = new ArrayList<>(solution.rows);
    }
    private void createNewRow(){
        Neighbourhood neighbourhood = new Neighbourhood();
        rows.add(neighbourhood);
    }
    public void initBox(Box newBox){
        if(rows.get(rows.size()-1).getTotalWidth() + newBox.getWidth() > width){
            createNewRow();
        }
        rows.get(rows.size()-1).setBox(newBox);
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
