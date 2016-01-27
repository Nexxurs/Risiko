package main;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;

/**
 * Created by Paul on 07.01.2016.
 * Contains the information of a nation. Will be initiated when the world gets initiated
 */
public class Nation {
    private String name;
    private String[] neighbors;
    private Owner owner;
    private ObjectProperty<Paint> color;
    private IntegerProperty truppCounter;
    private int capitalX;
    private int capitalY;
    private boolean highlighted;



    public Nation(String name) {
        this.name = name;
        truppCounter = new SimpleIntegerProperty(0);
        owner=Owner.Unowned;
        color = new SimpleObjectProperty<>(owner.getColor());
    }

    /**
     * Will only be used at the initiation of the gui,
     * because the neighbors are declared after the designing of each nation
     * @param newNeighbor
     */
    public void addNeighbor(String newNeighbor) {
        //if first neighbor, produce new array
        if(neighbors==null) {
            neighbors = new String[]{newNeighbor};
            return;
        }
        String[] newArray = new String[neighbors.length+1];
        for(int i =0;i<neighbors.length;i++){
            if(neighbors[i].equals(newNeighbor)){
                return;
            }
            newArray[i]=neighbors[i];
        }
        newArray[neighbors.length]=newNeighbor;
        neighbors=newArray;
    }

    /**
     * returns true, if nation is neighbor of current nation
     * @param nation
     * @return
     */
    public boolean isNeighbors(String nation){
        for (int i = 0; i < neighbors.length; i++)
        {
            if(nation.equals(neighbors[i])) return true;
        }
        return false;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
        color.setValue(this.owner.getColor());
    }

    public ObjectProperty<Paint> getColorProperty(){
        return color;
    }

    public String toString() {
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

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlight(boolean highlighted) {
        this.highlighted = highlighted;
        if(highlighted){
            color.setValue(owner.getHighlightColor());
        } else {
            color.setValue(owner.getColor());
        }
    }

    public int getCapitalX() {
        return capitalX;
    }

    public int getCapitalY() {
        return capitalY;
    }

    /**
     * Sets the x coordinate of the capital of the Nation. Cannot be in constructor,
     * because the value is declared after the initial construction of the nation
     * @param capitalX
     */
    public void setCapitalX(int capitalX) {
        this.capitalX = capitalX;
    }
    /**
     * Sets the y coordinate of the capital of the Nation. Cannot be in constructor,
     * because the value is declared after the initial construction of the nation
     * @param capitalY
     */
    public void setCapitalY(int capitalY) {
        this.capitalY = capitalY;
    }

}
