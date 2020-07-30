package com.criticalhit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//Seattle Tupuhi 1286197
//Jesse Whitten 1811972
public class Neighbourhood {
    private int ID;
    private List<Box> boxes = new ArrayList<>();

    public Neighbourhood(int _ID) {
        this.ID = _ID;
    }

    public int getID() {
        return ID;
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
    public void setBox(Box box){
        boxes.add(box);
    }
    public int getTotalHeight(){
        return -1;
    }
    public int getTotalWidth(){
        return -1;
    }

}
