package main;

import interfaces.Gui;
import interfaces.NPC;
import interfaces.TroopSelectionResult;

/**
 * Controller controlls the actions of the player
 */
public class Controller implements TroopSelectionResult{
    DataSystem data;
    private int phase;
    private String[] nations;
    private String[] continents;
    private Nation selected = null;
    private Nation[] capture = null;
    private Nation[] move = null;
    private boolean attack = false;
    private NPC computer;
    private Gui gui;


    public Controller(Gui gui){
        data = DataSystem.getInstance();
        phase = 0;
        nations = data.getNations().keySet().toArray(new String[data.getNations().keySet().toArray().length]);
        continents = data.getContinents().keySet().toArray(new String[data.getContinents().keySet().toArray().length]);
        this.gui = gui;
    }

    public void setComputerPlayer(NPC npc){
        this.computer=npc;
    }

    /**
     *  calculations for the next phase and computer attack
     */
    public void clickedNext()
    {
        if(phase == 2)
        {
            selected.setHighlight(false);
            move = null;
            capture = null;
            //Computer attack
            computer.attackNation();
            if(computer.getOwner().getOwendNations() == nations.length) gui.showEndScreen(false);
            //Calculate reinforcments
            Owner.Player1.setReinforcment(getReinforcmentContinent(Owner.Player1)+ Owner.Player1.getOwendNations()/3);
            Owner.Player2.setReinforcment(getReinforcmentContinent(Owner.Player2)+ Owner.Player2.getOwendNations()/3);
            phase = 1;
            data.statusProperty().setValue("reinforcments left: " + Owner.Player1.getReinforcment());
        }
    }

