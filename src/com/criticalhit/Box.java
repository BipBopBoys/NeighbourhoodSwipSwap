package com.criticalhit;

import java.util.ArrayList;
import java.util.List;

//Seattle Tupuhi 1286197
//Jesse Whitten 1311972
public class Box {
    private int height,width;
    List<Record> history = new ArrayList<>();
        public Box(int _height, int _width){
        height = _height;
        width = _width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public List<Record> getHistory() {
        return history;
    }

    public void setHistory(List<Record> history) {
        this.history = history;
    }

    public void addToHistory(Record r){
        history.add(r);
    }
    public void printBox(){
        System.out.print("("+width+","+height+")");
    }
    public void rotate(){
            //System.out.println("Rotate");
           // printBox();
        int tmp = width;
        width = height;
        height = tmp;
        //printBox();
    }
    public Boolean hasBeenTo(Record r){
        //Has the box been to location before
        return false;
    }
}
