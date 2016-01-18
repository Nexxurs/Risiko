package main;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;


/**
 * Created by Paul on 18.01.2016.
 */
public class Endscreen extends StackPane {

    private Label text;
    private double prefWidth,prefHeight;

    public Endscreen(double prefWidth, double prefHeight){
        super();
        this.prefHeight=prefHeight;
        this.prefWidth=prefWidth;
        this.setPrefSize(prefWidth,prefHeight);
        text = new Label();
        text.setFont(Font.font(50));
        this.getChildren().add(text);

        this.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE,null,null)));

    }

    /**
     *
     * @param playerName
     * @param win if yes, Text will be in blue and  will say "Victory", else red and "Defeat"
     */
    public void setText(String playerName, boolean win){
        if(win){
            text.setText(playerName+" Won!");
            text.setTextFill(Color.BLUE);
        } else {
            text.setText(playerName+" Lost!");
            text.setTextFill(Color.RED);
        }
    }
}
