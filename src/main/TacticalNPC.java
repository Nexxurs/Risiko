package main;

import interfaces.NPC;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Paul on 24.01.2016.
 */
public class TacticalNPC extends NPC {
    private DataSystem data = DataSystem.getInstance();
    private String[] nations = data.getNations().keySet().toArray(new String[data.getNations().keySet().toArray().length]);
    private String[] continents = data.getContinents().keySet().toArray(new String[data.getContinents().keySet().toArray().length]);

    public TacticalNPC(Owner owner) {
        super(owner);
    }


    @Override
    public void claimNation() {
        Map<String, Double> claimValue = new HashMap<>();

        for(String continent : continents){
            Continent currCont = data.getContinents().get(continent);
            int numberNations = currCont.getNations().length;
            double percentValuePerNation = 1f/numberNations;
            double percentUnowned =0;
            double percentOwnedMe=0;
            double percentOwnedOther=0;
            double contClaimValue=0;
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


            if(percentUnowned!=0){
                if(percentOwnedMe==0&&percentOwnedOther>0){
                    contClaimValue = 1;
                } else if(percentOwnedMe>0&&percentOwnedOther==0){
                    if(percentOwnedMe+percentValuePerNation>0.999){
                        contClaimValue = 2;
                    } else {
                        contClaimValue=0.7;
                    }
                } else {
                    contClaimValue=0.3;
                }
            }

            for(String nation : currCont.getNations()){
                Nation currNation = data.getNations().get(nation);
                if(currNation.getOwner().equals(Owner.Unowned)){
                    claimValue.put(nation, contClaimValue*currNation.getNeighbors().length);
                }
            }
        }

        String[] claimableNations = claimValue.keySet().toArray(new String[claimValue.keySet().toArray().length]);

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

    @Override
    public void placeReinforcements() {
        Map<String, Double> threatMap = new HashMap<>();
        int totalThreat=0;
        for(String nation : nations){
            Nation currNation = data.getNations().get(nation);
            if(currNation.getOwner().equals(owner)){
                double threatLevel=0;
                for(int i =0;i<currNation.getNeighbors().length;i++){
                    Nation currNeighbor = data.getNations().get(currNation.getNeighbors()[i]);
                    if(!currNeighbor.getOwner().equals(owner)){
                        threatLevel+=(currNeighbor.getTrupps()/currNation.getTrupps());
                    }
                }

                if(threatLevel!=0){
                    threatMap.put(nation,threatLevel);
                    totalThreat+=threatLevel;
                }

            }
        }

        String[] reinforcementArray = threatMap.keySet().toArray(new String[threatMap.keySet().toArray().length]);
        int totalReinforcement = 0;
        for(String nation : reinforcementArray){
            Nation currNation = data.getNations().get(nation);
            int newTroops = (int)Math.round((threatMap.get(nation)/totalThreat)*owner.getReinforcment());
            totalReinforcement+=newTroops;
            currNation.setTrupps(currNation.getTrupps()+(int)newTroops);
        }
        owner.decReinforcment(totalReinforcement);

        String highestThreat = reinforcementArray[0];
        for(String nation : reinforcementArray){
            if(threatMap.get(nation)>threatMap.get(highestThreat)) highestThreat = nation;
        }
        Nation highestThreatNation = data.getNations().get(highestThreat);
        highestThreatNation.setTrupps(highestThreatNation.getTrupps()+owner.getReinforcment());
        owner.decReinforcment(owner.getReinforcment());
        //System.out.println(owner.getReinforcment());

    }

    @Override
    public void attackNation() {
        Random rnd = ThreadLocalRandom.current();
        for(String nation : nations)
        {
            Nation currNation = data.getNations().get(nation);
            if(currNation.getOwner().equals(owner) && currNation.getTrupps()>=2){

                for(String neighbor : currNation.getNeighbors()){
                    Nation currNeighbor = data.getNations().get(neighbor);
                    if(!currNeighbor.getOwner().equals(owner)){
                        double attackProbability = rnd.nextDouble()*2.5;
                        System.out.println(currNation.toString()+"("+(currNation.getTrupps()-1)+")"+" vs "+currNeighbor.toString()+"("+currNeighbor.getTrupps()+")"+": "+attackProbability);
                        if(attackProbability<= ((currNation.getTrupps()-1)/currNeighbor.getTrupps())){
                            System.out.println("Attack!");
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
}
