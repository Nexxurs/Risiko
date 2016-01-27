package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Paul on 07.01.2016.
 * Is a Singleton, so GUI and controller have access to the same data.
 * It contains a Map for the nations, the continents and a String for the status line on screen
 */
public class DataSystem {
    private static DataSystem system;
    private Map<String, Nation> nations;
    private Map<String, Continent> continents;
    private StringProperty status;


    /**
     * private constructor, because it is a singleton. Will be initiated once at the beginning,
     * when it is needed the first time, so the status is for the first phase of the game.
     */
    private DataSystem(){
        status = new SimpleStringProperty("Please claim your Territory");


    }

    /**
     * Get Singleton instance of DataSystem. If there is no instance, construct it
     * @return
     */
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
     * Manipulate the DataSystem so the owner of the nation changes to a new one
     * @param nation
     * @param owner
     */
    public void claimNation(Nation nation, Owner owner){
        nation.setOwner(owner);
        nation.setTrupps(1);
        owner.addOwendNations();
    }

    /**
     *  Attack a nation as another nation.
     * @param attacker
     * @param defender
     * @param player true if a player attacks
     * @return true, if the attack was succesfull and no defending troops are remaining.
     * else, false (even if the attack was a success, but there are defending troops left)
     */
    public boolean attackNation(Nation attacker, Nation defender,boolean player)
    {
        int[] attack;
        int[] defend;
        String status = "Attacked " + defender + " from " + attacker + "\n";
        if(attacker.getTrupps()> 3) attack = new int[3];
        else attack = new int[attacker.getTrupps()-1];
        if(defender.getTrupps()>= 2) defend = new int[2];
        else defend = new int[1];
        status += "Attackers dice: ";
        status += rollDice(attack) + " Defenders dice: ";
        status += rollDice(defend);
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
            if(player) statusProperty().setValue(status + "\n"+ "Player 1 captured " + defender);
            return true;
        }
        else if(player) statusProperty().setValue(status);
        return false;
    }

    /**
     * Help method for attack method
     * @param rolls
     * @return
     */
    public static String rollDice(int[] rolls)
    {
        for (int i = 0; i < rolls.length; i++)
        {
            rolls[i] = (int)((Math.random()*6)+1);
        }
        Arrays.sort(rolls);
        String result = "";
        for (int i = 0; i < rolls.length; i++)
        {
            result += rolls[i] + " ";
        }
        return result;

    }

}

