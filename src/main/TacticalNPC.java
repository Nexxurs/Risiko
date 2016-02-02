package main;

import interfaces.NPC;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Paul on 24.01.2016.
 * A NPC, which calculates the best step to go for with as little as possible random numbers
 */
public class TacticalNPC extends NPC {
    private DataSystem data = DataSystem.getInstance();
    private String[] nations = data.getNations().keySet().toArray(new String[data.getNations().keySet().toArray().length]);
    private String[] continents = data.getContinents().keySet().toArray(new String[data.getContinents().keySet().toArray().length]);

    //Delegate the constructor to the constructor of the abstract class NPC
    public TacticalNPC(Owner owner) {
        super(owner);
    }

    /**
     * calculates a 'Claim Value' for each unowned nation and claims the one with the highest value.
     * It takes into consideration how many nations which player got on a continent. The priority list is as follows
     * If it is possible to get a whole continent with the next claim, the npc will always choose that nation
     * If only the opponent has one or more nations claimed on a continent, the npc claims one himself so the opponent doesn't get a free continent
     * If there are no nations at all claimed on a continent, the npc claims a nation on another contested continent
     */
    @Override
    public void claimNation() {
        Map<String, Double> claimValue = new HashMap<>();

        for(String continent : continents){
            Continent currCont = data.getContinents().get(continent);
            int numberNations = currCont.getNations().length;
            //the values will be calculated in percentage, so the number of nations are being taken into consideration
            double percentValuePerNation = 1f/numberNations;
            double percentUnowned =0;
            double percentOwnedMe=0;
            double percentOwnedOther=0;
            double contClaimValue=0;
            //Get the percent values for the doubles above
            for(String nation : currCont.getNations()){
                Nation currNation = data.getNations().get(nation);
                if(currNation.getOwner().equals(Owner.Unowned)){
                    percentUnowned+=percentValuePerNation;
                } else if(currNation.getOwner().equals(owner)) {
                    percentOwnedMe+=percentValuePerNation;
                } else {
                    percentOwnedOther+=percentValuePerNation;
                }
            }


            if(percentUnowned!=0){      //Only claim something, that is unowned
                if(percentOwnedMe==0&&percentOwnedOther>0){ //Claim a nation, if the opponent has some nations while the npc has none on this continent (2. priority)
                    contClaimValue = 1;
                } else if(percentOwnedMe>0&&percentOwnedOther==0){
                    if(percentOwnedMe+percentValuePerNation>0.999){ //Claim a nation, which gives the npc the whole continent for himself (1. priority)
                        contClaimValue = 2;
                    } else {
                        contClaimValue=0.7; //Claim a nation of a continent where the opponent doesn't own anything (3. priority)
                    }
                } else {
                    contClaimValue=0.3; //Claim a random nation (4. priority)
                }
            }

            for(String nation : currCont.getNations()){
                Nation currNation = data.getNations().get(nation);
                if(currNation.getOwner().equals(Owner.Unowned)){
                    claimValue.put(nation, contClaimValue*currNation.getNeighbors().length); //the more central the nation is, the better. So the npc chooses a nation with more neighbors first
                }
            }
        }

        String[] claimableNations = claimValue.keySet().toArray(new String[claimValue.keySet().toArray().length]);

        //Claim the nation with the highest claim value
        String resultNation="";
        double resultValue=0;

        for(String nation : claimableNations){
            if(claimValue.get(nation)>resultValue){
                resultNation = nation;
                resultValue = claimValue.get(nation);
            }
        }
        if(resultNation.equals("")) System.out.println("Kein Result gefunden!");
        data.claimNation(data.getNations().get(resultNation),owner);
    }

    /**
     * Placement of Reinforcements
     * The threat is calculated as a prozentual value. It checks each owned nation for enemy neighbors and
     * gives reinforcements equal to the prozentual threat level of the nation.
     * Because of rounding errors (prozentual reinforcements) some troops cannot be send to a nation.
     * These reinforcements will be added to the nation with the highest threat level
     */
    @Override
    public void placeReinforcements() {
        Map<String, Double> threatMap = new HashMap<>(); //Threat level is the number of enemy troops in neighbor nations
                                        // in comparison to the own troops of the nation. It will be saved for each nation
        int totalThreat=0;     //Total Threat is the sum of all threats to the player (For percentage calculations)
        for(String nation : nations){
            Nation currNation = data.getNations().get(nation);
            if(currNation.getOwner().equals(owner)){
                double threatLevel=0;
                for(int i =0;i<currNation.getNeighbors().length;i++){
                    Nation currNeighbor = data.getNations().get(currNation.getNeighbors()[i]);
                    if(!currNeighbor.getOwner().equals(owner)){
                        threatLevel+=(currNeighbor.getTroops()/currNation.getTroops());
                    }
                }

                if(threatLevel!=0){
                    threatMap.put(nation,threatLevel);
                    totalThreat+=threatLevel;
                }

            }
        }

        String[] reinforcementArray = threatMap.keySet().toArray(new String[threatMap.keySet().toArray().length]);
                //ReinforcementArray is an array which holds all the nations who need reinforcements
        int totalReinforcement = 0;
        for(String nation : reinforcementArray){
            Nation currNation = data.getNations().get(nation);
            int newTroops = (int)Math.round((threatMap.get(nation)/totalThreat)*owner.getReinforcment()); //give Reinforcements to a nation in relation to the total threat of all nations
            totalReinforcement+=newTroops;
            currNation.setTroops(currNation.getTroops()+(int)newTroops);
        }
        owner.decReinforcment(totalReinforcement);

        //Get the nation with the highest Threat level
        String highestThreat = reinforcementArray[0];
        for(String nation : reinforcementArray){
            if(threatMap.get(nation)>threatMap.get(highestThreat)) highestThreat = nation;
        }
        //Give that nation the remaining reinforcements
        Nation highestThreatNation = data.getNations().get(highestThreat);
        highestThreatNation.setTroops(highestThreatNation.getTroops()+owner.getReinforcment());
        owner.decReinforcment(owner.getReinforcment());
    }

    /**
     * Checks every owned nation, which is capable of attacking (more than 1 troop).
     * If it attacks depends on a random variable. If it is smaller than the relative Troopvalue
     * (possible attack troops/defending troops), the nation will be attacked.
     * If the attacker has 2.5 times the troops of the opponent, he will be attacked for sure
     */
    @Override
    public void attackNation() {
        Random rnd = ThreadLocalRandom.current();
        for(String nation : nations)
        {
            Nation currNation = data.getNations().get(nation);
            if(currNation.getOwner().equals(owner) && currNation.getTroops()>=2){

                for(String neighbor : currNation.getNeighbors()){
                    Nation currNeighbor = data.getNations().get(neighbor);
                    if(!currNeighbor.getOwner().equals(owner)){
                        double attackProbability = rnd.nextDouble()*2.5;
                        //System.out.println(currNation.toString()+"("+(currNation.getTroops()-1)+")"+" vs "+currNeighbor.toString()+"("+currNeighbor.getTroops()+")"+": "+attackProbability);
                        if(attackProbability<= ((currNation.getTroops()-1)/currNeighbor.getTroops())){
                            while(currNation.getTroops()>1){
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
}
