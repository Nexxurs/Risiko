package interfaces;

import main.Owner;

/**
 * Created by Paul on 23.01.2016.
 */
public abstract class NPC {
    protected Owner owner;

    public NPC(Owner owner) {
        this.owner = owner;
    }
    public Owner getOwner(){
        return owner;
    }
    /**
     * Phase 0
     */
    public abstract void claimNation();
    /**
     * Phase 1
     */
    public abstract void placeReinforcements();
    /**
     * Phase 2
     */
    public abstract void attackNation();

}

