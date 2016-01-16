package main;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created by Paul on 07.01.2016.
 */
public enum Owner {
    Player1(Color.BLUE,Color.DODGERBLUE),
    Player2(Color.RED,Color.INDIANRED),
    Unowned(Color.GRAY,Color.GRAY);

    private Paint color;
    private Paint highlightColor;
    Owner(Paint color,Paint hightlight){
        this.color=color;
        this.highlightColor=hightlight;
    }
    public Paint getColor(){
        return color;
    }

    public Paint getHighlightColor() {
        return highlightColor;
    }
}
