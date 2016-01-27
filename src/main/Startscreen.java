package main;

import interfaces.Gui;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by Paul on 25.01.2016.
 * Is visible at the beginning of the game to choose a opponent
 */
public class Startscreen extends VBox {
    Button start;
    ToggleGroup toggleGroup;
    Gui gui;

    /**
     * Constructor needs a gui, which gets the chosen opponent.
     * @param prefWidth
     * @param prefHeight
     * @param gui
     */
    public Startscreen(int prefWidth, int prefHeight, Gui gui){
        super();
        this.setPrefSize(prefWidth,prefHeight);
        this.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE,null,null)));
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);
        this.setStyle("-fx-border-width: 1; -fx-border-style: solid; -fx-border-color: black");

        this.gui=gui;

        Label title = new Label("Welcome to Risiko!");
        title.setFont(Font.font(30));
        this.getChildren().add(title);

        Label text = new Label("Please select your Opponent");
        this.getChildren().add(text);

        //Togglegroup is used to get the chosen opponent.
        toggleGroup = new ToggleGroup();
        HBox opponentPicker = new HBox();
        opponentPicker.setAlignment(Pos.CENTER);
        opponentPicker.setSpacing(10);
        RadioButton random = new RadioButton("Random");
        random.setToggleGroup(toggleGroup);
        opponentPicker.getChildren().add(random);
        RadioButton tactical = new RadioButton("Tactical");
        tactical.setToggleGroup(toggleGroup);
        opponentPicker.getChildren().add(tactical);
        toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
        this.getChildren().add(opponentPicker);

        start = new Button("Play Risiko");
        start.setFont(Font.font(15));
        start.setOnAction(this::onStart);
        this.getChildren().add(start);


    }

    private void onStart(ActionEvent ae){
        RadioButton selectedButton = (RadioButton)toggleGroup.getSelectedToggle();
        String opponentName = selectedButton.getText();
        if(opponentName.equals("Random")) gui.setOpponent(new RandomNPC(Owner.Player2));
        else if(opponentName.equals("Tactical")) gui.setOpponent(new TacticalNPC(Owner.Player2));
        else gui.setOpponent(null); //Null should never happen, because under normal circumstances one radio button needs to be selected
        this.setVisible(false);
    }

    /**
     * requestFocus sets focus on the start button, so the game can be started by hitting the Enter button
     */
    @Override
    public void requestFocus() {
        super.requestFocus();
        start.requestFocus();
    }
}
