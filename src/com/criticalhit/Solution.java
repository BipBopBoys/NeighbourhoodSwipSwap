package com.criticalhit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//Seattle Tupuhi 1286197
//Jesse Whitten 1311972
public class Solution {
    private List<Neighbourhood> rows = new ArrayList<>();
    private int width;
    public Solution(int _width) {
        width = _width;
        createNewRow();
    }

    public Solution(Solution solution){
        this.width = solution.width;
        this.rows = new ArrayList<>();
        for (Neighbourhood row: solution.rows) {
            this.rows.add(new Neighbourhood(row));
        }
        System.out.print("");
    }

    public void repair(Box box){
        sortNeighbourhoodsByWidth();
        for(int i = rows.size()-1; i >= 0;i--) {
            if(rows.get(i).setBox(box))
                return;
        }
        createNewRow();
        rows.get(0).setBox(box);
    }


    private void createNewRow(){
        Neighbourhood neighbourhood = new Neighbourhood(width);
        rows.add(0,neighbourhood);
    }
    private void createNewRow(Neighbourhood neighbourhood){
        rows.add(neighbourhood);
    }

    public void printSolution(){
        for (Neighbourhood row: rows) {
            row.printRow();
        }
        printHeight();
        printBoxCount();
    }
    public int getBoxCount(){
        int c = 0;
        for (Neighbourhood n: rows) {
            c += n.getBoxCount();
        }
        return c;
    }
    public void printBoxCount(){
        System.out.println("Num Boxes = " + getBoxCount());
    }
    public void printHeight(){
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

    public Neighbourhood getNeighbourhood(int i)
    {
        return rows.get(i);
    }
    public void removeNeighbourhood(int index)
    {
        if(index >= rows.size()) return;
        rows.remove(index);
    }


    public void finalPass(){
        sortSolutionByHeight();
        sortNeighbourhoodsByWidth();
        sortNeighbourhoodsByHeight();
        //printSolution();
        while(true){
            //printSolution();
            if(rows.get(0).getTotalWidth() == 0)
                rows.remove(0);
            else{
                Box box = rows.get(0).findAndRemove(0);
                box.rotate();
                for(int i = rows.size()-1;i >= 0;i--){
                    if(i == 0){
                        box.rotate();
                        rows.get(i).placeBox(box);
                        return;
                    }
                    if(box.getWidth() + rows.get(i).getTotalWidth() <= width && box.getHeight() <= rows.get(i).getTotalHeight()){
                        rows.get(i).placeBox(box);
                        break;
                    }
                }
            }
        }
    }

    private int availableSpace(){
        int space = 0;
        for(int i = 1; i < rows.size()-1;i++){
            space += rows.get(i).getSpace();
        }
        return space;
    }
    public void sortSolutionByHeight(){
        Collections.sort(rows, new SortBySolutionHeight());
    }

    public void sortNeighbourhoodsByWidth(){
        Collections.sort(rows, new SortByWidth());
    }
    public void sortNeighbourhoodsByHeight(){
        for (Neighbourhood row: rows) {
            row.sortByHeight();
        }
    }
}
class SortBySolutionHeight implements Comparator<Neighbourhood> {
    public int compare(Neighbourhood a, Neighbourhood b){
        return b.getTotalHeight() - a.getTotalHeight();
    }
}
class SortByWidth implements Comparator<Neighbourhood> {
    public int compare(Neighbourhood a, Neighbourhood b){
        return a.getTotalWidth() - b.getTotalWidth();
    }
}

