package main;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main extends Application {
    private static final int LINEWIDTH=3;
    private static final int SCREENWIDTH=1300;
    private static final int SCREENHEIGHT=600;
    Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Pane world = new Pane();
        Pane root = new Pane();
        Pane seaConnections = new Pane();
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE,null,null)));

        FileReader fr = new FileReader("C:/Users/Paul/Dropbox/Informatik/2015-WS-PK/Abschlussbeispiel/world.map");
        BufferedReader br = new BufferedReader(fr);
        String zeile;

        DataSystem data = DataSystem.getInstance();
        Map<String,Nation> Nations = data.getNations();

        while( (zeile = br.readLine())!= null){
            System.out.println(zeile);
            String[] parts = zeile.split(" ");

            int i = 1;
            String name="";
            while(parts[i].matches("[A-Za-z]+")){
                name+=parts[i]+" ";
                i++;
            }
            String nameNoSpace=name.replace(" ","");


            if (parts[0].equals("patch-of")) {
                Nation nation;
                if(Nations.containsKey(nameNoSpace)) {
                    nation = Nations.get(nameNoSpace);
                } else {
                    nation = new Nation(name);
                    Nations.put(nameNoSpace, nation);
                }


                Path path = new Path();
                MoveTo moveTo = new MoveTo();
                moveTo.setX(Double.parseDouble(parts[i]));
                moveTo.setY(Double.parseDouble(parts[i + 1]));
                path.getElements().add(moveTo);
                i = i + 2;

                for (; i < parts.length; i = i + 2) {
                    LineTo line = new LineTo();
                    line.setX(Double.parseDouble(parts[i]));
                    line.setY(Double.parseDouble(parts[i + 1]));
                    path.getElements().add(line);
                }
                path.fillProperty().bind(nation.getColorProperty());
                //path.setFill(Owner.Unowned.color);
                path.setId(nameNoSpace);
                path.setOnMouseClicked(this::mouseClickHandler);
                world.getChildren().add(path);



            } else if (parts[0].equals("capital-of")) {
                int x = Integer.parseInt(parts[i]);
                int y = Integer.parseInt(parts[i + 1]);

                Nation nation = Nations.get(nameNoSpace);
                nation.setCapitalX(x);
                nation.setCapitalY(y);

                Label count = new Label();
                count.setLayoutX(x);
                count.setLayoutY(y);
                count.textProperty().bind(nation.getTruppCounter().asString());
                count.setId(nameNoSpace);
                count.setOnMouseClicked(this::mouseClickHandler);
                world.getChildren().add(count);


            } else if (parts[0].equals("neighbors-of")) {
                Nation nation = Nations.get(nameNoSpace);
                List<String> neighbors = new ArrayList<>();
                i++;
                while (i < parts.length) {
                    String neighbor = "";
                    while (i < parts.length && parts[i].matches("[A-Za-z]+")) {
                        neighbor += parts[i];
                        i++;
                    }
                    // Linien zwischen Hauptstädten, außer bei Alaska und Kamchatka - manuell gezeichnet!
                    if (!(nameNoSpace.equals("Alaska") || nameNoSpace.equals("Kamchatka"))) {
                        seaConnections.getChildren().add(lineBetweenCapitals(Nations.get(nameNoSpace), Nations.get(neighbor)));
                    }
                    neighbors.add(neighbor);
                    i++; //Aufzählungszeichen überspringen
                }
                nation.setNeighbors(neighbors.toArray(new String[neighbors.size()]));
            } else if (parts[0].equals("continent")) {
                List<String> nations = new ArrayList<>();
                int addValue = Integer.parseInt(parts[i]);
                //addValue und ":" überspringen
                i = i + 2;

                while (i < parts.length) {
                    String nation = "";
                    while (i < parts.length && parts[i].matches("[A-Za-z]+")) {
                        nation += parts[i];
                        i++;
                    }
                    nations.add(nation);
                    i++; //Aufzählungszeichen überspringen
                }

                data.getContinents().put(nameNoSpace, new Continent(name, addValue, nations.toArray(new String[nations.size()])));

            } else {
                System.err.println("Unerwartete Zeile: " + zeile);
            }
        }
        br.close();

        Label status = new Label();
        status.textProperty().bind(data.statusProperty());
        status.setLayoutX(400);
        status.setLayoutY(600);
        root.getChildren().add(status);


        //Verbindung zwischen Alaska und Kamchatka
        Line alaskaLine = new Line();
        alaskaLine.setStrokeWidth(LINEWIDTH);
        Nation alaska = Nations.get("Alaska");
        alaskaLine.setStartY(alaska.getCapitalY());
        alaskaLine.setStartX(alaska.getCapitalX());
        alaskaLine.setEndX(0);
        alaskaLine.setEndY(alaska.getCapitalY());
        seaConnections.getChildren().add(alaskaLine);

        Line kamchatkaLine = new Line();
        kamchatkaLine.setStrokeWidth(LINEWIDTH);
        Nation kamchatka = Nations.get("Kamchatka");
        kamchatkaLine.setStartX(kamchatka.getCapitalX());
        kamchatkaLine.setStartY(kamchatka.getCapitalY());
        kamchatkaLine.setEndX(SCREENWIDTH);
        kamchatkaLine.setEndY(kamchatka.getCapitalY());
        seaConnections.getChildren().add(kamchatkaLine);


        root.getChildren().add(seaConnections);
        root.getChildren().add(world);

        Scene scene = new Scene(root,SCREENWIDTH,SCREENHEIGHT);
        primaryStage.setTitle("All those Territories");
        primaryStage.setScene(scene);
        primaryStage.show();

        controller = new Controller();
    }

    private Line lineBetweenCapitals(Nation a, Nation b){
        if(a == null||b==null){
            System.err.println("Cannot draw line between 2 Capitals!");
            return null;
        }
        Line line = new Line();
        line.setStrokeWidth(LINEWIDTH);
        line.setStartX(a.getCapitalX());
        line.setStartY(a.getCapitalY());
        line.setEndX(b.getCapitalX());
        line.setEndY(b.getCapitalY());
        return line;
    }

    private void mouseClickHandler(MouseEvent me){
        controller.clickedOnNation(((Node)me.getSource()).getId());
        /*
        String source = ((Node)me.getSource()).getId();

        DataSystem data = DataSystem.getInstance();
        Nation nation = data.getNations().get(source);
        nation.setOwner(Owner.Player1);
        nation.setTrupps(nation.getTrupps()+1);
        */
    }

    public static void main(String[] args) {
        launch(args);
    }
}