    /**
     * left click actions for the different phases for the player
     *    phase 0: start phase choosing the unowend Nation
     *    phase 1: placing reinforcments
     *    phase 2: select a nation and attack an enemy nation
     * @param nationID name of the nation clicked
     */
    public void leftClickedOnNation(String nationID){
        /*

         */
        if (phase > 0)
        {
            if(phase == 1) //reinforcments placed
            {
                if (data.getNations().get(nationID).getOwner() == Owner.Player1)
                {
                    selected.setHighlight(false);
                    selected = data.getNations().get(nationID);
                    selected.setHighlight(true);
                    if(Owner.Player1.getReinforcment() > 1) gui.showTroopSelection("send reinforcement to " + selected, 1, Owner.Player1.getReinforcment());
                    else
                    {
                        selected.setTroops(selected.getTroops() + 1);
                        Owner.Player1.decReinforcment(1);
                        data.statusProperty().setValue("Added " + 1 + " army to " + selected + "\n" + "reinforcments left: " + Owner.Player1.getReinforcment());
                        computer.placeReinforcements();
                        phase = 2;
                    }
                }
            }
            else if(phase == 2)
            {
                //select Nation
                if (data.getNations().get(nationID).getOwner() == Owner.Player1)
                {
                    selected.setHighlight(false);
                    selected = data.getNations().get(nationID);
                    selected.setHighlight(true);
                    data.statusProperty().setValue("Selected " + nationID);
                }
                //attack from the selected nation
                if (selected != null && selected.getTroops() > 1 && data.getNations().get(nationID).getOwner() == Owner.Player2 && selected.isNeighbors(nationID))
                {
                    attack = true;
                    if(data.attackNation(selected, data.getNations().get(nationID),true)) capture = new Nation[]{selected,data.getNations().get(nationID)};
                    else capture = null;
                    if(selected.getOwner().getOwendNations() == nations.length) gui.showEndScreen(true);
                    else if(capture != null && selected.getTroops() > 2)
                    {
                        attack = true;
                        gui.showTroopSelection("send reinforcments from " + selected + " to " + nationID, 1, selected.getTroops() - 1);
                    }
                }
            }
        }
        // claim nations
        else if(phase == 0)
        {
            selected = data.getNations().get(nationID);
            if (selected.getOwner() == Owner.Unowned)
            {
                //player 1
                data.claimNation(selected,Owner.Player1);
                data.statusProperty().setValue("Claimed "+ nationID);
                //Computer
                computer.claimNation();
                //all nations claimed
                Nation cur;
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
            if(phase == 1)
            {
                Owner.Player1.setReinforcment(getReinforcmentContinent(Owner.Player1)+ Owner.Player1.getOwendNations()/3);
                Owner.Player2.setReinforcment(getReinforcmentContinent(Owner.Player2)+ Owner.Player2.getOwendNations()/3);
                data.statusProperty().setValue("reinforcments left: " + Owner.Player1.getReinforcment());
            }
        }

    }


    /**
     * right click actions for moving troops after attack and between friendly nations for the player
     * @param nationID name of the nation clicked
     */
    public void rightClickedOnNation(String nationID){
        if(phase == 2)
        {
            Nation cur = data.getNations().get(nationID);
            if(selected.getOwner() == cur.getOwner() && selected.isNeighbors(nationID))
            {
                //moving troops after attack
                if(capture != null && ((selected == capture[0] && cur == capture[1]) || (selected == capture[1] && cur == capture[0])))
                {
                    attack = true;
                    if(selected.getTroops() > 2)gui.showTroopSelection("send reinforcments from " + selected +  " to " + nationID , 1, selected.getTroops()-1);
                    else if(selected.getTroops() == 2) moveTroops(capture[0], capture[1], 1);
                    else data.statusProperty().setValue("Not enough troops to move");
                }
                else    //moving troops between friendly nations
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
                        if(selected.getTroops() > 2) gui.showTroopSelection("move troops from " + selected + " to " + nationID, 1, selected.getTroops() - 1);
                        else if(selected.getTroops() == 2) moveTroops(move[0], move[1], 1);
                        else data.statusProperty().setValue("Not enough troops to move");

                    } else
                    {
                        data.statusProperty().setValue("already send troops from " + move[0] + " to " + move[1]);
                    }
                }
            }
        }
    }

    /**
     * calculates how many reinforcment troops the Owner owner gets from continent bonus
     * @param owner
     * @return
     */
    private int getReinforcmentContinent(Owner owner)
    {
        int reinforcment = 0;
        for (String continent:continents)
        {
            String[] nation = data.getContinents().get(continent).getNations();
            Owner cur = data.getNations().get(nation[0]).getOwner();
            for (int j = 1; j < nation.length; j++)
            {
                if(data.getNations().get(nation[j]).getOwner() != cur)
                {
                    cur = Owner.Unowned;
                    break;
                }
            }
            if(cur == owner)
            {
                reinforcment += data.getContinents().get(continent).getAddValue();
            }
        }
        return reinforcment;
    }

    /**
     * moves troops between nation n1 to nation n2
     * @param n1
     * @param n2
     * @param amount
     */
    private void moveTroops(Nation n1, Nation n2, int amount)
    {
        if(selected == n1)
        {
            n1.setTroops(n1.getTroops()-amount);
            n2.setTroops(n2.getTroops()+amount);
        }
        else
        {
            n1.setTroops(n1.getTroops()+amount);
            n2.setTroops(n2.getTroops()-amount);
        }
    }

    /**
     * troops selection for moving around larger amount of troops
     * @param value selected number of troops
     */
    @Override
    public void troopSelectionResult(int value)
    {
        if (value > 0)
        {
            if (phase == 1)
            {
                selected.setTroops(selected.getTroops() + value);
                Owner.Player1.decReinforcment(value);
                data.statusProperty().setValue("Added " + value + " army to " + selected + "\n" + "reinforcments left: " + Owner.Player1.getReinforcment());
                if (Owner.Player1.getReinforcment() == 0)
                {
                    computer.placeReinforcements();
                    phase = 2;
                }
            } else
            {
                if (attack)
                {
                    moveTroops(capture[0], capture[1], value);

                } else
                {
                    moveTroops(move[0], move[1], value);
                }

            }
        }

    }
}
