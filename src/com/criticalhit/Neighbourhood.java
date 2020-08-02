package com.criticalhit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Seattle Tupuhi 1286197
//Jesse Whitten 1811972
public class Neighbourhood {
    private int ID;
    private List<Box> boxes = new ArrayList<>();
    String ANSI_RED = "\u001B[31m";
    String ANSI_RESET = "\u001B[0m";
    public Neighbourhood(){    }


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
            System.out.print("("+box.getWidth()+","+box.getHeight()+")");
        }
        System.out.print(":"+ ANSI_RED +"("+ANSI_RESET+ getTotalWidth()+ANSI_RED+","+ANSI_RESET+getTotalHeight()+ANSI_RED+")\n" + ANSI_RESET);
    }
    public int getBoxCount(){ return boxes.size();}
    public void setBox(Box box){
        boxes.add(box);
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

}
