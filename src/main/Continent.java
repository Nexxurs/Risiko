package main;

/**
 * Created by Paul on 07.01.2016.
 */
public class Continent{
    private String name;
    private int addValue;
    private String[] nations;

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
