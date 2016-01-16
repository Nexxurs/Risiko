package main;



import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created by Paul on 07.01.2016.
 */
public enum Owner {
    Player1(Color.BLUE,Color.DODGERBLUE,0,0),
    Player2(Color.RED,Color.INDIANRED,0,0),
    Unowned(Color.GRAY,Color.GRAY,0,0);

    private Paint color;
    private Paint highlightColor;
    private int owendNations;
    private int reinforcment;

    Owner(Paint color,Paint hightlight,int owendNations,int reinforcment){
        this.color=color;
        this.highlightColor=hightlight;
        this.owendNations=owendNations;
        this.reinforcment=reinforcment;
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
