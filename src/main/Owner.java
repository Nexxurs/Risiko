package main;


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Created by Paul on 07.01.2016.
 */
public enum Owner {
    Player1(Color.BLUE),
    Player2(Color.RED),
    Unowned(Color.GRAY);

    private Paint color;
    Owner(Paint color){
        this.color=color;
    }
    public Paint getColor(){
        return color;
    }


}
