package com.criticalhit;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    List<Neighbourhood> rows = new ArrayList<>();
    int width;
    public Solution(int _width) {
        width = _width;
        createNewRow();
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
}
