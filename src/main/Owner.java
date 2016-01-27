package main;



import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created by Paul on 07.01.2016.
 * Owner as an enum, so the Values can be changed globally (enum is automatically a singleton)
 */
public enum Owner {
    Player1(Color.BLUE,Color.DODGERBLUE),
    Player2(Color.RED,Color.INDIANRED),
    Unowned(Color.GRAY,Color.GRAY);

    private Paint color;
    private Paint highlightColor;
    private int owendNations;
    private int reinforcment;

    /**
     * Constructor
     * @param color color for nation, when it's in idle status
     * @param hightlight color for nation, if it is highlighted
     */
    Owner(Paint color,Paint hightlight){
        this.color=color;
        this.highlightColor=hightlight;
        this.owendNations=0;
        this.reinforcment=0;
    }

    public Paint getColor(){
        return color;
    }

    public Paint getHighlightColor() {
        return highlightColor;
    }

    public int getReinforcment(){
        return reinforcment;
    }

    public void setReinforcment(int reinforcment){
        this.reinforcment=reinforcment;
    }

    public void decReinforcment(int decrease){
        this.reinforcment -= decrease;
    }

    public int getOwendNations(){
        return owendNations;
    }

    public void addOwendNations(){
        this.owendNations++;
    }

    public void decOwendNations(){
        this.owendNations--;
    }
}
