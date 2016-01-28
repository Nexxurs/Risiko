package main;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.Font;


/**
 * Created by Paul on 18.01.2016.
 * Endscreen is the last thing shown in the application. It tells the player if he has won or lost.
 */
public class Endscreen extends StackPane {

    private Label text;

    public Endscreen(double prefWidth, double prefHeight){
        super();
        this.setPrefSize(prefWidth,prefHeight);
        text = new Label();
        text.setFont(Font.font(50));
        this.getChildren().add(text);

        this.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE,null,null)));
        this.setStyle("-fx-border-width: 8; -fx-border-style: solid;");

    }

    /**
     *
     * @param playerName
     * @param win if yes, Text will be in blue and will say that the player won, else red and that he lost
     */
    public void setText(String playerName, boolean win){
        if(win){
            text.setText(playerName+" Won!");
            text.setTextFill(Color.BLUE);
            this.setStyle(this.getStyle()+"-fx-border-color: darkblue;");
        } else {
            text.setText(playerName+" Lost!");
            text.setTextFill(Color.RED);
            this.setStyle(this.getStyle()+"-fx-border-color: darkred;");
        }
    }
}
