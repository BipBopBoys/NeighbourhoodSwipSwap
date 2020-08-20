package com.criticalhit;

import java.util.*;
import  java.lang.Math;

//Seattle Tupuhi 1286197
//Jesse Whitten 1311972
public class Neighbourhood {
    private int ID;
    private List<Box> boxes = new ArrayList<>();
    private int maxWidth;
    String ANSI_RED = "\u001B[31m";
    String ANSI_RESET = "\u001B[0m";


    // This method finds a box at the given index and returns it, while also removing it from its own list.
    public Box findAndRemove(int index)
    {
        if(index >= boxes.size())
            return null;
        Box box = boxes.get(index);
        boxes.remove(index);
        return box;
    }

    public Neighbourhood(int _maxWidth){
        maxWidth = _maxWidth;
    }

    public Neighbourhood(Neighbourhood neighbourhood){
        this.maxWidth = neighbourhood.maxWidth;
        this.boxes = new ArrayList<>();
        for (Box box: neighbourhood.boxes) {
            this.boxes.add(new Box(box.getHeight(), box.getWidth()));
        }
    }


    public void shuffleOrBoogie(){


    }

    public List<Box> getBoxes() {
        return boxes;
    }
    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }



    public Box getBox(){
        Random rng = new Random();
        return boxes.get(rng.nextInt(boxes.size()));    //Possibly want to remove the box
    }

    public void printRow(){
        for (Box box: boxes) {
            box.printBox();
        }
        System.out.print(":"+ ANSI_RED +"("+ANSI_RESET+ getTotalWidth()+ANSI_RED+","+ANSI_RESET+getTotalHeight()+ANSI_RED+")\n" + ANSI_RESET);
    }
    public int getBoxCount(){ return boxes.size();}
    public void placeBox(Box box){
        boxes.add(box);
    }
    public Boolean setBox(Box box){

        if(boxes.isEmpty()){
            boxes.add(box);
            return true;
        }

        float aHeight = getAverageHeight();
        if(Math.abs(Float.compare(aHeight,box.getHeight())) < 1 ){
            if((getTotalWidth() + box.getWidth()) < maxWidth) {
                boxes.add(box);
                return true;
            }
        }
        if(Math.abs(Float.compare(aHeight,box.getWidth())) < 1 ){
            box.rotate();
            if((getTotalWidth() + box.getWidth()) < maxWidth){
                boxes.add(box);
                return true;
            }
        }
        return false;
    }
    private Boolean betweenTallAndShort(int size){
        int tally = 0;
        int shorty = 0;
        for (Box box: boxes) {
            if(box.getHeight() > tally)
                tally = box.getHeight();
            if(box.getHeight() < shorty)
                shorty = box.getHeight();
        }
        return (tally >= size && shorty <= size);
    }
    private float getAverageHeight(){
        float aHeight = 0;{
            for (Box box: boxes) {
                aHeight += box.getHeight();
            }
            aHeight /= boxes.size();
        }
        return aHeight;
    }
    public int getTotalHeight(){
        int height = 0;
        for (Box box: boxes) {
            if(height < box.getHeight())
                height = box.getHeight();
        }
        return height;
    }
    public int getTotalWidth(){
        int width = 0;
        for (Box box: boxes) {
            width += box.getWidth();
        }
        return width;
    }
    public void sortByHeight(){
        Collections.sort(boxes, new SortByHeight());
    }
    public int getSpace(){
        return maxWidth - getTotalWidth();
    }
    public Box getBigBox(){
        return boxes.get(0);
    }
}
class SortByHeight implements Comparator<Box> {
    public int compare(Box a, Box b){
        return b.getWidth() - a.getWidth();
    }
}