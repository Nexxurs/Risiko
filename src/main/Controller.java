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
    private Nation[] capture = null;
    private Nation[] move = null;
    private boolean attack = false;
    private Gui gui;


    public Controller(Gui gui){
        data = DataSystem.getInstance();
        phase = 0;
        nations = data.getNations().keySet().toArray(new String[data.getNations().keySet().toArray().length]);
        continents = data.getContinents().keySet().toArray(new String[data.getContinents().keySet().toArray().length]);
        this.gui = gui;
    }



    private static int dice()
    {
        return (int)(Math.random()*6)+1;
    }

    public static String rollDice(int[] rolls)
    {
        for (int i = 0; i < rolls.length; i++)
        {
            rolls[i] = dice();
        }
        Arrays.sort(rolls);
        String result = "";
        for (int i = 0; i < rolls.length; i++)
        {
            result += rolls[i] + " ";
        }
        return result;
    }

    public void clickedNext()
    {
        if(phase == 2)
        {
            selected.setHighlight(false);
            move = null;
            capture = null;
            //Computer attack
            Owner.Player1.setReinforcment(getReinforcment(Owner.Player1)+ Owner.Player1.getOwendNations()/3);
            Owner.Player2.setReinforcment(getReinforcment(Owner.Player2)+ Owner.Player2.getOwendNations()/3);
            phase = 1;
            data.statusProperty().setValue("reinforcments left: " + Owner.Player1.getReinforcment());
        }
    }

    public void leftClickedOnNation(String nationID){
        System.out.println(nationID);
        if (phase > 0)
        {
            if(phase == 1) //reinforcments placed
            {
                if (data.getNations().get(nationID).getOwner() == Owner.Player1)
                {
                    selected.setHighlight(false);
                    selected = data.getNations().get(nationID);
                    selected.setHighlight(true);
                    if(Owner.Player1.getReinforcment() > 1) gui.showTruppSelection("send reinforcement to" + nationID,1,Owner.Player1.getReinforcment());
                    else
                    {
                        selected.setTrupps(selected.getTrupps() + 1);
                        Owner.Player1.decReinforcment(1);
                        data.statusProperty().setValue("Added " + 1 + " army to " + selected + "\n" + "reinforcments left: " + Owner.Player1.getReinforcment());
                        placTruppsCom();
                    }
                }
            }
            else if(phase == 2)  // attack / select Nation
            {
                //data.statusProperty().setValue("Phase 2: " + nationID);
                if (data.getNations().get(nationID).getOwner() == Owner.Player1)
                {
                    selected.setHighlight(false);
                    selected = data.getNations().get(nationID);
                    selected.setHighlight(true);
                    data.statusProperty().setValue("Selected " + nationID);
                }
                if (selected != null && selected.getTrupps() > 1 && data.getNations().get(nationID).getOwner() == Owner.Player2 && selected.isNeighbors(nationID))
                {
                    attack = true;
                    capture = attack(selected, data.getNations().get(nationID));
                    if(selected.getOwner().getOwendNations() == nations.length) gui.showEndScreen(true);
                }
            }
        }
        if(phase == 0) // claim nations
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

    public void rightClickedOnNation(String nationID){
        if(phase == 2)
        {
            Nation cur = data.getNations().get(nationID);
            if(selected.getOwner() == cur.getOwner() && selected.isNeighbors(nationID))
            {
                if(capture != null && ((selected == capture[0] && cur == capture[1]) || (selected == capture[1] && cur == capture[0])))
                {
                    attack = true;
                    if(selected.getTrupps() > 2)gui.showTruppSelection("send reinforcments from " + selected +  " to " + nationID ,1,selected.getTrupps()-1);
                    else if(selected.getTrupps() == 2) moveTrupps(capture[0], capture[1], 1);
                    else data.statusProperty().setValue("Not enough trupps to move");
                }
                else
                {
                    if (move == null)
                    {
                        move = new Nation[2];
                        move[0] = selected;
                        move[1] = cur;
                    }
                    if ((selected == move[0] && cur == move[1]) || (selected == move[1] && cur == move[0]))
                    {
                        attack = false;
                        if(selected.getTrupps() > 2) gui.showTruppSelection("move trupps from " + selected + " to " + nationID, 1, selected.getTrupps() - 1);
                        else if(selected.getTrupps() == 2) moveTrupps(move[0], move[1], 1);
                        else data.statusProperty().setValue("Not enough trupps to move");

                    } else
                    {
                        data.statusProperty().setValue("already send trupps from " + move[0] + " to " + move[1]);
                    }
                }
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

    private Nation[] attack(Nation selected, Nation enemy)
    {
        int[] attack;
        int[] defend;
        String status = "Attacked " + enemy + " from " + selected + "\n";
        if(selected.getTrupps()> 3) attack = new int[3];
        else attack = new int[selected.getTrupps()-1];
        if(enemy.getTrupps()>= 2) defend = new int[2];
        else defend = new int[1];
        status += "Attackdice: ";
        status += Controller.rollDice(attack) + " Defenddice: ";
        status += Controller.rollDice(defend);
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

            data.statusProperty().setValue(status + "\n" + "Player1 captured " + enemy);
            return new Nation[]{selected,enemy};
        }
        data.statusProperty().setValue(status);
        return null;
    }

    private void moveTrupps(Nation n1, Nation n2,int amount)
    {
        if(selected == n1)
        {
            n1.setTrupps(n1.getTrupps()-amount);
            n2.setTrupps(n2.getTrupps()+amount);
        }
        else
        {
            n1.setTrupps(n1.getTrupps()+amount);
            n2.setTrupps(n2.getTrupps()-amount);
        }
    }

    private void placTruppsCom()
    {
        Nation cur;
        for (String nation : nations)
        {
            cur = data.getNations().get(nation);
            if (cur.getOwner() == Owner.Player2)
            {
                cur.setTrupps(cur.getTrupps() + Owner.Player2.getReinforcment());
                Owner.Player2.decReinforcment(Owner.Player2.getReinforcment());
                break;
            }
        }
        phase = 2;
    }

    @Override
    public void troopSelectionResult(int value)
    {
        //Wird aufgerufen, wenn Truppen ausgewählt wurden, wenn keine Truppen ausgewählt werden, wird -1 übertragen
        if (value > 0)
        {
            if (phase == 1)
            {
                selected.setTrupps(selected.getTrupps() + value);
                Owner.Player1.decReinforcment(value);
                data.statusProperty().setValue("Added " + value + " army to " + selected + "\n" + "reinforcments left: " + Owner.Player1.getReinforcment());
                if (Owner.Player1.getReinforcment() == 0)
                {
                    placTruppsCom();
                }
            } else
            {
                if (attack)
                {
                    moveTrupps(capture[0], capture[1], value);

                } else
                {
                    moveTrupps(move[0], move[1], value);
                }

            }
        }

    }
}
