package main;

import interfaces.NPC;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Paul on 23.01.2016.
 */
public class RandomNPC implements NPC{
    private Owner owner;
    private DataSystem data = DataSystem.getInstance();
    private String[] nations = data.getNations().keySet().toArray(new String[data.getNations().keySet().toArray().length]);

    /**
     *
     * @param owner Select which Player should be played as NPC
     */
    public RandomNPC(Owner owner){
        this.owner=owner;
    }

    @Override
    public void claimNation() {
        shuffleArray(nations);
        for(String nation : nations){
            Nation currNation = data.getNations().get(nation);
            if(currNation.getOwner().equals(Owner.Unowned)){
                currNation.setOwner(owner);
                currNation.setTrupps(1);
                owner.addOwendNations();
                return;
            }
        }
    }

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
                    if(owner.getReinforcment() <= 0) break;
                }
            }
        }
    }

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
