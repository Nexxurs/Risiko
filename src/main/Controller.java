package main;

import interfaces.Gui;
import interfaces.TroopSelectionResult;

import java.util.Arrays;

public class Controller implements TroopSelectionResult{
    DataSystem data;
    private int phase;
    private String[] nations;
    private String[] continents;
    private Nation selected = null;
    private Nation capture = null;
    private Gui gui;


    public Controller(Gui gui){
        data = DataSystem.getInstance();
        phase = 0;
        nations = data.getNations().keySet().toArray(new String[data.getNations().keySet().toArray().length]);
        continents = data.getContinents().keySet().toArray(new String[data.getContinents().keySet().toArray().length]);
        this.gui = gui;
    }


//        if(phase == 2)
//        {
//            //Verstärkung verteilen
//            //player1.verstärkung
//            //player2.verstärkung
//
//            //Angriff, Bewegung
//            //player1.angriff loop
//            //player2.angriff loop
//        }


    private static int dice()
    {
        return (int)(Math.random()*6)+1;
    }

    public static void rollDice(int[] rolls)
    {
        for (int i = 0; i < rolls.length; i++)
        {
            rolls[i] = dice();
        }
        Arrays.sort(rolls);
    }

    public void clickedNext()
    {
        if(phase == 2)
        {
            selected.setHighlight(false);
            //Computer attack
            Owner.Player1.setReinforcment(getReinforcment(Owner.Player1)+ Owner.Player1.getOwendNations()/3);
            Owner.Player2.setReinforcment(getReinforcment(Owner.Player2)+ Owner.Player2.getOwendNations()/3);
            phase = 1;
            data.statusProperty().setValue("reinforcments left: " + Owner.Player1.getReinforcment());
        }
    }

    public void clickedOnNation(String nationID){
        System.out.println(nationID);
        if (phase > 0)
        {
            if(phase == 1)
            {
                if (data.getNations().get(nationID).getOwner() == Owner.Player1)
                {
                    selected.setHighlight(false);
                    selected = data.getNations().get(nationID);
                    selected.setHighlight(true);
                    selected.setTrupps(selected.getTrupps()+1);
                    Owner.Player1.decReinforcment(1);
                    data.statusProperty().setValue("Added 1 army to " + nationID +"\n"+ "reinforcments left: " + Owner.Player1.getReinforcment());
                }
                if (Owner.Player1.getReinforcment() == 0)
                {
                    //computer
                    Nation cur;
                    for (String nation:nations)
                    {
                        cur = data.getNations().get(nation);
                        if (cur.getOwner() == Owner.Player2)
                        {
                            cur.setTrupps(cur.getTrupps()+Owner.Player2.getReinforcment());
                            Owner.Player2.decReinforcment(Owner.Player2.getReinforcment());
                            break;
                        }
                    }
                    phase = 2;
                }
            }
//            if(phase == 3)
//            {
//                if(nationID.equals(capture.getName()))
//                {
//                    if(selected.getTrupps()>1) move(selected,capture);
//                }else if(nationID.equals(selected.getName()))
//                {
//                    if(capture.getTrupps()> 1) move(capture, selected);
//                }else phase = 2;
//            }
            if(phase == 2)
            {
                data.statusProperty().setValue("Phase 2: " + nationID);
                if (data.getNations().get(nationID).getOwner() == Owner.Player1)
                {
                    selected.setHighlight(false);
                    selected = data.getNations().get(nationID);
                    selected.setHighlight(true);
                    data.statusProperty().setValue("Selected " + nationID);
                }
                if (selected != null && data.getNations().get(nationID).getOwner() == Owner.Player2 && selected.isNeighbors(nationID))
                {
                    capture = attack(selected, data.getNations().get(nationID));
//                  if(capture != null) phase = 3;
                }
            }
        }
        if(phase == 0)
        {
            selected = data.getNations().get(nationID);
            if (selected.getOwner() == Owner.Unowned)
            {
                //player 1
                claim(selected,Owner.Player1);
                data.statusProperty().setValue("Claimed "+ nationID);
                //Computer
                Nation cur;
                for (String nation:nations)
                {
                    cur = data.getNations().get(nation);
                    if (cur.getOwner() == Owner.Unowned)
                    {
                        claim(cur,Owner.Player2);
                        break;
                    }

                }
                phase = 1;
                for (String nation:nations)
                {
                    cur = data.getNations().get(nation);
                    if (cur.getOwner() == Owner.Unowned)
                    {
                        phase = 0;
                        break;
                    }

                }
            }
            //data.statusProperty().setValue("Phase 1: " + nationID);
            if(phase == 1)
            {
                Owner.Player1.setReinforcment(getReinforcment(Owner.Player1)+ Owner.Player1.getOwendNations()/3);
                Owner.Player2.setReinforcment(getReinforcment(Owner.Player2)+ Owner.Player2.getOwendNations()/3);
                data.statusProperty().setValue("reinforcments left: " + Owner.Player1.getReinforcment());
            }
        }

    }

    private void claim(Nation nation,Owner p)
    {
        nation.setOwner(p);
        nation.setTrupps(1);
        p.addOwendNations();
    }

    private int getReinforcment(Owner o)
    {
        int reinforcment = 0;
        for (String continent:continents)
        {
            String[] nation = data.getContinents().get(continent).getNations();
            Owner a = data.getNations().get(nation[0]).getOwner();
            for (int j = 1; j < nation.length; j++)
            {
                if(data.getNations().get(nation[j]).getOwner() != a)
                {
                    a = Owner.Unowned;
                    break;
                }
            }
            if(a == o)
            {
                reinforcment += data.getContinents().get(continent).getAddValue();
            }
        }
        return reinforcment;
    }

    private Nation attack(Nation selected, Nation enemy)
    {
        int[] attack;
        int[] defend;
        if(selected.getTrupps()> 3) attack = new int[3];
        else attack = new int[selected.getTrupps()-1];
        if(enemy.getTrupps()>= 2) defend = new int[2];
        else defend = new int[1];
        Controller.rollDice(attack);
        Controller.rollDice(defend);
        for (int i = 0; (i < attack.length)&&(i<defend.length); i++)
        {
            if(attack[attack.length-1-i]>defend[defend.length-1-i]) enemy.setTrupps(enemy.getTrupps()-1);
            else selected.setTrupps(selected.getTrupps()-1);
        }
        if(enemy.getTrupps() == 0)
        {
            enemy.getOwner().decOwendNations();
            enemy.setOwner(selected.getOwner());
            enemy.setTrupps(1);
            selected.getOwner().addOwendNations();
            selected.setTrupps(selected.getTrupps()-1);
            return enemy;
        }
        return null;
    }

    private void move(Nation n1, Nation n2)
    {
        n1.setTrupps(n1.getTrupps()-1);
        n2.setTrupps(n2.getTrupps()+1);
    }

    @Override
    public void troopSelectionResult(int value) {
        //Wird aufgerufen, wenn Truppen ausgewählt wurden, wenn keine Truppen ausgewählt werden, wird -1 übertragen
        //TODO implement
    }
}
