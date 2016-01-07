package main;

import javafx.beans.property.IntegerProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 07.01.2016.
 * Ist ein Singleton, damit GUI und Controller immer auf die gleichen Daten zugreifen
 */
public class DataSystem {
    private static DataSystem system;
    private Map<String, Nation> nations;
    private Map<String, Continent> continents;


    public DataSystem(){


    }
    public static DataSystem getInstance(){
        if(system==null){
            system=new DataSystem();
        }
        return system;
    }

    public Map<String, Nation> getNations() {
        if(nations==null) nations = new HashMap<>();
        return nations;
    }
/*
    public void setNations(Map<String, Nation> nations) {
        this.nations = nations;
    }
*/
    public Map<String, Continent> getContinents() {
        if(continents == null) continents=new HashMap<>();
        return continents;
    }
/*
    public void setContinents(Map<String, Continent> continents) {
        this.continents = continents;
    }
*/
}
