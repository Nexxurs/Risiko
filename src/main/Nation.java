package main;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by Paul on 07.01.2016.
 */
public class Nation {
    String name;
    String[] neighbors;
    Owner owner;
    IntegerProperty truppCounter;

    public Nation(String name) {
        this.name = name;
        truppCounter = new SimpleIntegerProperty(0);
    }

    public void setNeighbors(String[] neighbors) {
        this.neighbors = neighbors;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setTruppCounter(IntegerProperty truppCounter) {
        this.truppCounter = truppCounter;
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
}
