package main;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;

/**
 * Created by Paul on 07.01.2016.
 */
public class Nation {
    private String name;
    private String[] neighbors;
    private Owner owner;
    private ObjectProperty<Paint> color;
    private IntegerProperty truppCounter;
    private int capitalX;
    private int capitalY;



    public Nation(String name) {
        this.name = name;
        truppCounter = new SimpleIntegerProperty(0);
        owner=Owner.Unowned;
        color = new SimpleObjectProperty<>(owner.getColor());
    }

    public void addNeighbor(String newNeighbor) {
        if(neighbors==null) {
            neighbors = new String[]{newNeighbor};
            return;
        }
        String[] newArray = new String[neighbors.length+1];
        for(int i =0;i<neighbors.length;i++){
            if(neighbors[i].equals(newNeighbor)){
                //System.out.println(name+" already has neighbor "+newNeighbor);
                return;
            }
            newArray[i]=neighbors[i];
        }
        newArray[neighbors.length]=newNeighbor;
        neighbors=newArray;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
        color.setValue(this.owner.getColor());
    }

    public ObjectProperty<Paint> getColorProperty(){
        return color;
    }

    public String getName() {
        return name;
    }

    public String[] getNeighbors() {
        return neighbors;
    }

    public Owner getOwner() {
        return owner;
    }

    public IntegerProperty getTruppCounter() {
        return truppCounter;
    }

    public int getTrupps(){
        return truppCounter.get();
    }
    public void setTrupps(int value){
        truppCounter.setValue(value);
    }

    public int getCapitalX() {
        return capitalX;
    }

    public int getCapitalY() {
        return capitalY;
    }
    public void setCapitalX(int capitalX) {
        this.capitalX = capitalX;
    }

    public void setCapitalY(int capitalY) {
        this.capitalY = capitalY;
    }

}
