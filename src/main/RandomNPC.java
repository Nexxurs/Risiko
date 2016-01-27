package main;

import interfaces.NPC;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Paul on 23.01.2016.
 * A computer player, which chooses his moves random (only allowed moves are possible)
 */
public class RandomNPC extends NPC{
    private DataSystem data = DataSystem.getInstance();
    private String[] nations = data.getNations().keySet().toArray(new String[data.getNations().keySet().toArray().length]);

    /**
     * Constructor will delegate the owner to the abstract class NPC
     * @param owner Select which Player should be played as NPC
     */
    public RandomNPC(Owner owner){
        super(owner);
    }

    /**
     * Checks all the nations for a unowned one and claims it
     */
    @Override
    public void claimNation() {
        shuffleArray(nations);
        for(String nation : nations){
            Nation currNation = data.getNations().get(nation);
            if(currNation.getOwner().equals(Owner.Unowned)){
                data.claimNation(currNation,owner);
                return;
            }
        }
    }

    /**
     * Goes through all nations of the specified owner and gives them a random number
     * of reinforcements (smaller or equal to the owners possible reinforcements)
     * until all the reinforcements are used
     */
    @Override
    public void placeReinforcements() {
        shuffleArray(nations);
        Random rnd = ThreadLocalRandom.current();
        while(owner.getReinforcment()>0){
            for(String nation : nations){
                Nation currNation = data.getNations().get(nation);
                if(currNation.getOwner().equals(owner)){
                    int rndNr = rnd.nextInt(owner.getReinforcment())+1;
                    currNation.setTrupps(currNation.getTrupps()+rndNr);
                    owner.decReinforcment(rndNr);
                    if(owner.getReinforcment() <= 0) return;
                }
            }
        }
    }

    /**
     * Goes through all nations of the specified owner and checks for enemy neighbors.
     * For each enemy there is a possibility of 1 to 5 that the nation will attack it
     * (Only if there are more than 1 troops in the nation at the moment of attacking)
     */
    @Override
    public void attackNation()
    {
        Random rnd = ThreadLocalRandom.current();
        for(String nation : nations)
        {
            Nation currNation = data.getNations().get(nation);
            if(currNation.getOwner().equals(owner) && currNation.getTrupps()>=2){
                for(String neighbor : currNation.getNeighbors()){
                    int rndNr = rnd.nextInt(10);
                    if(rndNr<=2){
                        Nation currNeighbor = data.getNations().get(neighbor);
                        if(!currNeighbor.getOwner().equals(owner)){
                            while(currNation.getTrupps()>1){
                                if(data.attackNation(currNation,currNeighbor,false)){
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    /**
     * Help method to shuffle the Array, so it can be gone through in a random order
     * @param array
     */
    private void shuffleArray(String[] array)
    {
        Random rnd = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            String a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }
}
