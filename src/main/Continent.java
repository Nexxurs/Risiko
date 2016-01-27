package main;

/**
 * Created by Paul on 07.01.2016.
 * Contains the information of a continent. Will be initiated along with the world.
 * It will be initiated at once, so there are no setters
 */
public class Continent{
    private String name;
    private int addValue;
    private String[] nations;

    /**
     *
     * @param name
     * @param addValue
     * @param nations as String Array, contains the Sring key for the nation in the
     *                DataSystem Nation Map
     */
    public Continent(String name, int addValue, String[] nations) {
        this.name = name;
        this.addValue = addValue;
        this.nations = nations;
    }

    public String getName() {
        return name;
    }

    public int getAddValue() {
        return addValue;
    }

    public String[] getNations() {
        return nations;
    }
}
