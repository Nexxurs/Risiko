package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Arrays;
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

    public StringProperty statusProperty() {
        return status;
    }

    public Map<String, Continent> getContinents() {
        if(continents == null) continents=new HashMap<>();
        return continents;
    }

    /**
     *
     * @param attacker
     * @param defender
     * @return true, if the attack was succesfull and no defending troops are remaining.
     * else, false (even if the attack was a success, but there are defending troops left)
     */
    public boolean attackNation(Nation attacker, Nation defender)
    {
        int[] attack;
        int[] defend;
        if(attacker.getTrupps()> 3) attack = new int[3];
        else attack = new int[attacker.getTrupps()-1];
        if(defender.getTrupps()>= 2) defend = new int[2];
        else defend = new int[1];
        rollDice(attack);
        rollDice(defend);
        for (int i = 0; (i < attack.length)&&(i<defend.length); i++)
        {
            if(attack[attack.length-1-i]>defend[defend.length-1-i]) defender.setTrupps(defender.getTrupps()-1);
            else attacker.setTrupps(attacker.getTrupps()-1);
        }
        if(defender.getTrupps() == 0)
        {
            defender.getOwner().decOwendNations();
            defender.setOwner(attacker.getOwner());
            defender.setTrupps(1);
            attacker.getOwner().addOwendNations();
            attacker.setTrupps(attacker.getTrupps()-1);
            return true;
        }
        return false;
    }

    public static void rollDice(int[] rolls)
    {
        for (int i = 0; i < rolls.length; i++)
        {
            rolls[i] = (int)((Math.random()*6)+1);
        }
        Arrays.sort(rolls);
    }

}
