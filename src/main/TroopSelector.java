package main;

import interfaces.TroopSelectionResult;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Created by Paul on 17.01.2016.
 */
public class TroopSelector extends VBox {

    private Label label;
    private Slider slider;
    private TroopSelectionResult result;
    private double prefHeight = 170;
    private double prefWidth = 220;

    public TroopSelector(TroopSelectionResult result){
        super();
        this.result = result;



        this.setAlignment(Pos.CENTER);
        this.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));
        this.setStyle("-fx-border-width: 1; -fx-border-style: solid; -fx-border-color: black");
        this.setPrefSize(prefWidth,prefHeight);

        label = new Label("Select Number of Troops");
        label.setFont(Font.font(15));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setPrefSize(prefWidth-20,80);
        label.setWrapText(true);
        this.getChildren().add(label);
        slider = new Slider(1,3,1);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.setOnKeyPressed(event -> {
            if(event.getCode()== KeyCode.ENTER) ok();
            else if(event.getCode()==KeyCode.ESCAPE) cancel();
        });

        this.getChildren().add(slider);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(5,5,5,5));
        Button ok = new Button("OK");
        ok.setOnAction(this::onOK);
        Separator sep = new Separator();
        sep.setVisible(false);
        Button cancel = new Button("Cancel");
        cancel.setOnAction(this::onCancel);
        buttonBox.getChildren().add(ok);
        buttonBox.getChildren().add(sep);
        buttonBox.getChildren().add(cancel);
        this.getChildren().add(buttonBox);
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        slider.requestFocus();
    }

    private void onOK(ActionEvent ae){
        ok();
    }
    private void ok(){
        this.setVisible(false);
        result.troopSelectionResult((int)slider.getValue());
    }
    private void onCancel(ActionEvent ae){
        cancel();
    }
    private void cancel(){
        this.setVisible(false);
        result.troopSelectionResult(-1);
    }

    public void setTitle(String title){
        label.setText(title);
    }

    public void setSliderValues(int min, int max){
        slider.setMax(max);
        slider.setMin(min);
        slider.setValue(min);
    }

}