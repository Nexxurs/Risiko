package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    private StringProperty status;



    private DataSystem(){
        status = new SimpleStringProperty("Status");


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

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public Map<String, Continent> getContinents() {
        if(continents == null) continents=new HashMap<>();
        return continents;
    }


}
